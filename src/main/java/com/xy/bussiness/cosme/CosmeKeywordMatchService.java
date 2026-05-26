package com.xy.bussiness.cosme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.brand.mybean.SearchBrand;
import com.xy.bussiness.brand.mybatisservice.SearchBrandService;
import com.xy.bussiness.cosme.dto.ConfirmCosmeMatchRequest;
import com.xy.bussiness.cosme.dto.CosmeMatchCandidate;
import com.xy.bussiness.cosme.dto.CosmeMatchItemResult;
import com.xy.bussiness.cosme.dto.CosmeMatchRunResult;
import com.xy.bussiness.cosme.dto.CosmeProduct;
import com.xy.bussiness.cosme.dto.CosmeSearchResult;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CosmeKeywordMatchService {

    /** 用户明确选择不关联 @cosme 时的占位 ID，批量匹配时会跳过 */
    public static final String COSME_SKIPPED_MARKER = "-1";
    /** 批量匹配未找到合适产品时的占位 ID，避免下次批量重复请求 */
    public static final String COSME_NOT_FOUND_MARKER = "-2";

    private static final double AUTO_THRESHOLD = 80D;
    private static final long COSME_REQUEST_INTERVAL_MS = 800L;

    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private RakutenSearchConditionService rakutenSearchConditionService;
    @Autowired
    private CosmeSearchService cosmeSearchService;
    @Autowired
    private SearchBrandService searchBrandService;

    public CosmeMatchRunResult runMatch(CosmeMatchPlatform platform, String brand, boolean onlyMissing, int limit) {
        List<CosmeMatchTarget> targets = listTargets(platform, brand, onlyMissing, limit);
        CosmeMatchRunResult result = new CosmeMatchRunResult();
        Map<String, SearchBrand> brandMap = loadBrandMap();

        for (int i = 0; i < targets.size(); i++) {
            CosmeMatchTarget target = targets.get(i);
            CosmeMatchItemResult itemResult = matchCondition(platform, target, brandMap, true, false);
            if ("not_found".equals(itemResult.getStatus())) {
                applyCosmeInfo(platform, target.getId(), COSME_NOT_FOUND_MARKER, null, null, null);
            }
            result.getItems().add(itemResult);
            result.setProcessed(result.getProcessed() + 1);
            switch (itemResult.getStatus()) {
                case "auto_applied":
                    result.setAutoApplied(result.getAutoApplied() + 1);
                    break;
                case "pending":
                case "pick":
                    result.setPending(result.getPending() + 1);
                    break;
                case "skipped":
                    result.setSkipped(result.getSkipped() + 1);
                    break;
                default:
                    result.setNotFound(result.getNotFound() + 1);
                    break;
            }
            if (i < targets.size() - 1) {
                sleepBetweenRequests();
            }
        }
        return result;
    }

    public CosmeMatchItemResult matchOne(CosmeMatchPlatform platform, Integer conditionId, boolean autoApply) {
        CosmeMatchTarget target = loadTarget(platform, conditionId);
        if (target == null) {
            CosmeMatchItemResult result = new CosmeMatchItemResult();
            result.setConditionId(conditionId);
            result.setStatus("error");
            result.setMessage("关键字不存在");
            return result;
        }
        return matchCondition(platform, target, loadBrandMap(), autoApply, true);
    }

    public String confirmMatch(CosmeMatchPlatform platform, ConfirmCosmeMatchRequest request) {
        if (request == null || request.getConditionId() == null) {
            return "参数无效";
        }
        if (StringUtils.isBlank(request.getCosmeProductId()) && StringUtils.isBlank(request.getCosmeProductUrl())) {
            return "Cosme 产品信息不能为空";
        }
        if (loadTarget(platform, request.getConditionId()) == null) {
            return "关键字不存在";
        }
        applyCosmeInfo(platform, request.getConditionId(),
                request.getCosmeProductId(),
                request.getCosmeProductUrl(),
                request.getCosmeProductName(),
                request.getCosmeProductImage());
        return "已关联 @cosme：" + StringUtils.defaultIfBlank(request.getCosmeProductName(), request.getCosmeProductId());
    }

    public String skipMatch(CosmeMatchPlatform platform, Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (loadTarget(platform, conditionId) == null) {
            return "关键字不存在";
        }
        applyCosmeInfo(platform, conditionId, COSME_SKIPPED_MARKER, null, null, null);
        return "已标记为不关联 @cosme";
    }

    public String resetMatch(CosmeMatchPlatform platform, Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (loadTarget(platform, conditionId) == null) {
            return "关键字不存在";
        }
        applyCosmeInfo(platform, conditionId, null, null, null, null);
        return "已清除 Cosme 关联状态，可重新匹配";
    }

    private List<CosmeMatchTarget> listTargets(CosmeMatchPlatform platform, String brand, boolean onlyMissing, int limit) {
        switch (platform) {
            case YAHOO:
                return listYahooTargets(brand, onlyMissing, limit);
            case RAKUTEN:
                return listRakutenTargets(brand, onlyMissing, limit);
            case MERCARI:
            default:
                return listMercariTargets(brand, onlyMissing, limit);
        }
    }

    private List<CosmeMatchTarget> listMercariTargets(String brand, boolean onlyMissing, int limit) {
        LambdaQueryWrapper<MercariSearchCondition> wrapper = buildMissingWrapper(
                Wrappers.<MercariSearchCondition>lambdaQuery(), onlyMissing,
                MercariSearchCondition::getCosmeProductId);
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(MercariSearchCondition::getBrand, brand.trim());
        }
        wrapper.orderByDesc(MercariSearchCondition::getId);
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return mercariSearchConditionService.list(wrapper).stream()
                .map(this::toTarget)
                .collect(Collectors.toList());
    }

    private List<CosmeMatchTarget> listYahooTargets(String brand, boolean onlyMissing, int limit) {
        LambdaQueryWrapper<YahooSearchCondition> wrapper = buildMissingWrapper(
                Wrappers.<YahooSearchCondition>lambdaQuery(), onlyMissing,
                YahooSearchCondition::getCosmeProductId);
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(YahooSearchCondition::getBrand, brand.trim());
        }
        wrapper.orderByDesc(YahooSearchCondition::getId);
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return yahooSearchConditionService.list(wrapper).stream()
                .map(this::toTarget)
                .collect(Collectors.toList());
    }

    private List<CosmeMatchTarget> listRakutenTargets(String brand, boolean onlyMissing, int limit) {
        LambdaQueryWrapper<RakutenSearchCondition> wrapper = buildMissingWrapper(
                Wrappers.<RakutenSearchCondition>lambdaQuery(), onlyMissing,
                RakutenSearchCondition::getCosmeProductId);
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(RakutenSearchCondition::getBrand, brand.trim());
        }
        wrapper.orderByDesc(RakutenSearchCondition::getId);
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return rakutenSearchConditionService.list(wrapper).stream()
                .map(this::toTarget)
                .collect(Collectors.toList());
    }

    private <T> LambdaQueryWrapper<T> buildMissingWrapper(LambdaQueryWrapper<T> wrapper,
                                                          boolean onlyMissing,
                                                          com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> cosmeIdColumn) {
        if (onlyMissing) {
            wrapper.and(query -> query.isNull(cosmeIdColumn).or().eq(cosmeIdColumn, ""));
        }
        return wrapper;
    }

    private CosmeMatchTarget loadTarget(CosmeMatchPlatform platform, Integer conditionId) {
        if (conditionId == null) {
            return null;
        }
        switch (platform) {
            case YAHOO:
                return toTarget(yahooSearchConditionService.getById(conditionId));
            case RAKUTEN:
                return toTarget(rakutenSearchConditionService.getById(conditionId));
            case MERCARI:
            default:
                return toTarget(mercariSearchConditionService.getById(conditionId));
        }
    }

    private CosmeMatchTarget toTarget(MercariSearchCondition condition) {
        if (condition == null) {
            return null;
        }
        CosmeMatchTarget target = new CosmeMatchTarget();
        target.setId(condition.getId());
        target.setKeyword(condition.getKeyword());
        target.setDescription(condition.getDescription());
        target.setBrand(condition.getBrand());
        return target;
    }

    private CosmeMatchTarget toTarget(YahooSearchCondition condition) {
        if (condition == null) {
            return null;
        }
        CosmeMatchTarget target = new CosmeMatchTarget();
        target.setId(condition.getId());
        target.setKeyword(condition.getKeyword());
        target.setDescription(condition.getDescription());
        target.setBrand(condition.getBrand());
        return target;
    }

    private CosmeMatchTarget toTarget(RakutenSearchCondition condition) {
        if (condition == null) {
            return null;
        }
        CosmeMatchTarget target = new CosmeMatchTarget();
        target.setId(condition.getId());
        target.setKeyword(condition.getKeyword());
        target.setDescription(condition.getDescription());
        target.setBrand(condition.getBrand());
        return target;
    }

    private CosmeMatchItemResult matchCondition(CosmeMatchPlatform platform,
                                                CosmeMatchTarget target,
                                                Map<String, SearchBrand> brandMap,
                                                boolean autoApply,
                                                boolean showAllOnManual) {
        CosmeMatchItemResult result = new CosmeMatchItemResult();
        result.setConditionId(target.getId());
        result.setKeyword(target.getKeyword());
        result.setDescription(target.getDescription());
        result.setBrand(target.getBrand());

        if (StringUtils.isBlank(target.getKeyword())) {
            result.setStatus("skipped");
            result.setMessage("关键字为空");
            return result;
        }

        try {
            Map<String, String> referenceTexts = buildReferenceTexts(target, brandMap);
            List<CosmeProduct> products = searchCosmeProducts(target, brandMap);
            if (CollectionUtils.isEmpty(products)) {
                result.setStatus("not_found");
                result.setMessage("Cosme 搜索无结果");
                return result;
            }

            List<CosmeMatchCandidate> candidates = collectCandidates(products, referenceTexts);
            CosmeMatchCandidate best = candidates.isEmpty() ? null : candidates.get(0);
            CosmeProduct firstProduct = findFirstValidProduct(products);
            CosmeMatchCandidate firstCandidate = firstProduct == null ? null : scoreProduct(firstProduct, referenceTexts);

            if (best != null && best.getSimilarity() > AUTO_THRESHOLD) {
                if (autoApply) {
                    applyCosmeInfo(platform, target.getId(), best.getProductId(), best.getProductUrl(),
                            best.getProductName(), best.getImageUrl());
                }
                result.setBestMatch(best);
                result.setStatus("auto_applied");
                result.setMessage(String.format("关键字相似度 %.1f%%，已自动关联", best.getSimilarity()));
                return result;
            }

            if (firstCandidate == null) {
                result.setStatus("not_found");
                result.setMessage("Cosme 搜索结果无有效产品");
                return result;
            }

            if (showAllOnManual) {
                List<CosmeMatchCandidate> allCandidates = buildAllProductCandidates(products, referenceTexts);
                if (allCandidates.isEmpty()) {
                    result.setStatus("not_found");
                    result.setMessage("Cosme 搜索结果无有效产品");
                    return result;
                }
                result.setShowAllProducts(true);
                result.setBestMatch(allCandidates.get(0));
                result.setReviewCandidates(allCandidates);
                result.setStatus("pick");
                result.setMessage(String.format("最高相似度 %.1f%%（≤80%%），请从 %d 个搜索结果中挑选",
                        best != null ? best.getSimilarity() : firstCandidate.getSimilarity(), allCandidates.size()));
                return result;
            }

            result.setBestMatch(firstCandidate);
            result.setReviewCandidates(Collections.singletonList(firstCandidate));
            result.setStatus("pending");
            result.setMessage(String.format("相似度 %.1f%%（≤80%%），请确认是否关联首个搜索结果",
                    firstCandidate.getSimilarity()));
            return result;
        } catch (Exception e) {
            log.error("Cosme 匹配失败, platform={}, conditionId={}, keyword={}",
                    platform, target.getId(), target.getKeyword(), e);
            result.setStatus("error");
            result.setMessage("匹配失败：" + e.getMessage());
            return result;
        }
    }

    private List<CosmeProduct> searchCosmeProducts(CosmeMatchTarget target, Map<String, SearchBrand> brandMap) {
        LinkedHashSet<String> queries = new LinkedHashSet<>();
        queries.add(TextSimilarityUtil.normalize(target.getKeyword()));

        SearchBrand brand = brandMap.get(StringUtils.lowerCase(StringUtils.trimToEmpty(target.getBrand())));
        if (brand == null && StringUtils.isNotBlank(target.getBrand())) {
            brand = brandMap.get(target.getBrand().trim());
        }
        if (brand != null && StringUtils.isNotBlank(brand.getNameJa())) {
            queries.add(TextSimilarityUtil.normalize(brand.getNameJa()) + " "
                    + TextSimilarityUtil.normalize(target.getKeyword()));
        }

        for (String query : queries) {
            if (StringUtils.isBlank(query)) {
                continue;
            }
            CosmeSearchResult searchResult = cosmeSearchService.search(query, 1, null);
            if (searchResult != null && !CollectionUtils.isEmpty(searchResult.getProducts())) {
                return searchResult.getProducts();
            }
        }
        return new ArrayList<>();
    }

    private List<CosmeMatchCandidate> collectCandidates(List<CosmeProduct> products,
                                                        Map<String, String> referenceTexts) {
        Map<String, CosmeMatchCandidate> candidateMap = new LinkedHashMap<>();
        for (CosmeProduct product : products) {
            if (product == null || StringUtils.isBlank(product.getName())) {
                continue;
            }
            for (Map.Entry<String, String> reference : referenceTexts.entrySet()) {
                if (!isPrimaryReference(reference.getKey())) {
                    continue;
                }
                double score = TextSimilarityUtil.similarity(reference.getValue(), product.getName());
                upsertCandidate(candidateMap, product, reference, score);
            }
        }
        return candidateMap.values().stream()
                .sorted(Comparator.comparingDouble(CosmeMatchCandidate::getSimilarity).reversed())
                .collect(Collectors.toList());
    }

    private List<CosmeMatchCandidate> buildAllProductCandidates(List<CosmeProduct> products,
                                                                Map<String, String> referenceTexts) {
        List<CosmeMatchCandidate> candidates = new ArrayList<>();
        for (CosmeProduct product : products) {
            CosmeMatchCandidate candidate = scoreProduct(product, referenceTexts);
            if (candidate != null) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private CosmeProduct findFirstValidProduct(List<CosmeProduct> products) {
        for (CosmeProduct product : products) {
            if (product != null && StringUtils.isNotBlank(product.getName())) {
                return product;
            }
        }
        return null;
    }

    private CosmeMatchCandidate scoreProduct(CosmeProduct product, Map<String, String> referenceTexts) {
        if (product == null || StringUtils.isBlank(product.getName())) {
            return null;
        }
        CosmeMatchCandidate best = null;
        for (Map.Entry<String, String> reference : referenceTexts.entrySet()) {
            if (!isPrimaryReference(reference.getKey())) {
                continue;
            }
            double score = TextSimilarityUtil.similarity(reference.getValue(), product.getName());
            if (best == null || score > best.getSimilarity()) {
                best = buildCandidate(product, reference, score);
            }
        }
        return best;
    }

    private CosmeMatchCandidate buildCandidate(CosmeProduct product,
                                               Map.Entry<String, String> reference,
                                               double score) {
        CosmeMatchCandidate candidate = new CosmeMatchCandidate();
        candidate.setProductId(product.getProductId());
        candidate.setProductName(product.getName());
        candidate.setProductUrl(product.getProductUrl());
        candidate.setImageUrl(product.getImageUrl());
        candidate.setBrand(product.getBrand());
        candidate.setSimilarity(round(score));
        candidate.setMatchedReference(reference.getValue());
        candidate.setReferenceType(reference.getKey());
        return candidate;
    }

    private void upsertCandidate(Map<String, CosmeMatchCandidate> candidateMap,
                                 CosmeProduct product,
                                 Map.Entry<String, String> reference,
                                 double score) {
        String key = StringUtils.defaultString(product.getProductId(), product.getName());
        CosmeMatchCandidate existing = candidateMap.get(key);
        if (existing != null && existing.getSimilarity() >= score) {
            return;
        }
        CosmeMatchCandidate candidate = new CosmeMatchCandidate();
        candidate.setProductId(product.getProductId());
        candidate.setProductName(product.getName());
        candidate.setProductUrl(product.getProductUrl());
        candidate.setImageUrl(product.getImageUrl());
        candidate.setBrand(product.getBrand());
        candidate.setSimilarity(round(score));
        candidate.setMatchedReference(reference.getValue());
        candidate.setReferenceType(reference.getKey());
        candidateMap.put(key, candidate);
    }

    private boolean isPrimaryReference(String referenceType) {
        if (StringUtils.isBlank(referenceType)) {
            return false;
        }
        return referenceType.startsWith("keyword") || referenceType.startsWith("brand_");
    }

    private Map<String, String> buildReferenceTexts(CosmeMatchTarget target, Map<String, SearchBrand> brandMap) {
        Map<String, String> references = new LinkedHashMap<>();
        String keyword = TextSimilarityUtil.normalize(target.getKeyword());
        if (StringUtils.isBlank(keyword)) {
            return references;
        }

        addReference(references, "keyword", keyword);
        addReference(references, "keyword_compact", TextSimilarityUtil.removeSpaces(keyword));

        SearchBrand brand = brandMap.get(StringUtils.lowerCase(StringUtils.trimToEmpty(target.getBrand())));
        if (brand == null && StringUtils.isNotBlank(target.getBrand())) {
            brand = brandMap.get(target.getBrand().trim());
        }
        if (brand != null && StringUtils.isNotBlank(brand.getNameJa())) {
            String brandJa = TextSimilarityUtil.normalize(brand.getNameJa());
            addReference(references, "brand_keyword", brandJa + " " + keyword);
            addReference(references, "brand_keyword_compact", TextSimilarityUtil.removeSpaces(brandJa + keyword));
            if (!keyword.startsWith(brandJa)) {
                addReference(references, "brand_prefix_keyword", brandJa + keyword);
            }
        }
        return references;
    }

    private void addReference(Map<String, String> references, String type, String value) {
        if (StringUtils.isNotBlank(value)) {
            references.put(type, value.trim());
        }
    }

    private Map<String, SearchBrand> loadBrandMap() {
        Map<String, SearchBrand> brandMap = new LinkedHashMap<>();
        for (SearchBrand brand : searchBrandService.list()) {
            if (StringUtils.isNotBlank(brand.getNameEn())) {
                brandMap.put(brand.getNameEn().trim().toLowerCase(), brand);
                brandMap.put(brand.getNameEn().trim(), brand);
            }
        }
        return brandMap;
    }

    private void applyCosmeInfo(CosmeMatchPlatform platform, Integer conditionId, String productId,
                                String productUrl, String productName, String productImage) {
        String image = normalizeImageUrl(productImage);
        switch (platform) {
            case YAHOO:
                LambdaUpdateWrapper<YahooSearchCondition> yahooWrapper = Wrappers.lambdaUpdate();
                yahooWrapper.eq(YahooSearchCondition::getId, conditionId);
                yahooWrapper.set(YahooSearchCondition::getCosmeProductId, StringUtils.trimToNull(productId));
                yahooWrapper.set(YahooSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(productUrl));
                yahooWrapper.set(YahooSearchCondition::getCosmeProductName, StringUtils.trimToNull(productName));
                yahooWrapper.set(YahooSearchCondition::getCosmeProductImage, image);
                yahooSearchConditionService.update(yahooWrapper);
                break;
            case RAKUTEN:
                LambdaUpdateWrapper<RakutenSearchCondition> rakutenWrapper = Wrappers.lambdaUpdate();
                rakutenWrapper.eq(RakutenSearchCondition::getId, conditionId);
                rakutenWrapper.set(RakutenSearchCondition::getCosmeProductId, StringUtils.trimToNull(productId));
                rakutenWrapper.set(RakutenSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(productUrl));
                rakutenWrapper.set(RakutenSearchCondition::getCosmeProductName, StringUtils.trimToNull(productName));
                rakutenWrapper.set(RakutenSearchCondition::getCosmeProductImage, image);
                rakutenSearchConditionService.update(rakutenWrapper);
                break;
            case MERCARI:
            default:
                LambdaUpdateWrapper<MercariSearchCondition> mercariWrapper = Wrappers.lambdaUpdate();
                mercariWrapper.eq(MercariSearchCondition::getId, conditionId);
                mercariWrapper.set(MercariSearchCondition::getCosmeProductId, StringUtils.trimToNull(productId));
                mercariWrapper.set(MercariSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(productUrl));
                mercariWrapper.set(MercariSearchCondition::getCosmeProductName, StringUtils.trimToNull(productName));
                mercariWrapper.set(MercariSearchCondition::getCosmeProductImage, image);
                mercariSearchConditionService.update(mercariWrapper);
                break;
        }
    }

    private String normalizeImageUrl(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return null;
        }
        return imageUrl.replace("target=70x70", "target=220x220");
    }

    private double round(double value) {
        return Math.round(value * 10D) / 10D;
    }

    private void sleepBetweenRequests() {
        try {
            Thread.sleep(COSME_REQUEST_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
