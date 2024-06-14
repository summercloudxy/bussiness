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

import java.util.*;
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
        Map<String, Long> collect = list.stream().filter(t-> t.getBrand() !=null).collect(Collectors.groupingBy(MercariSearchCondition::getBrand,Collectors.counting()));
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
        List<ItemRecord> interestItem = mercariRestController.getConditionInterestItem(conditionId);
        model.addAttribute("brand","empty");
        model.addAttribute("conditionId",conditionId);
        model.addAttribute("itemList",interestItem);
        return "/mercariitem";
    }

    @GetMapping("/interest/item/brand/{brand}")
    public String getBrandInterestItemPage(Model model,@PathVariable String brand){

        List<ItemRecord> interestItem = mercariRestController.getBrandInterestItem(brand);
        model.addAttribute("itemList",interestItem);
        model.addAttribute("brand",brand);
        model.addAttribute("conditionId","empty");
        return "/mercariitem";
    }



    public List<MercariSearchCondition> getMercariKeyWord(String keyword,String brand){
        LambdaQueryWrapper<MercariSearchCondition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotBlank(keyword),MercariSearchCondition::getDescription,keyword);
        queryWrapper.eq(StringUtils.isNotBlank(brand),MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(queryWrapper);
        return list;
    }
}
