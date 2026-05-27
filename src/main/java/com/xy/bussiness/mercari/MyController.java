package com.xy.bussiness.mercari;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mapper.MercariMapper;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.service.MercariSearchService;
import com.xy.bussiness.rakuten.mybatisservice.RakutenItemRecordService;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.rakuten.service.RakutenService;
import com.xy.bussiness.yahoo.YahooService;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private MercariSearchService mercariSearchService;
    @Autowired
    private YahooService yahooService;
    @Autowired
    private RakutenService rakutenService;

    @GetMapping("mercari/disableSearchCondition")
    public String disableMercariSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return mercariSearchService.disableSearchCondition(conditionId);
    }

    @GetMapping("mercari/enableSearchCondition")
    public String enableMercariSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return mercariSearchService.enableSearchCondition(conditionId);
    }

    @GetMapping("mercari/deleteSearchCondition")
    public String deleteMercariSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return mercariSearchService.deleteSearchCondition(conditionId);
    }

    @GetMapping("mercari/updateDuration")
    public String updateMercariSearchConditionDuration(@RequestParam("conditionId") Integer conditionId,
                                                       @RequestParam("duration") Integer duration) {
        return mercariSearchService.updateSearchConditionDuration(conditionId, duration);
    }

    @PostMapping("mercari/searchCondition/create")
    public String createMercariSearchCondition(@RequestParam String brand,
                                               @RequestParam String description,
                                               @RequestParam String keyword,
                                               @RequestParam(required = false) String enKeyword,
                                               @RequestParam(defaultValue = "60") Integer duration,
                                               @RequestParam(defaultValue = "false") Boolean enable) {
        return mercariSearchService.createSearchCondition(brand, description, keyword, enKeyword, duration, enable);
    }

    @PostMapping("mercari/searchCondition/updateKeywords")
    public String updateMercariSearchConditionKeywords(@RequestParam Integer conditionId,
                                                       @RequestParam String keyword,
                                                       @RequestParam(required = false) String enKeyword) {
        return mercariSearchService.updateSearchConditionKeywords(conditionId, keyword, enKeyword);
    }

    @GetMapping("yahoo/disableSearchCondition")
    public String disableYahooSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return yahooService.disableSearchCondition(conditionId);
    }

    @GetMapping("yahoo/enableSearchCondition")
    public String enableYahooSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return yahooService.enableSearchCondition(conditionId);
    }

    @GetMapping("yahoo/deleteSearchCondition")
    public String deleteYahooSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return yahooService.deleteSearchCondition(conditionId);
    }

    @GetMapping("yahoo/updateDuration")
    public String updateYahooSearchConditionDuration(@RequestParam("conditionId") Integer conditionId,
                                                   @RequestParam("duration") Integer duration) {
        return yahooService.updateSearchConditionDuration(conditionId, duration);
    }

    @PostMapping("yahoo/searchCondition/create")
    public String createYahooSearchCondition(@RequestParam String brand,
                                             @RequestParam String description,
                                             @RequestParam String keyword,
                                             @RequestParam(defaultValue = "120") Integer duration,
                                             @RequestParam(defaultValue = "false") Boolean enable,
                                             @RequestParam(required = false) String searchCategory,
                                             @RequestParam(defaultValue = "100") Integer pageSize) {
        return yahooService.createSearchCondition(brand, description, keyword, duration, enable, searchCategory, pageSize);
    }

    @PostMapping("yahoo/searchCondition/updateKeyword")
    public String updateYahooSearchConditionKeyword(@RequestParam Integer conditionId,
                                                   @RequestParam String keyword) {
        return yahooService.updateSearchConditionKeyword(conditionId, keyword);
    }

    @GetMapping("rakuten/disableSearchCondition")
    public String disableRakutenSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return rakutenService.disableSearchCondition(conditionId);
    }

    @GetMapping("rakuten/enableSearchCondition")
    public String enableRakutenSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return rakutenService.enableSearchCondition(conditionId);
    }

    @GetMapping("rakuten/deleteSearchCondition")
    public String deleteRakutenSearchCondition(@RequestParam("conditionId") Integer conditionId) {
        return rakutenService.deleteSearchCondition(conditionId);
    }

    @GetMapping("rakuten/updateDuration")
    public String updateRakutenSearchConditionDuration(@RequestParam("conditionId") Integer conditionId,
                                                       @RequestParam("duration") Integer duration) {
        return rakutenService.updateSearchConditionDuration(conditionId, duration);
    }

    @PostMapping("rakuten/searchCondition/create")
    public String createRakutenSearchCondition(@RequestParam String brand,
                                               @RequestParam String description,
                                               @RequestParam String keyword,
                                               @RequestParam(defaultValue = "120") Integer duration,
                                               @RequestParam(defaultValue = "false") Boolean enable,
                                               @RequestParam(required = false) String searchCategory,
                                               @RequestParam(defaultValue = "3") Integer maxPageNum,
                                               @RequestParam(defaultValue = "new") String status) {
        return rakutenService.createSearchCondition(brand, description, keyword, duration, enable, searchCategory, maxPageNum, status);
    }

    @PostMapping("rakuten/searchCondition/updateKeyword")
    public String updateRakutenSearchConditionKeyword(@RequestParam Integer conditionId,
                                                      @RequestParam String keyword) {
        return rakutenService.updateSearchConditionKeyword(conditionId, keyword);
    }

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

    @GetMapping("mercari/seller/setInterest")
    public String sellerinterest(@Param("itemId") String itemId, @Param("interest") Integer interest) {
        mercariMapper.setSellerInterest(itemId, interest);

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
