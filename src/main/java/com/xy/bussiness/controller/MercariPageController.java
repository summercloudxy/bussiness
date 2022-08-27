package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MercariPageController {
    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;

    @GetMapping("/mercari")
    public String index(Model model){
        Set<String> mercariBrandList = getMercariBrandList();
        model.addAttribute("brand",mercariBrandList);
        List<MercariSearchCondition> mercariKeyWord = getMercariKeyWord(null, null);
        model.addAttribute("searchConditions",mercariKeyWord);
        return "/mercari";
    }

    public Set<String> getMercariBrandList(){
        List<MercariSearchCondition> list = mercariSearchConditionService.list();
        Map<String, List<MercariSearchCondition>> collect = list.stream().collect(Collectors.groupingBy(MercariSearchCondition::getBrand));
        Set<String> brandSet = collect.keySet();
        return brandSet;
    }

    public List<MercariSearchCondition> getMercariKeyWord(String keyword,String brand){
        LambdaQueryWrapper<MercariSearchCondition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotBlank(keyword),MercariSearchCondition::getDescription,keyword);
        queryWrapper.eq(StringUtils.isNotBlank(brand),MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(queryWrapper);
        return list;
    }
}
