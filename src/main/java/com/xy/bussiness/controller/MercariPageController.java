package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class MercariPageController {
    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    private MercariItemRecordService itemRecordService;
    @Autowired
    private MercariRestController mercariRestController;

    @GetMapping("/mercari")
    public String index(Model model){
        List<String> mercariBrandList = getMercariBrandList();
        model.addAttribute("brand",mercariBrandList);
        List<MercariSearchCondition> mercariKeyWord = mercariRestController.getSearchConditionList(null);
        model.addAttribute("searchConditions",mercariKeyWord);
        return "/mercari";
    }

    public List<String> getMercariBrandList(){
        List<MercariSearchCondition> list = mercariSearchConditionService.list();
        Map<String, Long> collect = list.stream().collect(Collectors.groupingBy(MercariSearchCondition::getBrand,Collectors.counting()));
        Map<String, Long> map = new TreeMap<>();
        map.putAll(collect);
        // 升序比较器
        Comparator<Map.Entry<String, Long>> valueComparator = new Comparator<Map.Entry<String,Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                // TODO Auto-generated method stub
                return o2.getValue().intValue()-o1.getValue().intValue();
            }
        };

        // map转换成list进行排序
        List<Map.Entry<String, Long>> list1 = new ArrayList<Map.Entry<String,Long>>(map.entrySet());

        // 排序
        Collections.sort(list1,valueComparator);
        List<String> brandList = new ArrayList<>();
        brandList.add("全部");
        // 默认情况下，TreeMap对key进行升序排序
        System.out.println("------------map按照value升序排序--------------------");
        for (Map.Entry<String, Long> entry : list1) {
            brandList.add(entry.getKey());
        }

        return brandList;
    }

    @GetMapping("/interest/item/{conditionId}")
    public String getInterestItemPage(Model model,@PathVariable Integer conditionId){
        if (conditionId <0){
            conditionId = null;
        }
        List<ItemRecord> interestItem = getInterestItem(conditionId);
        model.addAttribute("itemList",interestItem);
        return "/mercariitem";
    }


    public List<ItemRecord> getInterestItem(Integer conditionId){
        LambdaQueryWrapper<ItemRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ItemRecord::isInterest,true);
        if (conditionId != null) {
            queryWrapper.eq(ItemRecord::getSearchConditionId, conditionId);
        }
        List<ItemRecord> list = itemRecordService.list(queryWrapper);
        Map<String, ItemRecord> collect = list.stream().collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity(),(a,b)->a));
        return new ArrayList<>(collect.values());
    }

    public List<MercariSearchCondition> getMercariKeyWord(String keyword,String brand){
        LambdaQueryWrapper<MercariSearchCondition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotBlank(keyword),MercariSearchCondition::getDescription,keyword);
        queryWrapper.eq(StringUtils.isNotBlank(brand),MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(queryWrapper);
        return list;
    }
}
