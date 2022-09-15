package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.apibean.ItemData;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.service.DpopService;
import com.xy.bussiness.mercari.service.MercariSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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
    private DpopService dpopService;
    @Autowired
    private MercariSearchService mercariSearchService;

    @GetMapping("/mercari/searchCondition")
    public List<MercariSearchCondition>  getSearchConditionList(String brand){
        if (StringUtils.isBlank(brand) || "全部".equals(brand)){
            return conditionDetail(mercariSearchConditionService.list());
        }
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(wrapper);
        return conditionDetail(list);
    }


    public List<MercariSearchCondition>  conditionDetail(List<MercariSearchCondition> conditionList){
        if (CollectionUtils.isNotEmpty(conditionList)){
            conditionList.forEach(t->{
                String condition = t.getItemCondition();
                if (StringUtils.isNotBlank(condition)) {
                    String[] conditionArray = condition.split(",");
                    t.setConditionList(Arrays.asList(conditionArray));
                }
                String searchCategory = t.getSearchCategory();
                if (StringUtils.isNotBlank(searchCategory)){
                    String[] categoryArray = searchCategory.split(",");
                    t.setCategoryList(Arrays.asList(categoryArray));
                }
            });
        }
        return conditionList;
    }

    @GetMapping("/mercari/clean/item")
    public void cleanItems(){
        List<ItemRecord> list = itemRecordService.list();
        List<ItemRecord> removeList = new ArrayList<>();
        for (ItemRecord item : list){
            try {
                ItemData itemDetail = mercariCrawler.getItemDetail(item.getMercariItemId());
                String status = itemDetail.getStatus();
                if ("cancel".equals(status) || "trading".equals(status) || "sold_out".equals(status)) {
                    removeList.add(item);
                }
            }catch (Exception e){

            }
        }
        if (CollectionUtils.isNotEmpty(removeList)){
            log.info("清除mercari已售出记录{}条",removeList.size());
            itemRecordService.removeBatchByIds(removeList.stream().map(ItemRecord::getMercariItemId).collect(Collectors.toList()));
        }
    }

    @GetMapping("/mercari/item/detail")
    public ItemData getItemDetail(String itemId){
        ItemData itemDetail = mercariCrawler.getItemDetail(itemId);
        return itemDetail;
    }

    @PostMapping("/mercari/dpop")
    public void updateDpop(){
        dpopService.updateDpop();
    }


    @GetMapping("/mercari/dpop")
    public String getDpop(){
       return mercariCrawler.getDpop();
    }


    @GetMapping("/mercari/updateCondition")
    public void updateCondition(String id,String condition,Boolean enable){
        MercariSearchCondition searchCondition = mercariSearchConditionService.getById(id);
        Set<String> conditionSet = new HashSet<>();
        if (StringUtils.isNotBlank(searchCondition.getItemCondition())){
            String[] conditionList = searchCondition.getItemCondition().split(",");
            conditionSet = new HashSet<>(Arrays.asList(conditionList));
        }
        if (enable){
            conditionSet.add(condition);
        }else {
            conditionSet.remove(condition);
        }
        String updateCondition ="";
        if (CollectionUtils.isNotEmpty(conditionSet)){
            updateCondition = org.apache.commons.lang3.StringUtils.join(conditionSet, ",");

        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MercariSearchCondition::getItemCondition,updateCondition);
        wrapper.eq(MercariSearchCondition::getId,id);
        mercariSearchConditionService.update(wrapper);
    }

    @GetMapping("/mercari/updateCategory")
    public void updateCategory(String id,String category,Boolean enable){
        MercariSearchCondition searchCondition = mercariSearchConditionService.getById(id);
        Set<String> categorySet = new HashSet<>();
        if (StringUtils.isNotBlank(searchCondition.getSearchCategory())){
            String[] categoryList = searchCondition.getSearchCategory().split(",");
            categorySet = new HashSet<>(Arrays.asList(categoryList));
        }
        if (enable){
            categorySet.add(category);
        }else {
            categorySet.remove(category);
        }
        String updateCondition ="";
        if (CollectionUtils.isNotEmpty(categorySet)){
            updateCondition = org.apache.commons.lang3.StringUtils.join(categorySet, ",");

        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MercariSearchCondition::getSearchCategory,updateCondition);
        wrapper.eq(MercariSearchCondition::getId,id);
        mercariSearchConditionService.update(wrapper);
    }
}
