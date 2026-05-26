package com.xy.bussiness.cosme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.brand.mybatisservice.SearchBrandService;
import com.xy.bussiness.cosme.dto.AddSearchConditionRequest;
import com.xy.bussiness.cosme.dto.AddSearchConditionResult;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.service.MercariSearchService;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.rakuten.service.RakutenService;
import com.xy.bussiness.yahoo.YahooService;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchConditionAddService {

    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    private RakutenSearchConditionService rakutenSearchConditionService;
    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private MercariSearchService mercariSearchService;
    @Autowired
    private RakutenService rakutenService;
    @Autowired
    private YahooService yahooService;
    @Autowired
    private SearchBrandService searchBrandService;

    public AddSearchConditionResult add(AddSearchConditionRequest request) {
        AddSearchConditionResult result = new AddSearchConditionResult();
        if (request == null || StringUtils.isBlank(request.getKeyword())) {
            result.setSuccess(false);
            result.setMessage("搜索关键字不能为空");
            return result;
        }
        if (!request.isMercari() && !request.isRakuten() && !request.isYahoo()) {
            result.setSuccess(false);
            result.setMessage("请至少选择一个搜索平台");
            return result;
        }
        Integer priceMin = normalizePrice(request.getPriceMin());
        Integer priceMax = normalizePrice(request.getPriceMax());
        if (priceMin != null && priceMax != null && priceMin > priceMax) {
            result.setSuccess(false);
            result.setMessage("价格下限不能大于上限");
            return result;
        }
        String keyword = request.getKeyword().trim();
        String brand = resolveBrand(request);
        int duration = request.getDuration() != null && request.getDuration() > 0 ? request.getDuration() : 120;

        if (request.isMercari()) {
            MercariSearchCondition existing = findMercariByKeyword(keyword);
            if (existing != null) {
                updateMercariOnExisting(existing.getId(), request, priceMin, priceMax);
                result.getWarnings().add("煤炉：关键字已存在，已更新相关信息");
            } else {
                MercariSearchCondition condition = buildMercariCondition(
                        request, keyword, brand, duration, priceMin, priceMax);
                mercariSearchService.registerSearchCondition(condition);
                result.setMercariId(condition.getId());
            }
        }
        if (request.isRakuten()) {
            RakutenSearchCondition existing = findRakutenByKeyword(keyword);
            if (existing != null) {
                updateRakutenOnExisting(existing.getId(), request, priceMin, priceMax);
                result.getWarnings().add("乐天：关键字已存在，已更新相关信息");
            } else {
                RakutenSearchCondition condition = buildRakutenCondition(request, keyword, brand, duration, priceMin, priceMax);
                rakutenService.registerSearchCondition(condition);
                result.setRakutenId(condition.getId());
            }
        }
        if (request.isYahoo()) {
            YahooSearchCondition existing = findYahooByKeyword(keyword);
            if (existing != null) {
                updateYahooOnExisting(existing.getId(), request, priceMin, priceMax);
                result.getWarnings().add("雅虎：关键字已存在，已更新相关信息");
            } else {
                YahooSearchCondition condition = buildYahooCondition(request, keyword, brand, duration, priceMin, priceMax);
                yahooService.registerSearchCondition(condition);
                result.setYahooId(condition.getId());
            }
        }

        boolean added = result.getMercariId() != null || result.getRakutenId() != null || result.getYahooId() != null;
        result.setSuccess(added || !result.getWarnings().isEmpty());
        if (added) {
            result.setMessage("添加成功");
        } else if (!result.getWarnings().isEmpty()) {
            result.setMessage(String.join("；", result.getWarnings()));
        } else {
            result.setMessage("添加失败");
        }
        return result;
    }

    public List<String> listBrands() {
        return searchBrandService.listForCosme().stream()
                .map(com.xy.bussiness.brand.mybean.SearchBrand::getNameEn)
                .collect(Collectors.toList());
    }

    public String suggestBrand(String cosmeBrand, String searchKeyword) {
        return searchBrandService.suggestBrandSlug(cosmeBrand, searchKeyword);
    }

    private String resolveBrand(AddSearchConditionRequest request) {
        if (StringUtils.isNotBlank(request.getBrand())) {
            return request.getBrand().trim();
        }
        return suggestBrand(null, request.getKeyword());
    }

    private MercariSearchCondition buildMercariCondition(AddSearchConditionRequest request, String keyword,
                                                         String brand, int duration,
                                                         Integer priceMin, Integer priceMax) {
        MercariSearchCondition condition = new MercariSearchCondition();
        condition.setKeyword(keyword);
        condition.setDescription(StringUtils.defaultString(request.getDescription(), ""));
        condition.setBrand(brand);
        condition.setDuration(duration);
        condition.setEnable(true);
        condition.setPriceMin(priceMin);
        condition.setPriceMax(priceMax);
        condition.setSearchCategory(joinList(request.getMercariCategories(), "HUAZHUANGPIN"));
        condition.setItemCondition(joinList(request.getMercariConditions(), "QUANXIN,JINQUANXIN"));
        condition.setExcludeKeyword(StringUtils.trimToNull(request.getExcludeKeyword()));
        applyCosmeInfo(condition, request);
        return condition;
    }

    private RakutenSearchCondition buildRakutenCondition(AddSearchConditionRequest request, String keyword,
                                                         String brand, int duration,
                                                         Integer priceMin, Integer priceMax) {
        RakutenSearchCondition condition = new RakutenSearchCondition();
        condition.setKeyword(keyword);
        condition.setDescription(StringUtils.defaultString(request.getDescription(), ""));
        condition.setBrand(brand);
        condition.setDuration(duration);
        condition.setEnable(true);
        condition.setPriceMin(priceMin);
        condition.setPriceMax(priceMax);
        condition.setSearchCategory(StringUtils.trimToNull(request.getRakutenSearchCategory()));
        condition.setMaxPageNum(request.getRakutenMaxPageNum() != null ? request.getRakutenMaxPageNum() : 3);
        condition.setStatus(StringUtils.defaultIfBlank(request.getRakutenStatus(), "new"));
        condition.setExcludeKeyword(StringUtils.trimToNull(request.getExcludeKeyword()));
        applyCosmeInfo(condition, request);
        return condition;
    }

    private YahooSearchCondition buildYahooCondition(AddSearchConditionRequest request, String keyword,
                                                     String brand, int duration,
                                                     Integer priceMin, Integer priceMax) {
        YahooSearchCondition condition = new YahooSearchCondition();
        condition.setKeyword(keyword);
        condition.setDescription(StringUtils.defaultString(request.getDescription(), ""));
        condition.setBrand(brand);
        condition.setDuration(duration);
        condition.setEnable(true);
        condition.setPriceMin(priceMin);
        condition.setPriceMax(priceMax);
        condition.setSearchCategory(StringUtils.trimToNull(request.getYahooSearchCategory()));
        condition.setPageSize(request.getYahooPageSize() != null ? request.getYahooPageSize() : 100);
        condition.setExcludeKeyword(StringUtils.trimToNull(request.getExcludeKeyword()));
        applyCosmeInfo(condition, request);
        return condition;
    }

    private void applyCosmeInfo(MercariSearchCondition condition, AddSearchConditionRequest request) {
        if (!hasCosmeInfo(request)) {
            return;
        }
        condition.setCosmeProductId(StringUtils.trimToNull(request.getCosmeProductId()));
        condition.setCosmeProductUrl(StringUtils.trimToNull(request.getCosmeProductUrl()));
        condition.setCosmeProductName(StringUtils.trimToNull(request.getCosmeProductName()));
        condition.setCosmeProductImage(StringUtils.trimToNull(request.getCosmeProductImage()));
    }

    private void applyCosmeInfo(RakutenSearchCondition condition, AddSearchConditionRequest request) {
        if (!hasCosmeInfo(request)) {
            return;
        }
        condition.setCosmeProductId(StringUtils.trimToNull(request.getCosmeProductId()));
        condition.setCosmeProductUrl(StringUtils.trimToNull(request.getCosmeProductUrl()));
        condition.setCosmeProductName(StringUtils.trimToNull(request.getCosmeProductName()));
    }

    private void applyCosmeInfo(YahooSearchCondition condition, AddSearchConditionRequest request) {
        if (!hasCosmeInfo(request)) {
            return;
        }
        condition.setCosmeProductId(StringUtils.trimToNull(request.getCosmeProductId()));
        condition.setCosmeProductUrl(StringUtils.trimToNull(request.getCosmeProductUrl()));
        condition.setCosmeProductName(StringUtils.trimToNull(request.getCosmeProductName()));
    }

    private boolean hasCosmeInfo(AddSearchConditionRequest request) {
        return StringUtils.isNotBlank(request.getCosmeProductId())
                || StringUtils.isNotBlank(request.getCosmeProductUrl());
    }

    private void updateMercariOnExisting(Integer id, AddSearchConditionRequest request,
                                       Integer priceMin, Integer priceMax) {
        if (!hasCosmeInfo(request) && priceMin == null && priceMax == null) {
            return;
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MercariSearchCondition::getId, id);
        if (hasCosmeInfo(request)) {
            wrapper.set(MercariSearchCondition::getCosmeProductId, StringUtils.trimToNull(request.getCosmeProductId()));
            wrapper.set(MercariSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(request.getCosmeProductUrl()));
            wrapper.set(MercariSearchCondition::getCosmeProductName, StringUtils.trimToNull(request.getCosmeProductName()));
            wrapper.set(MercariSearchCondition::getCosmeProductImage, StringUtils.trimToNull(request.getCosmeProductImage()));
        }
        if (priceMin != null) {
            wrapper.set(MercariSearchCondition::getPriceMin, priceMin);
        }
        if (priceMax != null) {
            wrapper.set(MercariSearchCondition::getPriceMax, priceMax);
        }
        mercariSearchConditionService.update(wrapper);
    }

    private void updateRakutenOnExisting(Integer id, AddSearchConditionRequest request,
                                         Integer priceMin, Integer priceMax) {
        if (!hasCosmeInfo(request) && priceMin == null && priceMax == null) {
            return;
        }
        LambdaUpdateWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RakutenSearchCondition::getId, id);
        if (hasCosmeInfo(request)) {
            wrapper.set(RakutenSearchCondition::getCosmeProductId, StringUtils.trimToNull(request.getCosmeProductId()));
            wrapper.set(RakutenSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(request.getCosmeProductUrl()));
            wrapper.set(RakutenSearchCondition::getCosmeProductName, StringUtils.trimToNull(request.getCosmeProductName()));
        }
        if (priceMin != null) {
            wrapper.set(RakutenSearchCondition::getPriceMin, priceMin);
        }
        if (priceMax != null) {
            wrapper.set(RakutenSearchCondition::getPriceMax, priceMax);
        }
        rakutenSearchConditionService.update(wrapper);
    }

    private void updateYahooOnExisting(Integer id, AddSearchConditionRequest request,
                                       Integer priceMin, Integer priceMax) {
        if (!hasCosmeInfo(request) && priceMin == null && priceMax == null) {
            return;
        }
        LambdaUpdateWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(YahooSearchCondition::getId, id);
        if (hasCosmeInfo(request)) {
            wrapper.set(YahooSearchCondition::getCosmeProductId, StringUtils.trimToNull(request.getCosmeProductId()));
            wrapper.set(YahooSearchCondition::getCosmeProductUrl, StringUtils.trimToNull(request.getCosmeProductUrl()));
            wrapper.set(YahooSearchCondition::getCosmeProductName, StringUtils.trimToNull(request.getCosmeProductName()));
        }
        if (priceMin != null) {
            wrapper.set(YahooSearchCondition::getPriceMin, priceMin);
        }
        if (priceMax != null) {
            wrapper.set(YahooSearchCondition::getPriceMax, priceMax);
        }
        yahooSearchConditionService.update(wrapper);
    }

    private String joinList(List<String> values, String defaultValue) {
        if (CollectionUtils.isEmpty(values)) {
            return defaultValue;
        }
        return String.join(",", values);
    }

    private Integer normalizePrice(Integer price) {
        return price != null && price > 0 ? price : null;
    }

    private MercariSearchCondition findMercariByKeyword(String keyword) {
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MercariSearchCondition::getKeyword, keyword);
        return mercariSearchConditionService.getOne(wrapper, false);
    }

    private RakutenSearchCondition findRakutenByKeyword(String keyword) {
        LambdaQueryWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RakutenSearchCondition::getKeyword, keyword);
        return rakutenSearchConditionService.getOne(wrapper, false);
    }

    private YahooSearchCondition findYahooByKeyword(String keyword) {
        LambdaQueryWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(YahooSearchCondition::getKeyword, keyword);
        return yahooSearchConditionService.getOne(wrapper, false);
    }
}
