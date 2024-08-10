package com.xy.bussiness.rakuten;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.notification.mail.MyMailSender;
import com.xy.bussiness.rakuten.mybatisservice.RakutenItemRecordService;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.rakuten.service.RakutenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xy.bussiness.UrlConstants.SHUNTONG_RAKUTEN_URL;

@Component
@Slf4j
public class RakutenPipeline implements Pipeline {

    @Autowired
    private RakutenService rakutenService;
    @Autowired
    private RakutenItemRecordService rakutenItemRecordService;
    @Autowired
    private MyMailSender mailSender;
    @Value("${notification.host}")
    private String notifyHost;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Boolean empty = resultItems.get("empty");
        if (empty) {
            return;
        }
        List<RakutenItemRecord> items = resultItems.get("items");
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        String uuid = task.getUUID();
        String[] split = uuid.split("-");
        String searchConditionId = split[0];
        String pageNum = split[1];
        RakutenSearchCondition searchCondition = rakutenService.getSearchCondition(searchConditionId);

        try {
            LambdaQueryWrapper<RakutenItemRecord> queryWrapper = Wrappers.lambdaQuery(RakutenItemRecord.class);
            queryWrapper.eq(RakutenItemRecord::getSearchConditionId, searchConditionId);
            List<RakutenItemRecord> oldItemList = rakutenItemRecordService.list(queryWrapper);
            Map<String, RakutenItemRecord> oldItemMap = oldItemList.stream().collect(Collectors.toMap(RakutenItemRecord::getItemId, Function.identity(), (a, b) -> a));
            List<RakutenItemRecord> newItemList = new ArrayList<>();
            List<RakutenItemRecord> priceItemList = new ArrayList<>();
            List<RakutenItemRecord> noticeNewItems = new ArrayList<>();
            List<RakutenItemRecord> excludeNewItems = new ArrayList<>();
            for (RakutenItemRecord item : items) {
                RakutenItemRecord oldItem = oldItemMap.get(item.getItemId());
                item.setSearchConditionId(Integer.valueOf(searchConditionId));
                item.setInterest(false);
                if (oldItem == null) {
                    item.setOriginPrice(item.getCurrentPrice());
                    item.setCreateDate(new Date());
                    newItemList.add(item);
                    LambdaQueryWrapper<RakutenItemRecord> wrappers = Wrappers.lambdaQuery();
                    wrappers.eq(RakutenItemRecord::getItemId, item.getItemId());
                    List<RakutenItemRecord> itemRecordByItemId = rakutenItemRecordService.list(wrappers);
                    if (CollectionUtils.isEmpty(itemRecordByItemId)) {
                        boolean needExclude = false;
                        if (StringUtils.isNotBlank(searchCondition.getExcludeKeyword())) {
                            String[] excludeKeywordList = searchCondition.getExcludeKeyword().split(",");
                            for (String exclude : excludeKeywordList) {
                                if (item.getTitle().contains(exclude)) {
                                    needExclude = true;
                                }
                            }
                        }
                        item.setExclude(needExclude);
                        if (needExclude) {
                            excludeNewItems.add(item);
                        } else {
                            noticeNewItems.add(item);
                        }
                    }
                } else {
                    if (oldItem.isInterest() && oldItem.getCurrentPrice() > item.getCurrentPrice()) {
                        item.setOriginPrice(oldItem.getOriginPrice());
                        item.setId(oldItem.getId());
                        item.setUpdateDate(new Date());
                        priceItemList.add(item);
                    }
                }
            }


            if (!CollectionUtils.isEmpty(noticeNewItems)) {
                boolean sendResult = sendNewMail(searchCondition, noticeNewItems);
                log.info("搜索条件-[{}]有[{}]条上新,推送通知", searchCondition.getDescription(), newItemList.size());
                if (sendResult && !CollectionUtils.isEmpty(newItemList)) {
                    rakutenItemRecordService.saveBatch(newItemList);
                }
            }

            if (!CollectionUtils.isEmpty(priceItemList)) {
                log.info("搜索条件-[{}]降价了,推送通知", searchCondition.getDescription(), priceItemList.size());
                boolean sendResult = sendPriceMail(searchCondition, priceItemList);
                if (sendResult) {
                    for (RakutenItemRecord priceItem : priceItemList) {
                        LambdaUpdateWrapper<RakutenItemRecord> updateWrapper = Wrappers.lambdaUpdate();
                        updateWrapper.eq(RakutenItemRecord::getItemId, priceItem.getItemId());
                        updateWrapper.set(RakutenItemRecord::getCurrentPrice, priceItem.getCurrentPrice());
                        updateWrapper.set(RakutenItemRecord::getUpdateDate, priceItem.getUpdateDate());
                        rakutenItemRecordService.update(updateWrapper);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(excludeNewItems)) {
                rakutenItemRecordService.saveBatch(excludeNewItems);
            }
            Integer currentPageNum = Integer.valueOf(split[1]);

            if (currentPageNum < searchCondition.getMaxPageNum()) {
                rakutenService.addTask(split[0], String.valueOf(currentPageNum + 1));
            }
        } catch (Exception e) {
            log.error("乐天：处理[{}]第[{}]页的数据失败", searchCondition.getDescription(), pageNum, e);
        }

    }


    public boolean sendNewMail(RakutenSearchCondition searchCondition, List<RakutenItemRecord> newItems) throws Exception {
        String description = searchCondition.getDescription();
        return mailSender.send("乐天:" + searchCondition.getBrand() + description + "上新啦", getNewContent(newItems), 0);
    }

    public boolean sendPriceMail(RakutenSearchCondition searchCondition, List<RakutenItemRecord> priceItems) throws Exception {
        return mailSender.send("乐天:" + searchCondition.getBrand() + searchCondition.getDescription() + "的这些商品降价啦", getPriceContent(priceItems), 0);
    }


    public String getNewContent(List<RakutenItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (RakutenItemRecord record : recordList) {

            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='");
            stringBuilder.append(record.getItemUrl());
            stringBuilder.append("'><img src='").append(record.getImageUrl()).append("'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getTitle());
            stringBuilder.append("</div>");



            stringBuilder.append("<div>");
            stringBuilder.append("价格：").append(record.getCurrentPrice());
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            stringBuilder.append(SHUNTONG_RAKUTEN_URL);
            stringBuilder.append(record.getItemId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://" + notifyHost + "/rakuten/setInterest?interest=1&itemId=");
            stringBuilder.append(record.getItemId());
            stringBuilder.append("'>添加关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }


    public String getPriceContent(List<RakutenItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (RakutenItemRecord record : recordList) {
            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='");
            stringBuilder.append(record.getItemUrl());
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
            stringBuilder.append(record.getOriginPrice()).append("->").append(record.getCurrentPrice());

            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");

            stringBuilder.append(SHUNTONG_RAKUTEN_URL);
            stringBuilder.append(record.getItemId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://" + notifyHost + "/rakuten/setInterest?interest=0&itemId=");
            stringBuilder.append(record.getItemId());
            stringBuilder.append("'>不再关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }

}
