package com.xy.bussiness.brand.mybatisservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.bussiness.brand.mapper.SearchBrandMapper;
import com.xy.bussiness.brand.mybean.SearchBrand;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.xy.bussiness.brand.mybatisservice.SearchBrandService;
import com.xy.bussiness.brand.mybean.SearchBrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchBrandServiceImpl extends ServiceImpl<SearchBrandMapper, SearchBrand> implements SearchBrandService {

    private static final String MERCARI_BRANDS_URL = "https://api.mercari.jp/master/v2/datasets/item_brands";

    private static final Map<String, String> KNOWN_JA_NAMES;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("dior", "ディオール");
        map.put("chanel", "シャネル");
        map.put("ysl", "イヴ・サンローラン");
        map.put("lancome", "ランコム");
        map.put("guerlain", "ゲラン");
        map.put("givenchy", "ジバンシイ");
        map.put("elegance", "エレガンス");
        map.put("laduree", "ラデュレ");
        map.put("jill", "ジルスチュアート");
        map.put("kanebo", "カネボウ");
        map.put("kose", "コーセー");
        map.put("decorte", "コスメデコルテ");
        map.put("cpb", "クレ・ド・ポー ボーテ");
        map.put("albion", "アルビオン");
        map.put("mac", "MAC");
        map.put("clarins", "クラランス");
        map.put("maquillage", "マキアージュ");
        map.put("estee", "エスティ ローダー");
        map.put("dolce", "ドルチェ＆ガッバーナ");
        map.put("burberry", "バーバリー");
        map.put("armani", "アルマーニ");
        map.put("chantecaille", "シャンテカイユ");
        map.put("paul", "ポール＆ジョー");
        map.put("rmk", "RMK");
        map.put("资生堂", "資生堂");
        KNOWN_JA_NAMES = Collections.unmodifiableMap(map);
    }

    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    @Qualifier("httpsRestTemplate")
    private RestTemplate restTemplate;

    @PostConstruct
    public void initFromMercariConditions() {
        try {
            syncFromMercariConditions();
        } catch (Exception e) {
            log.warn("启动时同步煤炉品牌失败: {}", e.getMessage());
        }
    }

    @Override
    public List<SearchBrand> listForCosme() {
        syncFromMercariConditions();
        return list().stream()
                .sorted(Comparator.comparing(SearchBrand::getNameEn, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public int syncFromMercariConditions() {
        List<MercariSearchCondition> conditions = mercariSearchConditionService.list();
        Set<String> brandSlugs = conditions.stream()
                .map(MercariSearchCondition::getBrand)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        int added = 0;
        for (String slug : brandSlugs) {
            if (getById(slug) != null) {
                continue;
            }
            SearchBrand brand = new SearchBrand();
            brand.setNameEn(slug);
            brand.setNameJa(resolveJaName(slug));
            brand.setCosmeKeyword(resolveCosmeKeyword(slug, brand.getNameJa()));
            save(brand);
            added++;
        }
        if (added > 0) {
            log.info("从煤炉关键字库同步品牌 {} 个", added);
        }
        return added;
    }

    @Override
    public int syncFromMercariApi() {
        int updated = 0;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "Mozilla/5.0");
            headers.add("Accept", "application/json");
            ResponseEntity<String> response = restTemplate.exchange(
                    MERCARI_BRANDS_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return 0;
            }
            JSONArray items = parseMercariBrandArray(response.getBody());
            if (items == null || items.isEmpty()) {
                return 0;
            }
            Map<String, JSONObject> apiIndex = buildMercariApiIndex(items);
            for (SearchBrand brand : list()) {
                JSONObject matched = findMercariBrand(apiIndex, brand.getNameEn());
                if (matched == null) {
                    continue;
                }
                Integer id = matched.getInteger("id");
                String subName = matched.getString("subName");
                boolean changed = false;
                if (id != null && !id.equals(brand.getMercariBrandId())) {
                    brand.setMercariBrandId(id);
                    changed = true;
                }
                if (StringUtils.isNotBlank(subName) && !subName.equals(brand.getNameJa())) {
                    brand.setNameJa(subName);
                    changed = true;
                }
                if (changed) {
                    updateById(brand);
                    updated++;
                }
            }
            log.info("从煤炉 API 更新品牌 {} 个", updated);
        } catch (Exception e) {
            log.warn("同步煤炉品牌 API 失败: {}", e.getMessage());
        }
        return updated;
    }

    public String resolveJaName(String nameEn) {
        if (StringUtils.isBlank(nameEn)) {
            return "";
        }
        SearchBrand existing = getById(nameEn);
        if (existing != null && StringUtils.isNotBlank(existing.getNameJa())) {
            return existing.getNameJa();
        }
        String known = KNOWN_JA_NAMES.get(nameEn.toLowerCase(Locale.ROOT));
        if (known != null) {
            return known;
        }
        if (containsJapanese(nameEn)) {
            return nameEn;
        }
        return nameEn;
    }

    public String resolveCosmeKeyword(String nameEn, String nameJa) {
        if (StringUtils.isBlank(nameEn)) {
            return nameJa;
        }
        if (containsJapanese(nameEn) || containsJapanese(nameJa)) {
            return StringUtils.defaultIfBlank(nameJa, nameEn);
        }
        return nameEn;
    }

    @Override
    public String suggestBrandSlug(String cosmeBrand, String searchKeyword) {
        if (StringUtils.isNotBlank(cosmeBrand)) {
            SearchBrand byJa = lambdaQuery().eq(SearchBrand::getNameJa, cosmeBrand.trim()).one();
            if (byJa != null) {
                return byJa.getNameEn();
            }
        }
        if (StringUtils.isNotBlank(searchKeyword)) {
            SearchBrand byEn = getById(searchKeyword.trim().toLowerCase(Locale.ROOT));
            if (byEn != null) {
                return byEn.getNameEn();
            }
            SearchBrand byKeyword = lambdaQuery().eq(SearchBrand::getCosmeKeyword, searchKeyword.trim()).one();
            if (byKeyword != null) {
                return byKeyword.getNameEn();
            }
        }
        if (StringUtils.isNotBlank(searchKeyword)) {
            return searchKeyword.trim().toLowerCase(Locale.ROOT);
        }
        return "";
    }

    private JSONArray parseMercariBrandArray(String body) {
        Object parsed = JSON.parse(body);
        if (parsed instanceof JSONArray) {
            return (JSONArray) parsed;
        }
        if (!(parsed instanceof JSONObject)) {
            return null;
        }
        JSONObject root = (JSONObject) parsed;
        for (String key : Arrays.asList("itemBrands", "item_brands", "data", "brands")) {
            JSONArray array = root.getJSONArray(key);
            if (array != null && !array.isEmpty()) {
                return array;
            }
        }
        return null;
    }

    private Map<String, JSONObject> buildMercariApiIndex(JSONArray items) {
        Map<String, JSONObject> index = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item == null) {
                continue;
            }
            putApiIndex(index, item.getString("name"), item);
            putApiIndex(index, item.getString("subName"), item);
        }
        return index;
    }

    private void putApiIndex(Map<String, JSONObject> index, String key, JSONObject item) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        index.put(normalizeBrandKey(key), item);
    }

    private JSONObject findMercariBrand(Map<String, JSONObject> index, String nameEn) {
        JSONObject direct = index.get(normalizeBrandKey(nameEn));
        if (direct != null) {
            return direct;
        }
        return index.get(normalizeBrandKey(nameEn.toLowerCase(Locale.ROOT)));
    }

    private String normalizeBrandKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    private boolean containsJapanese(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0x3040 && c <= 0x30FF) {
                return true;
            }
            if (c >= 0x4E00 && c <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }
}
