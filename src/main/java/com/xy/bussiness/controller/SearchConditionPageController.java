package com.xy.bussiness.controller;

import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SearchConditionPageController {

    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private RakutenSearchConditionService rakutenSearchConditionService;
    @Autowired
    private YahooRestController yahooRestController;
    @Autowired
    private RakutenRestController rakutenRestController;

    @GetMapping("/yahoo")
    public String yahooIndex(Model model) {
        model.addAttribute("brand", listBrands(yahooSearchConditionService.list()));
        return "/yahoo";
    }

    @GetMapping("/rakuten")
    public String rakutenIndex(Model model) {
        model.addAttribute("brand", listBrands(rakutenSearchConditionService.list()));
        return "/rakuten";
    }

    @GetMapping("/yahoo/interest/item/{conditionId}")
    public String yahooInterest(Model model, @PathVariable Integer conditionId) {
        model.addAttribute("brand", "empty");
        model.addAttribute("conditionId", conditionId);
        model.addAttribute("itemList", yahooRestController.getConditionInterest(conditionId));
        model.addAttribute("pageTitle", resolveYahooInterestTitle(conditionId, null));
        model.addAttribute("backUrl", "/yahoo");
        return "/yahooitem";
    }

    @GetMapping("/yahoo/interest/item/brand/{brand}")
    public String yahooBrandInterest(Model model, @PathVariable String brand) {
        model.addAttribute("brand", brand);
        model.addAttribute("conditionId", "empty");
        model.addAttribute("itemList", yahooRestController.getBrandInterest(brand));
        model.addAttribute("pageTitle", resolveYahooInterestTitle(null, brand));
        model.addAttribute("backUrl", "/yahoo");
        return "/yahooitem";
    }

    @GetMapping("/rakuten/interest/item/{conditionId}")
    public String rakutenInterest(Model model, @PathVariable Integer conditionId) {
        model.addAttribute("brand", "empty");
        model.addAttribute("conditionId", conditionId);
        model.addAttribute("itemList", rakutenRestController.getConditionInterest(conditionId));
        model.addAttribute("pageTitle", resolveRakutenInterestTitle(conditionId, null));
        model.addAttribute("backUrl", "/rakuten");
        return "/rakutenitem";
    }

    @GetMapping("/rakuten/interest/item/brand/{brand}")
    public String rakutenBrandInterest(Model model, @PathVariable String brand) {
        model.addAttribute("brand", brand);
        model.addAttribute("conditionId", "empty");
        model.addAttribute("itemList", rakutenRestController.getBrandInterest(brand));
        model.addAttribute("pageTitle", resolveRakutenInterestTitle(null, brand));
        model.addAttribute("backUrl", "/rakuten");
        return "/rakutenitem";
    }

    private List<String> listBrands(List<?> conditions) {
        Map<String, Long> count = new HashMap<>();
        for (Object c : conditions) {
            String brand = null;
            if (c instanceof YahooSearchCondition) {
                brand = ((YahooSearchCondition) c).getBrand();
            } else if (c instanceof RakutenSearchCondition) {
                brand = ((RakutenSearchCondition) c).getBrand();
            }
            if (StringUtils.isNotBlank(brand)) {
                count.merge(brand, 1L, Long::sum);
            }
        }
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(count.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        List<String> brands = new ArrayList<>();
        brands.add("全部");
        for (Map.Entry<String, Long> e : sorted) {
            brands.add(e.getKey());
        }
        return brands;
    }

    private String resolveYahooInterestTitle(Integer conditionId, String brand) {
        if (StringUtils.isNotBlank(brand)) {
            return "雅虎已关注 · " + brand;
        }
        if (conditionId != null && conditionId == -1) {
            return "雅虎已关注 · 全部";
        }
        if (conditionId != null) {
            YahooSearchCondition c = yahooSearchConditionService.getById(conditionId);
            if (c != null) {
                return "雅虎已关注 · " + (StringUtils.isNotBlank(c.getDescription()) ? c.getDescription() : c.getKeyword());
            }
        }
        return "雅虎已关注产品";
    }

    private String resolveRakutenInterestTitle(Integer conditionId, String brand) {
        if (StringUtils.isNotBlank(brand)) {
            return "乐天已关注 · " + brand;
        }
        if (conditionId != null && conditionId == -1) {
            return "乐天已关注 · 全部";
        }
        if (conditionId != null) {
            RakutenSearchCondition c = rakutenSearchConditionService.getById(conditionId);
            if (c != null) {
                return "乐天已关注 · " + (StringUtils.isNotBlank(c.getDescription()) ? c.getDescription() : c.getKeyword());
            }
        }
        return "乐天已关注产品";
    }
}
