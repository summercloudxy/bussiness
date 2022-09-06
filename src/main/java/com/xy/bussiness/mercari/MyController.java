package com.xy.bussiness.mercari;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mapper.MercariMapper;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.rakuten.mybatisservice.RakutenItemRecordService;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MyController {
    @Autowired
    private MercariMapper mercariMapper;
    @Autowired
    private YahooItemRecordService yahooItemRecordService;
    @Autowired
    private MercariCrawler mercariCrawler;
    @Autowired
    private RakutenItemRecordService rakutenItemRecordService;
    @Autowired
    private MercariSearchConditionService mercariSearchConditionService;

    @GetMapping("mercari/setInterest")
    public String interest(@Param("itemId") String itemId, @Param("interest") Integer interest) {
        mercariMapper.setInterest(itemId, interest);

        if (interest == 1) {
            log.info("关注产品{}", itemId);
            return "关注成功";
        } else {
            log.info("取消关注产品{}", itemId);
            return "取关成功";
        }
    }

    @GetMapping("yahoo/setInterest")
    public String yahoointerest(@Param("itemId") String itemId, @Param("interest") Integer interest) {
        LambdaUpdateWrapper<YahooItemRecord> lambdaQueryWrapper = Wrappers.lambdaUpdate(YahooItemRecord.class);
        lambdaQueryWrapper.eq(YahooItemRecord::getAuctionId, itemId).set(YahooItemRecord::getInterest, interest == 1);
        yahooItemRecordService.update(lambdaQueryWrapper);
        if (interest == 1) {
            log.info("关注产品{}", itemId);
            return "关注成功";
        } else {
            log.info("取消关注产品{}", itemId);
            return "取关成功";
        }
    }

    @GetMapping("rakuten/setInterest")
    public String rakuteninterest(@Param("itemId") String itemId, @Param("interest") Integer interest) {
        LambdaUpdateWrapper<RakutenItemRecord> lambdaQueryWrapper = Wrappers.lambdaUpdate(RakutenItemRecord.class);
        lambdaQueryWrapper.eq(RakutenItemRecord::getItemId, itemId).set(RakutenItemRecord::isInterest, interest == 1);
        rakutenItemRecordService.update(lambdaQueryWrapper);
        if (interest == 1) {
            log.info("关注产品{}", itemId);
            return "关注成功";
        } else {
            log.info("取消关注产品{}", itemId);
            return "取关成功";
        }
    }




    @GetMapping("/searchCondition/list")
    public List<MercariSearchCondition> getMercariKeyWord(String keyword,String brand){
        LambdaQueryWrapper<MercariSearchCondition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotBlank(keyword),MercariSearchCondition::getDescription,keyword);
        queryWrapper.eq(StringUtils.isNotBlank(brand),MercariSearchCondition::getBrand,brand);
        List<MercariSearchCondition> list = mercariSearchConditionService.list(queryWrapper);
        return list;
    }


    @GetMapping("/brand/list")
    public Set<String> getMercariBrandList(){
        List<MercariSearchCondition> list = mercariSearchConditionService.list();
        Map<String, List<MercariSearchCondition>> collect = list.stream().collect(Collectors.groupingBy(MercariSearchCondition::getBrand));
        Set<String> brandSet = collect.keySet();
        return brandSet;
    }

}
