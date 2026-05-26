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
            "cosmeet\\.cosme\\.net/product/search/srt/\\d+/fw/[^\"/]+/itm/(\\d+)\">([^<]+)<span>\\((\\d+)\\)</span>");
    private static final Pattern CATEGORY_DISABLED_PATTERN = Pattern.compile(
            "<p><span>([^<]+)<span>\\((\\d+)\\)</span></span></p>");
    private static final Pattern CATEGORY_CURRENT_PATTERN = Pattern.compile(
            "class=\"current\">([^<]+)<span>\\((\\d+)\\)</span>");
    private static final Pattern CATEGORY_LV2_SECTION_PATTERN = Pattern.compile(
            "<ul class=\"lv2\">(.*?)</ul>", Pattern.DOTALL);

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
            String html = fetchHtml(url);
            if (StringUtils.isBlank(html)) {
                return result;
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

        Matcher currentMatcher = CATEGORY_CURRENT_PATTERN.matcher(section);
        while (currentMatcher.find()) {
            String nameJa = cleanText(currentMatcher.group(1));
            int count = Integer.parseInt(currentMatcher.group(2));
            if (containsCategory(categories, nameJa)) {
                continue;
            }
            categories.add(buildCategoryFilter(null, nameJa, count, 1, false, selectedCategoryId));
        }

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
            Matcher lv2Matcher = CATEGORY_LV2_SECTION_PATTERN.matcher(section);
            lv2Matcher.region(index, section.length());
            if (lv2Matcher.find()) {
                appendCategoryLinks(categories, section.substring(index, lv2Matcher.start()), selectedCategoryId, 1);
                appendCategoryLinks(categories, lv2Matcher.group(1), selectedCategoryId, 2);
                index = lv2Matcher.end();
            } else {
                appendCategoryLinks(categories, section.substring(index), selectedCategoryId, 1);
                break;
            }
        }
    }

    private void appendCategoryLinks(List<CosmeCategoryFilter> categories, String section,
                                     String selectedCategoryId, int level) {
        Matcher linkMatcher = CATEGORY_LINK_PATTERN.matcher(section);
        while (linkMatcher.find()) {
            String id = linkMatcher.group(1);
            if (containsCategoryId(categories, id)) {
                continue;
            }
            categories.add(buildCategoryFilter(
                    id,
                    linkMatcher.group(2),
                    Integer.parseInt(linkMatcher.group(3)),
                    level,
                    true,
                    selectedCategoryId));
        }
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
