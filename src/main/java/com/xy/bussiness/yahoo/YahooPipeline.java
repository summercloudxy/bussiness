package com.xy.bussiness.yahoo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.notification.mail.MyMailSender;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class YahooPipeline implements Pipeline {

    @Autowired
    private YahooItemRecordService yahooItemRecordService;
    @Autowired
    private YahooService yahooService;
    @Autowired
    private MyMailSender mailSender;

    private Map<String,List<YahooItemRecord>> recordMap = new ConcurrentHashMap<>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        Boolean empty = resultItems.get("empty");
        if (empty) {
            return;
        }
        List<YahooItemRecord> items = resultItems.get("items");
        if (CollectionUtils.isEmpty(items))
        {
            return;
        }
        String uuid = task.getUUID();
        String[] split = uuid.split("-");
        String searchConditionId = split[0];
        String pageNum = split[1];
        YahooSearchCondition searchCondition = yahooService.getSearchCondition(searchConditionId);

        try {
            LambdaQueryWrapper<YahooItemRecord> queryWrapper = Wrappers.lambdaQuery(YahooItemRecord.class);
            queryWrapper.eq(YahooItemRecord::getSearchConditionId, searchConditionId);
            List<YahooItemRecord> oldItemList = yahooItemRecordService.list(queryWrapper);
            Map<String, YahooItemRecord> oldItemMap = oldItemList.stream().collect(Collectors.toMap(YahooItemRecord::getAuctionId, Function.identity(),(a, b)-> a));
            List<YahooItemRecord> newItemList = new ArrayList<>();
            List<YahooItemRecord> priceItemList = new ArrayList<>();
            for (YahooItemRecord item : items) {
                YahooItemRecord oldItem = oldItemMap.get(item.getAuctionId());
                item.setSearchConditionId(Integer.valueOf(searchConditionId));
                item.setInterest(false);
                if (oldItem == null) {
                    newItemList.add(item);
                } else {
                    if (oldItem.getInterest() && oldItem.getAuctionPrice() > item.getAuctionPrice()) {
                        item.setOriginPrice(oldItem.getOriginPrice());
                        item.setId(oldItem.getId());
                        priceItemList.add(item);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(newItemList)){
                log.info("搜索条件-[{}]有[{}]条上新,推送通知", searchCondition.getDescription(), newItemList.size());
                sendNewMail(searchCondition,newItemList);
                yahooItemRecordService.saveBatch(newItemList);

            }
            if (!CollectionUtils.isEmpty(priceItemList)){
                log.info("搜索条件-[{}]降价了,推送通知", searchCondition.getDescription(), priceItemList.size());
                sendPriceMail(searchCondition,priceItemList);

                yahooItemRecordService.updateBatchById(priceItemList);
            }
            yahooService.addTask(split[0], String.valueOf(Integer.valueOf(split[1]) + 1));
        }catch (Exception e){
            log.error("雅虎：处理[{}]第[{}]页的数据失败",searchCondition.getDescription(),pageNum,e);
        }

    }



    public void sendNewMail(YahooSearchCondition searchCondition, List<YahooItemRecord> newItems) throws Exception {
        String description = searchCondition.getDescription();
        mailSender.send("雅虎:"+ searchCondition.getBrand() + description + "上新啦", getNewContent(newItems));
    }

    public void sendPriceMail(YahooSearchCondition searchCondition, List<YahooItemRecord> priceItems) throws Exception {
        mailSender.send("雅虎:" + searchCondition.getBrand() + searchCondition.getDescription() + "的这些商品降价啦", getPriceContent(priceItems));
    }


    public String getNewContent(List<YahooItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (YahooItemRecord record : recordList) {

            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='https://page.auctions.yahoo.co.jp/jp/auction/");
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'><img src='").append(record.getImageUrl()).append("'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getTitle());
            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            if (record.getItemConditionId() != null) {
//                stringBuilder.append(ConditionEnum.getDescriptionById(record.getItemConditionId()));
//            }
//            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("价格：").append(record.getAuctionPrice());
            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Mercari/goodsitem.html?url=");
//            stringBuilder.append(record.getAuctionId());
//            stringBuilder.append("'>点击购买</a>");
//            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://5699805pw3.zicp.fun/yahoo/setInterest?interest=1&itemId=");
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'>添加关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }


    public String getPriceContent(List<YahooItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (YahooItemRecord record : recordList) {
            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='https://page.auctions.yahoo.co.jp/jp/auction/");
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'><img src='").append(record.getImageUrl()).append("'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getTitle());
            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            if (record.getItemConditionId() != null) {
//                stringBuilder.append(ConditionEnum.getDescriptionById(record.getItemConditionId()));
//            }
//            stringBuilder.append("</div>");


            stringBuilder.append("<div>");
            stringBuilder.append(record.getOriginPrice()).append("->").append(record.getAuctionPrice());

            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Mercari/goodsitem.html?url=");
//            stringBuilder.append(record.getMercariItemId());
//            stringBuilder.append("'>点击购买</a>");
//            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://5699805pw3.zicp.fun/yahoo/setInterest?interest=0&itemId=");
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'>不再关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }

}
