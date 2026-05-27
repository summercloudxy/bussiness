package com.xy.bussiness.yahoo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.notification.NotifySender;
import com.xy.bussiness.notification.NotificationLinks;
import com.xy.bussiness.notification.wechat.NotifyImageUrls;
import com.xy.bussiness.notification.wechat.WeChatPlatform;
import com.xy.bussiness.notification.wechat.WeChatMarkdownLinks;
import com.xy.bussiness.notification.wechat.WeChatNewsArticle;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xy.bussiness.UrlConstants.SHUNTONG_YAHOOPP_URL;
import static com.xy.bussiness.UrlConstants.SHUNTONG_YAHOO_URL;

@Component
@Slf4j
public class YahooPipeline implements Pipeline {

    @Autowired
    private YahooItemRecordService yahooItemRecordService;
    @Autowired
    private YahooService yahooService;
    @Autowired
    private NotifySender notifySender;

    @Value("${notification.host}")
    private String notifyHost;

    private Map<String, List<YahooItemRecord>> recordMap = new ConcurrentHashMap<>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        Boolean empty = resultItems.get("empty");
        if (empty) {
            return;
        }
        List<YahooItemRecord> items = resultItems.get("items");
        if (CollectionUtils.isEmpty(items)) {
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
            Map<String, YahooItemRecord> oldItemMap = oldItemList.stream().collect(Collectors.toMap(YahooItemRecord::getAuctionId, Function.identity(), (a, b) -> a));
            List<YahooItemRecord> newItemList = new ArrayList<>();
            List<YahooItemRecord> priceItemList = new ArrayList<>();
            List<YahooItemRecord> excludeItemList = new ArrayList<>();
            for (YahooItemRecord item : items) {
                YahooItemRecord oldItem = oldItemMap.get(item.getAuctionId());
                item.setSearchConditionId(Integer.valueOf(searchConditionId));
                item.setInterest(false);
                if (oldItem == null) {
                    item.setCreateDate(new Date());
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
                        excludeItemList.add(item);
                    } else {
                        newItemList.add(item);
                    }
                } else {
                    if (oldItem.getInterest() && oldItem.getAuctionPrice() > item.getAuctionPrice()) {
                        item.setOriginPrice(oldItem.getOriginPrice());
                        item.setId(oldItem.getId());
                        item.setUpdateDate(new Date());
                        item.setInterest(true);
                        priceItemList.add(item);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(newItemList)) {
                log.info("搜索条件-[{}]有[{}]条上新,推送通知", searchCondition.getDescription(), newItemList.size());
                boolean sendResult = sendNewMail(searchCondition, newItemList);
                if (sendResult) {
                    yahooItemRecordService.saveBatch(newItemList);
                }

            }
            if (!CollectionUtils.isEmpty(priceItemList)) {
                log.info("搜索条件-[{}]降价了,推送通知", searchCondition.getDescription(), priceItemList.size());
                boolean sendResult = sendPriceMail(searchCondition, priceItemList);
                if (sendResult) {
                    yahooItemRecordService.updateBatchById(priceItemList);
                }
            }
            if (!CollectionUtils.isEmpty(excludeItemList)) {
                yahooItemRecordService.saveBatch(excludeItemList);
            }
            yahooService.addTask(split[0], String.valueOf(Integer.valueOf(split[1]) + 1));
        } catch (Exception e) {
            log.error("雅虎：处理[{}]第[{}]页的数据失败", searchCondition.getDescription(), pageNum, e);
        }

    }


    public boolean sendNewMail(YahooSearchCondition searchCondition, List<YahooItemRecord> newItems) throws Exception {
        String description = searchCondition.getDescription();
        String topic = "雅虎:" + searchCondition.getBrand() + description + "上新啦";
        return notifySender.send(topic, getNewContent(searchCondition, newItems), getNewWeChatContent(searchCondition, newItems),
                buildYahooNewsArticles(newItems, true), WeChatPlatform.YAHOO, 0);
    }

    public boolean sendPriceMail(YahooSearchCondition searchCondition, List<YahooItemRecord> priceItems) throws Exception {
        String topic = "雅虎:" + searchCondition.getBrand() + searchCondition.getDescription() + "的这些商品降价啦";
        return notifySender.send(topic, getPriceContent(searchCondition, priceItems), getPriceWeChatContent(searchCondition, priceItems),
                buildYahooNewsArticles(priceItems, false), WeChatPlatform.YAHOO, 0);
    }


