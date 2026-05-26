package com.xy.bussiness.cosme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.cosme.dto.CosmeProduct;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CosmeProductAddedService {

    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    private RakutenSearchConditionService rakutenSearchConditionService;
    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;

    public void markAddedProducts(List<CosmeProduct> products) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        Map<String, CosmeProduct> productById = products.stream()
                .filter(product -> StringUtils.isNotBlank(product.getProductId()))
                .collect(Collectors.toMap(CosmeProduct::getProductId, product -> product, (a, b) -> a));
        if (productById.isEmpty()) {
            return;
        }
        Map<String, String> productIdByName = buildProductIdByName(products);
        List<String> productIds = new ArrayList<>(productById.keySet());
        List<String> productNames = new ArrayList<>(productIdByName.keySet());

        Map<String, Set<String>> mercariKeywords = groupKeywords(
                listMercariMatches(productIds, productNames), productById, productIdByName);
        Map<String, Set<String>> rakutenKeywords = groupKeywords(
                listRakutenMatches(productIds, productNames), productById, productIdByName);
        Map<String, Set<String>> yahooKeywords = groupKeywords(
                listYahooMatches(productIds, productNames), productById, productIdByName);

        for (CosmeProduct product : products) {
            String productId = product.getProductId();
            if (StringUtils.isBlank(productId)) {
                continue;
            }
            List<String> mercariList = toKeywordList(mercariKeywords.get(productId));
            List<String> rakutenList = toKeywordList(rakutenKeywords.get(productId));
            List<String> yahooList = toKeywordList(yahooKeywords.get(productId));

            product.setMercariKeywords(mercariList);
            product.setRakutenKeywords(rakutenList);
            product.setYahooKeywords(yahooList);
            product.setMercariKeywordCount(mercariList.size());
            product.setRakutenKeywordCount(rakutenList.size());
            product.setYahooKeywordCount(yahooList.size());

            Set<String> allKeywords = new LinkedHashSet<>();
            allKeywords.addAll(mercariList);
            allKeywords.addAll(rakutenList);
            allKeywords.addAll(yahooList);
            product.setAddedKeywords(new ArrayList<>(allKeywords));
            product.setKeywordCount(allKeywords.size());
            product.setMercariAdded(!mercariList.isEmpty());
            product.setRakutenAdded(!rakutenList.isEmpty());
            product.setYahooAdded(!yahooList.isEmpty());
            product.setKeywordAdded(!allKeywords.isEmpty());
        }
    }

    private Map<String, String> buildProductIdByName(List<CosmeProduct> products) {
        Map<String, String> productIdByName = new HashMap<>();
        for (CosmeProduct product : products) {
            if (StringUtils.isBlank(product.getName()) || StringUtils.isBlank(product.getProductId())) {
                continue;
            }
            productIdByName.put(product.getName().trim(), product.getProductId());
        }
        return productIdByName;
    }

    private List<String> toKeywordList(Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(keywords);
    }

    private Map<String, Set<String>> groupKeywords(List<MatchRow> rows,
                                                   Map<String, CosmeProduct> productById,
                                                   Map<String, String> productIdByName) {
        Map<String, Set<String>> keywordsByProductId = new HashMap<>();
        for (MatchRow row : rows) {
            String productId = resolveProductId(row, productById, productIdByName);
            if (productId == null || StringUtils.isBlank(row.keyword)) {
                continue;
            }
            keywordsByProductId
                    .computeIfAbsent(productId, key -> new LinkedHashSet<>())
                    .add(row.keyword.trim());
        }
        return keywordsByProductId;
    }

    private String resolveProductId(MatchRow row,
                                    Map<String, CosmeProduct> productById,
                                    Map<String, String> productIdByName) {
        if (StringUtils.isNotBlank(row.cosmeProductId) && productById.containsKey(row.cosmeProductId)) {
            return row.cosmeProductId;
        }
        if (StringUtils.isNotBlank(row.keyword)) {
            return productIdByName.get(row.keyword.trim());
        }
        return null;
    }

    private static class MatchRow {
        private final String cosmeProductId;
        private final String keyword;

        private MatchRow(String cosmeProductId, String keyword) {
            this.cosmeProductId = cosmeProductId;
            this.keyword = keyword;
        }
    }

    private List<MatchRow> listMercariMatches(List<String> productIds, List<String> productNames) {
        if (productIds.isEmpty() && productNames.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.select(MercariSearchCondition::getCosmeProductId, MercariSearchCondition::getKeyword);
        applyMatchFilter(wrapper, productIds, productNames,
                MercariSearchCondition::getCosmeProductId, MercariSearchCondition::getKeyword);
        return mercariSearchConditionService.list(wrapper).stream()
                .map(row -> new MatchRow(row.getCosmeProductId(), row.getKeyword()))
                .collect(Collectors.toList());
    }

    private List<MatchRow> listRakutenMatches(List<String> productIds, List<String> productNames) {
        if (productIds.isEmpty() && productNames.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.select(RakutenSearchCondition::getCosmeProductId, RakutenSearchCondition::getKeyword);
        applyMatchFilter(wrapper, productIds, productNames,
                RakutenSearchCondition::getCosmeProductId, RakutenSearchCondition::getKeyword);
        return rakutenSearchConditionService.list(wrapper).stream()
                .map(row -> new MatchRow(row.getCosmeProductId(), row.getKeyword()))
                .collect(Collectors.toList());
    }

    private List<MatchRow> listYahooMatches(List<String> productIds, List<String> productNames) {
        if (productIds.isEmpty() && productNames.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.select(YahooSearchCondition::getCosmeProductId, YahooSearchCondition::getKeyword);
        applyMatchFilter(wrapper, productIds, productNames,
                YahooSearchCondition::getCosmeProductId, YahooSearchCondition::getKeyword);
        return yahooSearchConditionService.list(wrapper).stream()
                .map(row -> new MatchRow(row.getCosmeProductId(), row.getKeyword()))
                .collect(Collectors.toList());
    }

    private <T> void applyMatchFilter(LambdaQueryWrapper<T> wrapper,
                                      List<String> productIds,
                                      List<String> productNames,
                                      com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> cosmeIdColumn,
                                      com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> keywordColumn) {
        wrapper.and(query -> {
            boolean hasCondition = false;
            if (!productIds.isEmpty()) {
                query.in(cosmeIdColumn, productIds);
                hasCondition = true;
            }
            if (!productNames.isEmpty()) {
                if (hasCondition) {
                    query.or();
                }
                query.in(keywordColumn, productNames);
            }
        });
    }
}
