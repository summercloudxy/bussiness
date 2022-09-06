package com.xy.bussiness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.apibean.ItemData;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @GetMapping("/mercari/searchCondition")
    public List<MercariSearchCondition>  index(String brand){
        if ("全部".equals(brand)){
            return mercariSearchConditionService.list();
        }
        LambdaQueryWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(wrapper);
        return list;
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
}
