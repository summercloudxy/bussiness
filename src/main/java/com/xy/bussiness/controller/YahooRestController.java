package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.UrlConstants;
import com.xy.bussiness.cosme.CosmeKeywordMatchService;
import com.xy.bussiness.cosme.CosmeMatchPlatform;
import com.xy.bussiness.cosme.dto.ConfirmCosmeMatchRequest;
import com.xy.bussiness.cosme.dto.CosmeMatchItemResult;
import com.xy.bussiness.cosme.dto.CosmeMatchRunResult;
import com.xy.bussiness.mercari.dto.PageResult;
import com.xy.bussiness.yahoo.YahooService;
import com.xy.bussiness.yahoo.dto.YahooItemThumb;
import com.xy.bussiness.yahoo.dto.YahooSearchConditionView;
import com.xy.bussiness.yahoo.mapper.YahooItemMapper;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class YahooRestController {

    private static final int LATEST_ITEM_LIMIT = 5;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private YahooItemRecordService yahooItemRecordService;
    @Autowired
    private YahooItemMapper yahooItemMapper;
    @Autowired
    private YahooService yahooService;
    @Autowired
    private CosmeKeywordMatchService cosmeKeywordMatchService;

    @GetMapping("/yahoo/searchCondition/page")
    public PageResult<YahooSearchConditionView> getSearchConditionPage(String brand,
                                                                       Boolean enable,
                                                                       @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") long page,
                                                                       @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") long pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        LambdaQueryWrapper<YahooSearchCondition> countWrapper = buildWrapper(brand, enable);
        long total = yahooSearchConditionService.count(countWrapper);
        long offset = (page - 1) * pageSize;
        LambdaQueryWrapper<YahooSearchCondition> listWrapper = buildWrapper(brand, enable);
        listWrapper.orderByDesc(YahooSearchCondition::getId);
        listWrapper.last("LIMIT " + offset + "," + pageSize);
        List<YahooSearchCondition> records = yahooSearchConditionService.list(listWrapper);
        List<Integer> conditionIds = records.stream().map(YahooSearchCondition::getId).collect(Collectors.toList());
        Map<Integer, List<YahooItemThumb>> latestMap = loadLatestItems(conditionIds);
        List<YahooSearchConditionView> views = records.stream().map(c -> toView(c, latestMap)).collect(Collectors.toList());
        PageResult<YahooSearchConditionView> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setRecords(views);
        return result;
    }

    @GetMapping("/yahoo/item/interest")
    public List<YahooItemRecord> getInterestItems(String brand, String conditionId) {
        List<YahooItemRecord> allItem = new ArrayList<>();
        if (StringUtils.isNotBlank(brand) && !"empty".equals(brand)) {
            allItem = getBrandInterest(brand);
        }
        if (StringUtils.isNotBlank(conditionId) && !"empty".equals(conditionId)) {
            allItem = getConditionInterest(Integer.valueOf(conditionId));
        }
        return allItem;
    }

    public List<YahooItemRecord> getConditionInterest(Integer conditionId) {
        LambdaQueryWrapper<YahooItemRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(YahooItemRecord::getInterest, true);
        if (conditionId != null && conditionId != -1) {
            wrapper.eq(YahooItemRecord::getSearchConditionId, conditionId);
        }
        return yahooItemRecordService.list(wrapper);
    }

    public List<YahooItemRecord> getBrandInterest(String brand) {
        LambdaQueryWrapper<YahooSearchCondition> cw = Wrappers.lambdaQuery();
        cw.eq(YahooSearchCondition::getBrand, brand);
        List<Integer> ids = yahooSearchConditionService.list(cw).stream()
                .map(YahooSearchCondition::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<YahooItemRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(YahooItemRecord::getInterest, true);
        wrapper.in(YahooItemRecord::getSearchConditionId, ids);
        return yahooItemRecordService.list(wrapper);
    }

    private LambdaQueryWrapper<YahooSearchCondition> buildWrapper(String brand, Boolean enable) {
        LambdaQueryWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(YahooSearchCondition::getBrand, brand);
        }
        if (enable != null) {
            wrapper.eq(YahooSearchCondition::isEnable, enable);
        }
        return wrapper;
    }

    private Map<Integer, List<YahooItemThumb>> loadLatestItems(List<Integer> conditionIds) {
        if (CollectionUtils.isEmpty(conditionIds)) {
            return Collections.emptyMap();
        }
        List<YahooItemRecord> items = yahooItemMapper.getLatestItemsByConditionIds(conditionIds, LATEST_ITEM_LIMIT);
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyMap();
        }
        return items.stream().collect(Collectors.groupingBy(
                YahooItemRecord::getSearchConditionId,
                Collectors.mapping(this::toThumb, Collectors.toList())
        ));
    }

    private YahooItemThumb toThumb(YahooItemRecord record) {
        YahooItemThumb thumb = new YahooItemThumb();
        thumb.setItemId(record.getAuctionId());
        thumb.setTitle(record.getTitle());
        thumb.setImageUrl(record.getImageUrl());
        boolean paypal = Boolean.TRUE.equals(record.getIsPaypal());
        thumb.setItemUrl((paypal ? UrlConstants.SHUNTONG_YAHOOPP_URL : UrlConstants.SHUNTONG_YAHOO_URL) + record.getAuctionId());
        return thumb;
    }

    private YahooSearchConditionView toView(YahooSearchCondition source, Map<Integer, List<YahooItemThumb>> latestMap) {
        YahooSearchConditionView view = new YahooSearchConditionView();
        view.setId(source.getId());
        view.setKeyword(source.getKeyword());
        view.setDescription(source.getDescription());
        view.setSearchCategory(source.getSearchCategory());
        view.setPriceMax(source.getPriceMax());
        view.setPriceMin(source.getPriceMin());
        view.setDuration(source.getDuration());
        view.setStartTime(source.getStartTime());
        view.setEndTime(source.getEndTime());
        view.setBrand(source.getBrand());
        view.setEnable(source.isEnable());
        view.setSearchUrl(source.getSearchUrl());
        view.setPageSize(source.getPageSize());
        view.setExcludeKeyword(source.getExcludeKeyword());
        view.setCosmeProductId(source.getCosmeProductId());
        view.setCosmeProductUrl(source.getCosmeProductUrl());
        view.setCosmeProductName(source.getCosmeProductName());
        view.setLatestItems(latestMap.getOrDefault(source.getId(), Collections.emptyList()));
        return view;
    }

    @PostMapping("/yahoo/cosmeMatch/run")
    public CosmeMatchRunResult runCosmeMatch(@RequestParam(required = false) String brand,
                                             @RequestParam(defaultValue = "true") boolean onlyMissing,
                                             @RequestParam(defaultValue = "30") int limit) {
        return cosmeKeywordMatchService.runMatch(CosmeMatchPlatform.YAHOO, brand, onlyMissing, limit);
    }

    @PostMapping("/yahoo/cosmeMatch/matchOne")
    public CosmeMatchItemResult matchCosmeOne(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.matchOne(CosmeMatchPlatform.YAHOO, conditionId, true);
    }

    @PostMapping("/yahoo/cosmeMatch/confirm")
    public String confirmCosmeMatch(@RequestBody ConfirmCosmeMatchRequest request) {
        return cosmeKeywordMatchService.confirmMatch(CosmeMatchPlatform.YAHOO, request);
    }

    @PostMapping("/yahoo/cosmeMatch/skip")
    public String skipCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.skipMatch(CosmeMatchPlatform.YAHOO, conditionId);
    }

    @PostMapping("/yahoo/cosmeMatch/reset")
    public String resetCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.resetMatch(CosmeMatchPlatform.YAHOO, conditionId);
    }
}
