package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.apibean.ItemData;
import com.xy.bussiness.mercari.apibean.ItemsItem;
import com.xy.bussiness.mercari.dto.MercariItemThumb;
import com.xy.bussiness.mercari.dto.MercariSearchConditionView;
import com.xy.bussiness.mercari.dto.PageResult;
import com.xy.bussiness.mercari.mapper.MercariMapper;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.service.MercariSearchService;
import com.xy.bussiness.cosme.CosmeKeywordMatchService;
import com.xy.bussiness.cosme.CosmeMatchPlatform;
import com.xy.bussiness.cosme.dto.ConfirmCosmeMatchRequest;
import com.xy.bussiness.cosme.dto.CosmeMatchItemResult;
import com.xy.bussiness.cosme.dto.CosmeMatchRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MercariRestController {

    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    private MercariItemRecordService itemRecordService;
    @Autowired
    private MercariCrawler mercariCrawler;
    @Autowired
    private MercariSearchService mercariSearchService;
    @Autowired
    private MercariPageController mercariPageController;
    @Autowired
    private MercariMapper mercariMapper;
    @Autowired
    private CosmeKeywordMatchService cosmeKeywordMatchService;

    private static final int LATEST_ITEM_LIMIT = 5;
    private static final int DEFAULT_PAGE_SIZE = 10;


    @GetMapping("/mercari/searchCondition")
    public List<MercariSearchCondition> getSearchConditionList(String brand) {
        if (StringUtils.isBlank(brand) || "全部".equals(brand)) {
            return conditionDetail(mercariSearchConditionService.list());
        }
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MercariSearchCondition::getBrand, brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(wrapper);
        return conditionDetail(list);
    }

    @GetMapping("/mercari/searchCondition/page")
    public PageResult<MercariSearchConditionView> getSearchConditionPage(String brand,
                                                                         Boolean enable,
                                                                         @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") long page,
                                                                         @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") long pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        LambdaQueryWrapper<MercariSearchCondition> countWrapper = buildSearchConditionWrapper(brand, enable);
        long total = mercariSearchConditionService.count(countWrapper);

        long offset = (page - 1) * pageSize;
        LambdaQueryWrapper<MercariSearchCondition> listWrapper = buildSearchConditionWrapper(brand, enable);
        listWrapper.orderByDesc(MercariSearchCondition::getId);
        listWrapper.last("LIMIT " + offset + "," + pageSize);
        List<MercariSearchCondition> records = mercariSearchConditionService.list(listWrapper);

        records = conditionDetail(records);
        List<Integer> conditionIds = records.stream().map(MercariSearchCondition::getId).collect(Collectors.toList());
        Map<Integer, List<MercariItemThumb>> latestItemsMap = loadLatestItems(conditionIds);
        List<MercariSearchConditionView> views = records.stream().map(condition -> {
            MercariSearchConditionView view = new MercariSearchConditionView();
            copyConditionFields(condition, view);
            view.setLatestItems(latestItemsMap.getOrDefault(condition.getId(), Collections.emptyList()));
            return view;
        }).collect(Collectors.toList());
        PageResult<MercariSearchConditionView> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setRecords(views);
        return result;
    }

    private LambdaQueryWrapper<MercariSearchCondition> buildSearchConditionWrapper(String brand, Boolean enable) {
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(MercariSearchCondition::getBrand, brand);
        }
        if (enable != null) {
            wrapper.eq(MercariSearchCondition::isEnable, enable);
        }
        return wrapper;
    }

    private Map<Integer, List<MercariItemThumb>> loadLatestItems(List<Integer> conditionIds) {
        if (CollectionUtils.isEmpty(conditionIds)) {
            return Collections.emptyMap();
        }
        List<ItemRecord> items = mercariMapper.getLatestItemsByConditionIds(conditionIds, LATEST_ITEM_LIMIT);
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyMap();
        }
        return items.stream().collect(Collectors.groupingBy(
                ItemRecord::getSearchConditionId,
                Collectors.mapping(this::toItemThumb, Collectors.toList())
        ));
    }

    private MercariItemThumb toItemThumb(ItemRecord itemRecord) {
        MercariItemThumb thumb = new MercariItemThumb();
        thumb.setMercariItemId(itemRecord.getMercariItemId());
        thumb.setMercariItemTitle(itemRecord.getMercariItemTitle());
        thumb.setImageUrl(MercariItemThumb.buildImageUrl(itemRecord));
        return thumb;
    }

    private void copyConditionFields(MercariSearchCondition source, MercariSearchConditionView target) {
        target.setId(source.getId());
        target.setKeyword(source.getKeyword());
        target.setEnKeyword(source.getEnKeyword());
        target.setDescription(source.getDescription());
        target.setSearchCategory(source.getSearchCategory());
        target.setPriceMax(source.getPriceMax());
        target.setPriceMin(source.getPriceMin());
        target.setDuration(source.getDuration());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setBrand(source.getBrand());
        target.setEnable(source.isEnable());
        target.setItemCondition(source.getItemCondition());
        target.setConditionList(source.getConditionList());
        target.setCategoryList(source.getCategoryList());
        target.setExcludeKeyword(source.getExcludeKeyword());
        target.setExcludeKeywordList(source.getExcludeKeywordList());
        target.setCosmeProductId(source.getCosmeProductId());
        target.setCosmeProductUrl(source.getCosmeProductUrl());
        target.setCosmeProductName(source.getCosmeProductName());
        target.setCosmeProductImage(source.getCosmeProductImage());
    }


    @GetMapping("/mercari/item/interest")
    public List<ItemRecord> getSaleInterestItem(String brand, String conditionId, Boolean saleStatus) {
        List<ItemRecord> allItem = new ArrayList<>();
        if (StringUtils.isNotBlank(brand) && !"empty".equals(brand)) {
            allItem = getBrandInterestItem(brand);
        }
        if (StringUtils.isNotBlank(conditionId) && !"empty".equals(conditionId)) {
            allItem = getConditionInterestItem(Integer.valueOf(conditionId));
        }
        if (saleStatus) {
            allItem = allItem.stream().filter(t -> "on_sale".equals(t.getSoldStatus()) || t.getSoldStatus() == null).collect(Collectors.toList());
        }
        return allItem;
    }


    public List<ItemRecord> getBrandInterestItem(String brand) {
        LambdaQueryWrapper<ItemRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ItemRecord::isInterest, true);
        LambdaQueryWrapper<MercariSearchCondition> searchConditionLambdaQueryWrapper = Wrappers.lambdaQuery();
        searchConditionLambdaQueryWrapper.eq(MercariSearchCondition::getBrand, brand);
        List<MercariSearchCondition> searchConditionList = mercariSearchConditionService.list(searchConditionLambdaQueryWrapper);
        List<Integer> searchConditionIds = searchConditionList.stream().map(MercariSearchCondition::getId).collect(Collectors.toList());
        queryWrapper.in(ItemRecord::getSearchConditionId, searchConditionIds);
        List<ItemRecord> list = itemRecordService.list(queryWrapper);
        Map<String, ItemRecord> collect = list.stream().collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity(), (a, b) -> a));
        return new ArrayList<>(collect.values());
    }


    public List<ItemRecord> getConditionInterestItem(Integer conditionId) {
        LambdaQueryWrapper<ItemRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ItemRecord::isInterest, true);
        if (conditionId != null && conditionId != -1) {
            queryWrapper.eq(ItemRecord::getSearchConditionId, conditionId);
        }
        List<ItemRecord> list = itemRecordService.list(queryWrapper);
        Map<String, ItemRecord> collect = list.stream().collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity(), (a, b) -> a));
        return new ArrayList<>(collect.values());
    }

    public List<MercariSearchCondition> conditionDetail(List<MercariSearchCondition> conditionList) {
        if (CollectionUtils.isNotEmpty(conditionList)) {
            conditionList.forEach(t -> {
                String condition = t.getItemCondition();
                if (StringUtils.isNotBlank(condition)) {
                    String[] conditionArray = condition.split(",");
                    t.setConditionList(Arrays.asList(conditionArray));
                }
                String searchCategory = t.getSearchCategory();
                if (StringUtils.isNotBlank(searchCategory)) {
                    String[] categoryArray = searchCategory.split(",");
                    t.setCategoryList(Arrays.asList(categoryArray));
                }
                String excludeKeyword = t.getExcludeKeyword();
                if (StringUtils.isNotBlank(excludeKeyword)) {
                    String[] excludeKeywordArray = excludeKeyword.split(",");
                    t.setExcludeKeywordList(Arrays.asList(excludeKeywordArray));
                }
            });
        }
        return conditionList;
    }

    @GetMapping("/mercari/update/item")
    /**
     * 更新销售状态
     */
    public void updateItemSoldStatus() throws InterruptedException {
        LambdaQueryWrapper<ItemRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ItemRecord::isInterest, true);

        List<ItemRecord> list = itemRecordService.list(queryWrapper);
        Map<String, ItemRecord> collect = list.stream().filter(t -> t.getSoldStatus() == null || "on_sale".equals(t.getSoldStatus())).collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity(), (a, b) -> a));
        for (ItemRecord item : collect.values()) {
            try {
                ItemData itemDetail = mercariCrawler.getItemDetail(item.getMercariItemId());
                String status = itemDetail.getStatus();
                if (!status.equals(item.getSoldStatus())) {
                    LambdaUpdateWrapper<ItemRecord> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.set(ItemRecord::getSoldStatus, status);
                    updateWrapper.eq(ItemRecord::getMercariItemId, item.getMercariItemId());
                    itemRecordService.update(updateWrapper);
                }
            } catch (Exception e) {

            }
        }

    }


    /**
     * 清理已售出产品
     *
     * @param conditionId
     */
    @GetMapping("/mercari/clean/item")
    public void cleanItems(String conditionId) {
        LambdaQueryWrapper<ItemRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ne(ItemRecord::getSoldStatus, null).ne(ItemRecord::getSoldStatus, "on_sale");
        queryWrapper.eq(ItemRecord::isInterest, false);
        queryWrapper.eq(StringUtils.isNotBlank(conditionId), ItemRecord::getSearchConditionId, conditionId);
        itemRecordService.remove(queryWrapper);
    }


    @GetMapping("/mercari/item/detail")
    public ItemData getItemDetail(String itemId) {
        ItemData itemDetail = mercariCrawler.getItemDetail(itemId);
        return itemDetail;
    }

    @GetMapping("/mercari/test/search")
    public Map<String, Object> testSearch(String keyword) {
        Map<String, Object> result = new LinkedHashMap<>();
        MercariSearchCondition condition = new MercariSearchCondition();
        condition.setKeyword(StringUtils.isBlank(keyword) ? "chanel" : keyword);
        try {
            List<ItemsItem> items = mercariCrawler.getMercariItemsByCondition(condition);
            result.put("success", true);
            result.put("keyword", condition.getKeyword());
            result.put("count", items == null ? 0 : items.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("keyword", condition.getKeyword());
            result.put("error", e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/mercari/updateCondition")
    public void updateCondition(String id, String condition, Boolean enable) {
        MercariSearchCondition searchCondition = mercariSearchConditionService.getById(id);
        Set<String> conditionSet = new HashSet<>();
        if (StringUtils.isNotBlank(searchCondition.getItemCondition())) {
            String[] conditionList = searchCondition.getItemCondition().split(",");
            conditionSet = new HashSet<>(Arrays.asList(conditionList));
        }
        if (enable) {
            conditionSet.add(condition);
        } else {
            conditionSet.remove(condition);
        }
        String updateCondition = "";
        if (CollectionUtils.isNotEmpty(conditionSet)) {
            updateCondition = org.apache.commons.lang3.StringUtils.join(conditionSet, ",");

        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MercariSearchCondition::getItemCondition, updateCondition);
        wrapper.eq(MercariSearchCondition::getId, id);
        mercariSearchConditionService.update(wrapper);
    }

    @GetMapping("/mercari/updateCategory")
    public void updateCategory(String id, String category, Boolean enable) {
        MercariSearchCondition searchCondition = mercariSearchConditionService.getById(id);
        Set<String> categorySet = new HashSet<>();
        if (StringUtils.isNotBlank(searchCondition.getSearchCategory())) {
            String[] categoryList = searchCondition.getSearchCategory().split(",");
            categorySet = new HashSet<>(Arrays.asList(categoryList));
        }
        if (enable) {
            categorySet.add(category);
        } else {
            categorySet.remove(category);
        }
        String updateCondition = "";
        if (CollectionUtils.isNotEmpty(categorySet)) {
            updateCondition = org.apache.commons.lang3.StringUtils.join(categorySet, ",");

        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MercariSearchCondition::getSearchCategory, updateCondition);
        wrapper.eq(MercariSearchCondition::getId, id);
        mercariSearchConditionService.update(wrapper);
    }

    @GetMapping("/mercari/updatePrice")
    public String updatePrice(@RequestParam String id,
                              @RequestParam(required = false) String priceMin,
                              @RequestParam(required = false) String priceMax) {
        Integer min = parsePriceParam(priceMin);
        Integer max = parsePriceParam(priceMax);
        if (min != null && max != null && min > max) {
            return "下限不能大于上限";
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MercariSearchCondition::getPriceMin, min);
        wrapper.set(MercariSearchCondition::getPriceMax, max);
        wrapper.eq(MercariSearchCondition::getId, id);
        mercariSearchConditionService.update(wrapper);
        return "价格区间已保存";
    }

    private Integer parsePriceParam(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            int price = Integer.parseInt(value.trim());
            return price > 0 ? price : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @PostMapping("/mercari/cosmeMatch/run")
    public CosmeMatchRunResult runCosmeMatch(@RequestParam(required = false) String brand,
                                             @RequestParam(defaultValue = "true") boolean onlyMissing,
                                             @RequestParam(defaultValue = "30") int limit) {
        return cosmeKeywordMatchService.runMatch(CosmeMatchPlatform.MERCARI, brand, onlyMissing, limit);
    }

    @PostMapping("/mercari/cosmeMatch/matchOne")
    public CosmeMatchItemResult matchCosmeOne(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.matchOne(CosmeMatchPlatform.MERCARI, conditionId, true);
    }

    @PostMapping("/mercari/cosmeMatch/confirm")
    public String confirmCosmeMatch(@RequestBody ConfirmCosmeMatchRequest request) {
        return cosmeKeywordMatchService.confirmMatch(CosmeMatchPlatform.MERCARI, request);
    }

    @PostMapping("/mercari/cosmeMatch/skip")
    public String skipCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.skipMatch(CosmeMatchPlatform.MERCARI, conditionId);
    }

    @PostMapping("/mercari/cosmeMatch/reset")
    public String resetCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.resetMatch(CosmeMatchPlatform.MERCARI, conditionId);
    }


}
