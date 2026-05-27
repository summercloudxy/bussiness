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
import com.xy.bussiness.rakuten.dto.RakutenItemThumb;
import com.xy.bussiness.rakuten.dto.RakutenSearchConditionView;
import com.xy.bussiness.rakuten.mapper.RakutenItemMapper;
import com.xy.bussiness.rakuten.mybatisservice.RakutenItemRecordService;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RakutenRestController {

    private static final int LATEST_ITEM_LIMIT = 5;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private RakutenSearchConditionService rakutenSearchConditionService;
    @Autowired
    private RakutenItemRecordService rakutenItemRecordService;
    @Autowired
    private RakutenItemMapper rakutenItemMapper;
    @Autowired
    private CosmeKeywordMatchService cosmeKeywordMatchService;

    @GetMapping("/rakuten/searchCondition/page")
    public PageResult<RakutenSearchConditionView> getSearchConditionPage(String brand,
                                                                         Boolean enable,
                                                                         @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") long page,
                                                                         @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") long pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        LambdaQueryWrapper<RakutenSearchCondition> countWrapper = buildWrapper(brand, enable);
        long total = rakutenSearchConditionService.count(countWrapper);
        long offset = (page - 1) * pageSize;
        LambdaQueryWrapper<RakutenSearchCondition> listWrapper = buildWrapper(brand, enable);
        listWrapper.orderByDesc(RakutenSearchCondition::getId);
        listWrapper.last("LIMIT " + offset + "," + pageSize);
        List<RakutenSearchCondition> records = rakutenSearchConditionService.list(listWrapper);
        List<Integer> conditionIds = records.stream().map(RakutenSearchCondition::getId).collect(Collectors.toList());
        Map<Integer, List<RakutenItemThumb>> latestMap = loadLatestItems(conditionIds);
        List<RakutenSearchConditionView> views = records.stream().map(c -> toView(c, latestMap)).collect(Collectors.toList());
        PageResult<RakutenSearchConditionView> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setRecords(views);
        return result;
    }

    @GetMapping("/rakuten/item/interest")
    public List<RakutenItemRecord> getInterestItems(String brand, String conditionId) {
        if (StringUtils.isNotBlank(brand) && !"empty".equals(brand)) {
            return getBrandInterest(brand);
        }
        if (StringUtils.isNotBlank(conditionId) && !"empty".equals(conditionId)) {
            return getConditionInterest(Integer.valueOf(conditionId));
        }
        return Collections.emptyList();
    }

    public List<RakutenItemRecord> getConditionInterest(Integer conditionId) {
        LambdaQueryWrapper<RakutenItemRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RakutenItemRecord::isInterest, true);
        if (conditionId != null && conditionId != -1) {
            wrapper.eq(RakutenItemRecord::getSearchConditionId, conditionId);
        }
        return rakutenItemRecordService.list(wrapper);
    }

    public List<RakutenItemRecord> getBrandInterest(String brand) {
        LambdaQueryWrapper<RakutenSearchCondition> cw = Wrappers.lambdaQuery();
        cw.eq(RakutenSearchCondition::getBrand, brand);
        List<Integer> ids = rakutenSearchConditionService.list(cw).stream()
                .map(RakutenSearchCondition::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RakutenItemRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RakutenItemRecord::isInterest, true);
        wrapper.in(RakutenItemRecord::getSearchConditionId, ids);
        return rakutenItemRecordService.list(wrapper);
    }

    private LambdaQueryWrapper<RakutenSearchCondition> buildWrapper(String brand, Boolean enable) {
        LambdaQueryWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(brand) && !"全部".equals(brand)) {
            wrapper.eq(RakutenSearchCondition::getBrand, brand);
        }
        if (enable != null) {
            wrapper.eq(RakutenSearchCondition::isEnable, enable);
        }
        return wrapper;
    }

    private Map<Integer, List<RakutenItemThumb>> loadLatestItems(List<Integer> conditionIds) {
        if (CollectionUtils.isEmpty(conditionIds)) {
            return Collections.emptyMap();
        }
        List<RakutenItemRecord> items = rakutenItemMapper.getLatestItemsByConditionIds(conditionIds, LATEST_ITEM_LIMIT);
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyMap();
        }
        return items.stream().collect(Collectors.groupingBy(
                RakutenItemRecord::getSearchConditionId,
                Collectors.mapping(this::toThumb, Collectors.toList())
        ));
    }

    private RakutenItemThumb toThumb(RakutenItemRecord record) {
        RakutenItemThumb thumb = new RakutenItemThumb();
        thumb.setItemId(record.getItemId());
        thumb.setTitle(record.getTitle());
        thumb.setImageUrl(record.getImageUrl());
        String url = StringUtils.isNotBlank(record.getItemUrl())
                ? record.getItemUrl()
                : UrlConstants.SHUNTONG_RAKUTEN_URL + record.getItemId();
        thumb.setItemUrl(url);
        return thumb;
    }

    private RakutenSearchConditionView toView(RakutenSearchCondition source, Map<Integer, List<RakutenItemThumb>> latestMap) {
        RakutenSearchConditionView view = new RakutenSearchConditionView();
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
        view.setMaxPageNum(source.getMaxPageNum());
        view.setStatus(source.getStatus());
        view.setExcludeKeyword(source.getExcludeKeyword());
        view.setCosmeProductId(source.getCosmeProductId());
        view.setCosmeProductUrl(source.getCosmeProductUrl());
        view.setCosmeProductName(source.getCosmeProductName());
        view.setLatestItems(latestMap.getOrDefault(source.getId(), Collections.emptyList()));
        return view;
    }

    @PostMapping("/rakuten/cosmeMatch/run")
    public CosmeMatchRunResult runCosmeMatch(@RequestParam(required = false) String brand,
                                             @RequestParam(defaultValue = "true") boolean onlyMissing,
                                             @RequestParam(defaultValue = "30") int limit) {
        return cosmeKeywordMatchService.runMatch(CosmeMatchPlatform.RAKUTEN, brand, onlyMissing, limit);
    }

    @PostMapping("/rakuten/cosmeMatch/matchOne")
    public CosmeMatchItemResult matchCosmeOne(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.matchOne(CosmeMatchPlatform.RAKUTEN, conditionId, true);
    }

    @PostMapping("/rakuten/cosmeMatch/confirm")
    public String confirmCosmeMatch(@RequestBody ConfirmCosmeMatchRequest request) {
        return cosmeKeywordMatchService.confirmMatch(CosmeMatchPlatform.RAKUTEN, request);
    }

    @PostMapping("/rakuten/cosmeMatch/skip")
    public String skipCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.skipMatch(CosmeMatchPlatform.RAKUTEN, conditionId);
    }

    @PostMapping("/rakuten/cosmeMatch/reset")
    public String resetCosmeMatch(@RequestParam Integer conditionId) {
        return cosmeKeywordMatchService.resetMatch(CosmeMatchPlatform.RAKUTEN, conditionId);
    }
}