    public String getNewContent(YahooSearchCondition searchCondition, List<YahooItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (YahooItemRecord record : recordList) {

            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            if (record.getIsPaypal()) {
                stringBuilder.append("<a href='https://paypayfleamarket.yahoo.co.jp/item/");
            } else {
                stringBuilder.append("<a href='https://page.auctions.yahoo.co.jp/jp/auction/");
            }
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'><img src='").append(record.getImageUrl()).append("'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getTitle());
            stringBuilder.append("</div>");

            if (record.getIsNew()) {
                stringBuilder.append("<div>");
                stringBuilder.append("全新");
                stringBuilder.append("</div>");
            }
            stringBuilder.append("<div>");
            stringBuilder.append("价格：").append(record.getAuctionPrice());
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            if (record.getIsPaypal()) {
                stringBuilder.append(SHUNTONG_YAHOOPP_URL);
            } else {
                stringBuilder.append(SHUNTONG_YAHOO_URL);
            }
            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='").append(NotificationLinks.url(notifyHost,
                    "/yahoo/setInterest?interest=1&itemId=" + record.getAuctionId())).append("'>添加关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        NotificationLinks.appendDisableKeywordMailFooter(stringBuilder, notifyHost, "yahoo",
                searchCondition.getId(), searchCondition.getDescription());
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }


    public String getPriceContent(YahooSearchCondition searchCondition, List<YahooItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (YahooItemRecord record : recordList) {
            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            if (record.getIsPaypal()) {
                stringBuilder.append("<a href='https://paypayfleamarket.yahoo.co.jp/item/");
            } else {
                stringBuilder.append("<a href='https://page.auctions.yahoo.co.jp/jp/auction/");
            }
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
            if (record.getIsNew()) {
                stringBuilder.append("<div>");
                stringBuilder.append("全新");
                stringBuilder.append("</div>");
            }

            stringBuilder.append("<div>");
            stringBuilder.append(record.getOriginPrice()).append("->").append(record.getAuctionPrice());

            stringBuilder.append("</div>");


            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            if (record.getIsPaypal()) {
                stringBuilder.append(SHUNTONG_YAHOOPP_URL);
            } else {
                stringBuilder.append(SHUNTONG_YAHOO_URL);
            }

            stringBuilder.append(record.getAuctionId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='").append(NotificationLinks.url(notifyHost,
                    "/yahoo/setInterest?interest=0&itemId=" + record.getAuctionId())).append("'>不再关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        NotificationLinks.appendDisableKeywordMailFooter(stringBuilder, notifyHost, "yahoo",
                searchCondition.getId(), searchCondition.getDescription());
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }

    private String getNewWeChatContent(YahooSearchCondition searchCondition, List<YahooItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (YahooItemRecord record : recordList) {
            appendYahooItemMarkdown(sb, record, true);
        }
        NotificationLinks.appendDisableKeywordMarkdown(sb, notifyHost, "yahoo",
                searchCondition.getId(), searchCondition.getDescription());
        return sb.toString();
    }

    private String getPriceWeChatContent(YahooSearchCondition searchCondition, List<YahooItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (YahooItemRecord record : recordList) {
            appendYahooItemMarkdown(sb, record, false);
            sb.append("- 价格：").append(record.getOriginPrice()).append(" → ").append(record.getAuctionPrice()).append("\n\n");
        }
        NotificationLinks.appendDisableKeywordMarkdown(sb, notifyHost, "yahoo",
                searchCondition.getId(), searchCondition.getDescription());
        return sb.toString();
    }

    private void appendYahooItemMarkdown(StringBuilder sb, YahooItemRecord record, boolean isNew) {
        sb.append("### ").append(record.getTitle()).append("\n");
        if (record.getIsNew()) {
            sb.append("- 全新\n");
        }
        if (isNew) {
            sb.append("- 价格：").append(record.getAuctionPrice()).append("\n");
        }
        String interest = isNew ? "1" : "0";
        String interestLabel = isNew ? "添加关注" : "不再关注";
        WeChatMarkdownLinks.appendThreeLinks(sb,
                yahooItemUrl(record),
                shuntongUrl(record) + record.getAuctionId(),
                NotificationLinks.url(notifyHost, "/yahoo/setInterest?interest=" + interest
                        + "&itemId=" + record.getAuctionId()),
                interestLabel);
        sb.append("\n");
    }

    private String yahooItemUrl(YahooItemRecord record) {
        if (record.getIsPaypal()) {
            return "https://paypayfleamarket.yahoo.co.jp/item/" + record.getAuctionId();
        }
        return "https://page.auctions.yahoo.co.jp/jp/auction/" + record.getAuctionId();
    }

    private String shuntongUrl(YahooItemRecord record) {
        return record.getIsPaypal() ? SHUNTONG_YAHOOPP_URL : SHUNTONG_YAHOO_URL;
    }

    private List<WeChatNewsArticle> buildYahooNewsArticles(List<YahooItemRecord> recordList, boolean isNew) {
        List<WeChatNewsArticle> articles = new ArrayList<>();
        for (YahooItemRecord record : recordList) {
            String picurl = NotifyImageUrls.pickWeChatPicUrl(record.getImageUrl());
            if (picurl == null) {
                continue;
            }
            StringBuilder desc = new StringBuilder();
            if (record.getIsNew()) {
                desc.append("全新\n");
            }
            if (isNew) {
                desc.append("价格：").append(record.getAuctionPrice()).append("\n");
            } else {
                desc.append("价格：").append(record.getOriginPrice()).append(" → ").append(record.getAuctionPrice()).append("\n");
            }
            desc.append("点击下方消息中的链接操作");
            articles.add(WeChatNewsArticle.builder()
                    .title(record.getTitle())
                    .description(desc.toString())
                    .url(yahooItemUrl(record))
                    .picurl(picurl)
                    .build());
        }
        return articles;
    }

}
