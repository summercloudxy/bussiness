package com.xy.bussiness.cosme;

import com.xy.bussiness.cosme.dto.CosmeCategoryFilter;
import com.xy.bussiness.cosme.dto.CosmeProduct;
import com.xy.bussiness.cosme.dto.CosmeSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CosmeSearchService {

    @Autowired
    private CosmeProductAddedService cosmeProductAddedService;

    private static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");
    private static final int PAGE_SIZE = 10;

    public static int getPageSize() {
        return PAGE_SIZE;
    }
    private static final Pattern TOTAL_PATTERN = Pattern.compile("商品検索結果[^0-9]*(\\d+)\\s*件");
    private static final Pattern PRODUCT_BLOCK_PATTERN = Pattern.compile(
            "<div class=\"mdl-pdt-idv clearfix\"[^>]*>(.*?)(?=<div class=\"mdl-pdt-idv|<div id=\"mdl-pager\"|<!-- /mdl-pdt-idv -->|<!-- /cnt-list -->)",
            Pattern.DOTALL);
    private static final Pattern PRODUCT_PATTERN = Pattern.compile(
            "href=\"https://www\\.cosme\\.net/products/(\\d+)/\"[^>]*>([^<]+)</a>");
    private static final Pattern BRAND_PATTERN = Pattern.compile(
            "href=\"https://www\\.cosme\\.net/brands/\\d+/\">([^<]+)</a>");
    private static final Pattern IMAGE_PATTERN = Pattern.compile(
            "<p class=\"pic\">\\s*<img[^>]+src=\"([^\"]+)\"");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");
    private static final Pattern PRICE_PATTERN = Pattern.compile("税込価格：([^｜|<]+)");
    private static final Pattern CATEGORY_SECTION_PATTERN = Pattern.compile(
            "id=\"nrw-ctg\"(.*?)id=\"nrw-efct\"",
            Pattern.DOTALL);
    private static final Pattern CATEGORY_LINK_PATTERN = Pattern.compile(
            "cosmeet\\.cosme\\.net/product/search/srt/\\d+/fw/[^\"/]+/itm/(\\d+)\"[^>]*>([^<]+)<span>\\((\\d+)\\)</span>");
    private static final Pattern CATEGORY_DISABLED_PATTERN = Pattern.compile(
            "<p><span>([^<]+)<span>\\((\\d+)\\)</span></span></p>");
    private static final Pattern CATEGORY_CURRENT_PATTERN = Pattern.compile(
            "class=\"current\">([^<]+)<span>\\((\\d+)\\)</span>");
    private static final String CATEGORY_LV2_OPEN = "<ul class=\"lv2\">";
    private static final String UL_CLOSE = "</ul>";

    public CosmeSearchResult search(String keyword, Integer page, String categoryId) {
        CosmeSearchResult result = new CosmeSearchResult();
        if (StringUtils.isBlank(keyword)) {
            result.setProducts(new ArrayList<>());
            return result;
        }
        int currentPage = page == null || page < 1 ? 1 : page;
        String trimmedKeyword = keyword.trim();
        String trimmedCategoryId = StringUtils.trimToNull(categoryId);
        String url = buildSearchUrl(trimmedKeyword, currentPage, trimmedCategoryId);

        result.setKeyword(trimmedKeyword);
        result.setCategoryId(trimmedCategoryId);
        result.setPage(currentPage);
        result.setPageSize(PAGE_SIZE);
        result.setCosmeUrl(url);
        result.setCategories(defaultCategories(trimmedCategoryId));

        try {
            List<CosmeProduct> products = fetchAndParse(url, result, trimmedCategoryId);
            if (products.isEmpty()) {
                String utf8Url = buildSearchUrlUtf8(trimmedKeyword, currentPage, trimmedCategoryId);
                if (!utf8Url.equals(url)) {
                    products = fetchAndParse(utf8Url, result, trimmedCategoryId);
                    if (!products.isEmpty()) {
                        result.setCosmeUrl(utf8Url);
                    }
                }
            }
            result.setProducts(products);
        } catch (Exception e) {
            log.error("抓取 @cosme 搜索页失败, keyword={}, categoryId={}, page={}",
                    trimmedKeyword, trimmedCategoryId, currentPage, e);
            if (result.getCategories().isEmpty()) {
                result.setCategories(defaultCategories(trimmedCategoryId));
            }
        }
        return result;
    }

    private List<CosmeCategoryFilter> defaultCategories(String selectedCategoryId) {
        List<CosmeCategoryFilter> categories = new ArrayList<>();
        CosmeCategoryFilter all = new CosmeCategoryFilter();
        all.setId("");
        all.setNameJa("すべて");
        all.setNameZh("全部");
        all.setLabel("全部（すべて）");
        all.setSelectable(true);
        all.setSelected(StringUtils.isBlank(selectedCategoryId));
        categories.add(all);
        return categories;
    }

    private List<CosmeCategoryFilter> parseCategories(String html, String selectedCategoryId) {
        List<CosmeCategoryFilter> categories = defaultCategories(selectedCategoryId);
        String section = html;
        Matcher sectionMatcher = CATEGORY_SECTION_PATTERN.matcher(html);
        if (sectionMatcher.find()) {
            section = sectionMatcher.group(1);
        }

        appendCategoriesInDocumentOrder(categories, section, selectedCategoryId);

        if (categories.size() == 1) {
            String fullSection = html;
            Matcher fullSectionMatcher = CATEGORY_SECTION_PATTERN.matcher(html);
            if (fullSectionMatcher.find()) {
                fullSection = fullSectionMatcher.group(1);
            }
            appendCategoriesInDocumentOrder(categories, fullSection, selectedCategoryId);
        }

        Matcher disabledMatcher = CATEGORY_DISABLED_PATTERN.matcher(section);
        while (disabledMatcher.find()) {
            String nameJa = cleanText(disabledMatcher.group(1));
            int count = Integer.parseInt(disabledMatcher.group(2));
            if (containsCategory(categories, nameJa)) {
                continue;
            }
            categories.add(buildCategoryFilter(null, nameJa, count, 1, false, selectedCategoryId));
        }
        return categories;
    }

    private void appendCategoriesInDocumentOrder(List<CosmeCategoryFilter> categories, String section,
                                                 String selectedCategoryId) {
        if (StringUtils.isBlank(section)) {
            return;
        }
        int index = 0;
        while (index < section.length()) {
            int lv2Start = section.indexOf(CATEGORY_LV2_OPEN, index);
            if (lv2Start < 0) {
                appendCategorySegmentInOrder(categories, section.substring(index), selectedCategoryId, 1);
                break;
            }
            appendCategorySegmentInOrder(categories, section.substring(index, lv2Start), selectedCategoryId, 1);
            int contentStart = lv2Start + CATEGORY_LV2_OPEN.length();
            int lv2End = findMatchingUlEnd(section, contentStart);
            if (lv2End < 0) {
                appendCategorySegmentInOrder(categories, section.substring(index), selectedCategoryId, 1);
                break;
            }
            String lv2Content = removeNestedUlBlocks(section.substring(contentStart, lv2End));
            appendCategorySegmentInOrder(categories, lv2Content, selectedCategoryId, 2);
            index = lv2End + UL_CLOSE.length();
        }
    }

    private int findMatchingUlEnd(String html, int contentStart) {
        int depth = 1;
        int pos = contentStart;
        while (pos < html.length() && depth > 0) {
            int nextOpen = html.indexOf("<ul", pos);
            int nextClose = html.indexOf(UL_CLOSE, pos);
            if (nextClose < 0) {
                return -1;
            }
            if (nextOpen >= 0 && nextOpen < nextClose) {
                depth++;
                pos = html.indexOf('>', nextOpen) + 1;
            } else {
                depth--;
                if (depth == 0) {
                    return nextClose;
                }
                pos = nextClose + UL_CLOSE.length();
            }
        }
        return -1;
    }

    private String removeNestedUlBlocks(String html) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < html.length()) {
            int ulStart = html.indexOf("<ul", index);
            if (ulStart < 0) {
                result.append(html.substring(index));
                break;
            }
            result.append(html.substring(index, ulStart));
            int contentStart = html.indexOf('>', ulStart) + 1;
            int ulEnd = findMatchingUlEnd(html, contentStart);
            if (ulEnd < 0) {
                result.append(html.substring(ulStart));
                break;
            }
            index = ulEnd + UL_CLOSE.length();
        }
        return result.toString();
    }

    private void appendCategorySegmentInOrder(List<CosmeCategoryFilter> categories, String segment,
                                              String selectedCategoryId, int level) {
        if (StringUtils.isBlank(segment)) {
            return;
        }
        List<CategoryToken> tokens = new ArrayList<>();
        Matcher currentMatcher = CATEGORY_CURRENT_PATTERN.matcher(segment);
        while (currentMatcher.find()) {
            CategoryToken token = new CategoryToken();
            token.start = currentMatcher.start();
            token.current = true;
            token.nameJa = currentMatcher.group(1);
            token.count = Integer.parseInt(currentMatcher.group(2));
            tokens.add(token);
        }
        Matcher linkMatcher = CATEGORY_LINK_PATTERN.matcher(segment);
        while (linkMatcher.find()) {
            CategoryToken token = new CategoryToken();
            token.start = linkMatcher.start();
            token.current = false;
            token.id = linkMatcher.group(1);
            token.nameJa = linkMatcher.group(2);
            token.count = Integer.parseInt(linkMatcher.group(3));
            tokens.add(token);
        }
        tokens.sort(Comparator.comparingInt(token -> token.start));
        for (CategoryToken token : tokens) {
            if (token.current) {
                String nameJa = cleanText(token.nameJa);
                if (containsCategory(categories, nameJa)) {
                    continue;
                }
                CosmeCategoryFilter filter = buildCategoryFilter(selectedCategoryId, nameJa, token.count, level,
                        true, selectedCategoryId);
                filter.setSelected(true);
                categories.add(filter);
                continue;
            }
            if (containsCategoryId(categories, token.id)) {
                continue;
            }
            categories.add(buildCategoryFilter(token.id, token.nameJa, token.count, level, true,
                    selectedCategoryId));
        }
    }

    private static final class CategoryToken {
        private int start;
        private boolean current;
        private String id;
        private String nameJa;
        private int count;
    }

    private CosmeCategoryFilter buildCategoryFilter(String id, String nameJa, int count, int level,
                                                    boolean selectable, String selectedCategoryId) {
        CosmeCategoryFilter filter = new CosmeCategoryFilter();
        filter.setId(id);
        filter.setNameJa(cleanText(nameJa));
        filter.setNameZh(CosmeCategoryLabels.toChinese(filter.getNameJa()));
        filter.setLabel(CosmeCategoryLabels.toLabel(filter.getNameJa()));
        filter.setCount(count);
        filter.setLevel(level);
        filter.setSelectable(selectable);
        filter.setSelected(StringUtils.isNotBlank(id) && id.equals(selectedCategoryId));
        return filter;
    }

    private boolean containsCategoryId(List<CosmeCategoryFilter> categories, String id) {
        for (CosmeCategoryFilter category : categories) {
            if (id.equals(category.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCategory(List<CosmeCategoryFilter> categories, String nameJa) {
        for (CosmeCategoryFilter category : categories) {
            if (nameJa.equals(category.getNameJa())) {
                return true;
            }
        }
        return false;
    }

    private List<CosmeProduct> fetchAndParse(String url, CosmeSearchResult result, String trimmedCategoryId)
            throws Exception {
        String html = fetchHtml(url);
        if (StringUtils.isBlank(html)) {
            return new ArrayList<>();
        }
        Matcher totalMatcher = TOTAL_PATTERN.matcher(html);
        if (totalMatcher.find()) {
            int total = Integer.parseInt(totalMatcher.group(1));
            result.setTotal(total);
            result.setTotalPages((total + PAGE_SIZE - 1) / PAGE_SIZE);
        }
        result.setCategories(parseCategories(html, trimmedCategoryId));
        List<CosmeProduct> products = parseProducts(html);
        cosmeProductAddedService.markAddedProducts(products);
        return products;
    }

    private String buildSearchUrlUtf8(String keyword, int page, String categoryId) {
        String encodedKeyword = encodeKeywordUtf8(keyword);
        if (StringUtils.isBlank(categoryId)) {
            return "https://cosmeet.cosme.net/product/search?fw=" + encodedKeyword + "&page=" + page;
        }
        StringBuilder url = new StringBuilder("https://cosmeet.cosme.net/product/search/srt/4/fw/")
                .append(encodedKeyword)
                .append("/itm/")
                .append(categoryId);
        if (page > 1) {
            url.append("/page/").append(page);
        }
        return url.toString();
    }

    private String encodeKeywordUtf8(String keyword) {
        try {
            return URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return keyword;
        }
    }

    private String buildSearchUrl(String keyword, int page, String categoryId) {
        String encodedKeyword = encodeKeyword(keyword);
        if (StringUtils.isBlank(categoryId)) {
            return "https://cosmeet.cosme.net/product/search?fw=" + encodedKeyword + "&page=" + page;
        }
        StringBuilder url = new StringBuilder("https://cosmeet.cosme.net/product/search/srt/4/fw/")
                .append(encodedKeyword)
                .append("/itm/")
                .append(categoryId);
        if (page > 1) {
            url.append("/page/").append(page);
        }
        return url.toString();
    }

    private List<CosmeProduct> parseProducts(String html) {
        List<CosmeProduct> products = new ArrayList<>();
        Matcher blockMatcher = PRODUCT_BLOCK_PATTERN.matcher(html);
        while (blockMatcher.find()) {
            String block = blockMatcher.group(1);
            Matcher productMatcher = PRODUCT_PATTERN.matcher(block);
            if (!productMatcher.find()) {
                continue;
            }
            CosmeProduct product = new CosmeProduct();
            product.setProductId(productMatcher.group(1));
            product.setName(cleanText(productMatcher.group(2)));
            product.setProductUrl("https://www.cosme.net/products/" + product.getProductId() + "/");

            Matcher brandMatcher = BRAND_PATTERN.matcher(block);
            if (brandMatcher.find()) {
                product.setBrand(cleanText(brandMatcher.group(1)));
            }
            Matcher imageMatcher = IMAGE_PATTERN.matcher(block);
            if (imageMatcher.find()) {
                product.setImageUrl(normalizeImageUrl(imageMatcher.group(1)));
            }
            Matcher categoryMatcher = CATEGORY_PATTERN.matcher(block);
            if (categoryMatcher.find()) {
                product.setCategory(cleanText(categoryMatcher.group(1)));
            }
            Matcher priceMatcher = PRICE_PATTERN.matcher(block);
            if (priceMatcher.find()) {
                product.setPrice(cleanText(priceMatcher.group(1)));
            }
            products.add(product);
        }
        return products;
    }

    private String normalizeImageUrl(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return imageUrl;
        }
        return imageUrl.replace("target=70x70", "target=220x220");
    }

    private String fetchHtml(String url) throws Exception {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setSocketTimeout(30000)
                .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36");
            request.setHeader("Accept-Language", "ja-JP,ja;q=0.9");
            try (CloseableHttpResponse response = client.execute(request)) {
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                return new String(bytes, SHIFT_JIS);
            }
        }
    }

    private String encodeKeyword(String keyword) {
        try {
            return URLEncoder.encode(keyword, "Shift_JIS");
        } catch (UnsupportedEncodingException e) {
            return keyword;
        }
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
