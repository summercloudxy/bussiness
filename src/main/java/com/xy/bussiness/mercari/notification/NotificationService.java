package com.xy.bussiness.mercari.notification;

import com.xy.bussiness.mercari.constants.ConditionEnum;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import com.xy.bussiness.mercari.mybean.SellerItemRecord;
import com.xy.bussiness.notification.NotifySender;
import com.xy.bussiness.notification.WindowsNotification;
import com.xy.bussiness.notification.wechat.NotifyImageUrls;
import com.xy.bussiness.notification.wechat.WeChatPlatform;
import com.xy.bussiness.notification.wechat.WeChatMarkdownLinks;
import com.xy.bussiness.notification.wechat.WeChatNewsArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xy.bussiness.UrlConstants.JPDELIVER_MEILU_URL;
import static com.xy.bussiness.UrlConstants.SHUNTONG_MEILU_URL;

@Service
public class NotificationService {
    @Autowired
    NotifySender notifySender;
    @Autowired
    WindowsNotification windowsNotification;
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${notification.host}")
    private String notifyHost;

    public boolean sendNew(MercariSearchCondition searchCondition, List<ItemRecord> newItems) throws Exception {
        String description = searchCondition.getDescription();

//        executorService.execute(()->windowsNotification.display("煤炉:" + searchCondition.getBrand() + description + "上新啦",getNewWindowsContent(newItems)));
        String topic = "煤炉:" + searchCondition.getBrand() + description + "上新啦";
        return notifySender.send(topic, getNewMailContent(newItems), getNewWeChatContent(newItems),
                buildMercariNewsArticles(newItems, true), WeChatPlatform.MERCARI, 0);
    }


    public boolean sendPrice(MercariSearchCondition searchCondition, List<ItemRecord> priceItems) throws Exception {
        String topic = "煤炉:" + searchCondition.getBrand() + searchCondition.getDescription() + "的这些商品降价啦";
        return notifySender.send(topic, getPriceMailContent(priceItems), getPriceWeChatContent(priceItems),
                buildMercariNewsArticles(priceItems, false), WeChatPlatform.MERCARI, 0);
    }


    public String getNewWindowsContent(List<ItemRecord> recordList){
        StringBuilder stringBuilder = new StringBuilder();
        for (ItemRecord record:recordList){
            stringBuilder.append(ConditionEnum.getDescriptionById(record.getItemConditionId()));
            stringBuilder.append(":");
            stringBuilder.append(record.getCurrentPrice());
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public String getNewMailContent(List<ItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (ItemRecord record : recordList) {

            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            if ("ITEM_TYPE_MERCARI".equalsIgnoreCase(record.getItemType())) {
                stringBuilder.append("<a href='https://jp.mercari.com/item/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("'><img src='http://static.mercdn.net/c!/w=240,f=webp/thumb/photos/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("_1.jpg'/></a>   ");
            }else {
                stringBuilder.append("<a href='https://jp.mercari.com/shops/product/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("'><img src='");
                stringBuilder.append(record.getImageUrl());
                stringBuilder.append("'/></a>   ");
            }

            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getMercariItemTitle());
            stringBuilder.append("</div>");


            if ("ITEM_TYPE_BEYOND".equalsIgnoreCase(record.getItemType())){
                stringBuilder.append("<div>");
                stringBuilder.append("店铺：").append(record.getSellerId());
                stringBuilder.append("</div>");
            }
            stringBuilder.append("<div>");
            if (record.getItemConditionId() != null) {
                stringBuilder.append(ConditionEnum.getDescriptionById(record.getItemConditionId()));
            }
            stringBuilder.append("</div>");
            stringBuilder.append("<div>");
            stringBuilder.append("价格：").append(record.getCurrentPrice());
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            stringBuilder.append(SHUNTONG_MEILU_URL);
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            stringBuilder.append(JPDELIVER_MEILU_URL);
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>jpDeliver购买</a>");
            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            stringBuilder.append("<a href='");
//            stringBuilder.append(record.getMercariItemId());
//            stringBuilder.append(SHUNTONG_MEILU_URL);
//            stringBuilder.append("'>电脑端购买</a>");
//            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://" + notifyHost + "/mercari/setInterest?interest=1&itemId=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>添加关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }



 public String getPriceMailContent(List<ItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (ItemRecord record : recordList) {
            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");

            if ("ITEM_TYPE_MERCARI".equalsIgnoreCase(record.getItemType())) {
                stringBuilder.append("<a href='https://jp.mercari.com/item/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("'><img src='http://static.mercdn.net/c!/w=240,f=webp/thumb/photos/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("_1.jpg'/></a>   ");
            }else {
                stringBuilder.append("<a href='https://jp.mercari.com/shops/product/");
                stringBuilder.append(record.getMercariItemId());
                stringBuilder.append("'><img src='");
                stringBuilder.append(record.getImageUrl());
                stringBuilder.append("'/></a>   ");
            }
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getMercariItemTitle());
            stringBuilder.append("</div>");
            if ("ITEM_TYPE_BEYOND".equalsIgnoreCase(record.getItemType())){
                stringBuilder.append("<div>");
                stringBuilder.append("店铺");
                stringBuilder.append("</div>");
            }
            stringBuilder.append("<div>");
            if (record.getItemConditionId() != null) {
                stringBuilder.append(ConditionEnum.getDescriptionById(record.getItemConditionId()));
            }
            stringBuilder.append("</div>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getOriginPrice()).append("->").append(record.getCurrentPrice());

            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://meilu.jpshuntong.com/item/");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");


            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            stringBuilder.append(JPDELIVER_MEILU_URL);
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>jpDeliver购买</a>");
            stringBuilder.append("</div>");

//            stringBuilder.append("<div>");
//            stringBuilder.append("<a href='https://meilu.jpshuntong.com/item/");
//            stringBuilder.append(record.getMercariItemId());
//            stringBuilder.append("'>电脑端购买</a>");
//            stringBuilder.append("</div>");


            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://" + notifyHost + "/mercari/setInterest?interest=0&itemId=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>不再关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }

    public String getNewWeChatContent(List<ItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (ItemRecord record : recordList) {
            appendMercariItemMarkdown(sb, record, true);
        }
        return sb.toString();
    }

    public String getPriceWeChatContent(List<ItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (ItemRecord record : recordList) {
            String priceLine = "- 价格：" + record.getOriginPrice() + " → " + record.getCurrentPrice() + "\n";
            appendMercariItemMarkdown(sb, record, false, priceLine);
        }
        return sb.toString();
    }

    private void appendMercariItemMarkdown(StringBuilder sb, ItemRecord record, boolean isNew) {
        appendMercariItemMarkdown(sb, record, isNew, isNew ? "- 价格：" + record.getCurrentPrice() + "\n" : null);
    }

    private void appendMercariItemMarkdown(StringBuilder sb, ItemRecord record, boolean isNew, String priceLine) {
        sb.append("### ").append(record.getMercariItemTitle()).append("\n");
        if ("ITEM_TYPE_BEYOND".equalsIgnoreCase(record.getItemType())) {
            sb.append("- 店铺：").append(record.getSellerId()).append("\n");
        }
        if (record.getItemConditionId() != null) {
            sb.append("- 成色：").append(ConditionEnum.getDescriptionById(record.getItemConditionId())).append("\n");
        }
        if (priceLine != null) {
            sb.append(priceLine);
        }
        String interest = isNew ? "1" : "0";
        String interestLabel = isNew ? "添加关注" : "不再关注";
        WeChatMarkdownLinks.appendThreeLinks(sb,
                mercariItemUrl(record),
                SHUNTONG_MEILU_URL + record.getMercariItemId(),
                "https://" + notifyHost + "/mercari/setInterest?interest=" + interest
                        + "&itemId=" + record.getMercariItemId(),
                interestLabel);
        sb.append("\n");
    }

    private String mercariItemUrl(ItemRecord record) {
        if ("ITEM_TYPE_MERCARI".equalsIgnoreCase(record.getItemType())) {
            return "https://jp.mercari.com/item/" + record.getMercariItemId();
        }
        return "https://jp.mercari.com/shops/product/" + record.getMercariItemId();
    }

    private List<WeChatNewsArticle> buildMercariNewsArticles(List<ItemRecord> recordList, boolean isNew) {
        List<WeChatNewsArticle> articles = new ArrayList<>();
        for (ItemRecord record : recordList) {
            String picurl = mercariPicUrl(record);
            if (picurl == null) {
                continue;
            }
            StringBuilder desc = new StringBuilder();
            if ("ITEM_TYPE_BEYOND".equalsIgnoreCase(record.getItemType())) {
                desc.append("店铺：").append(record.getSellerId()).append("\n");
            }
            if (record.getItemConditionId() != null) {
                desc.append("成色：").append(ConditionEnum.getDescriptionById(record.getItemConditionId())).append("\n");
            }
            if (isNew) {
                desc.append("价格：").append(record.getCurrentPrice()).append("\n");
            } else {
                desc.append("价格：").append(record.getOriginPrice()).append(" → ").append(record.getCurrentPrice()).append("\n");
            }
            desc.append("点击下方消息中的链接操作");
            articles.add(WeChatNewsArticle.builder()
                    .title(record.getMercariItemTitle())
                    .description(desc.toString())
                    .url(mercariItemUrl(record))
                    .picurl(picurl)
                    .build());
        }
        return articles;
    }

    private String mercariPicUrl(ItemRecord record) {
        if ("ITEM_TYPE_MERCARI".equalsIgnoreCase(record.getItemType())) {
            return NotifyImageUrls.resolveMercariPicUrl(record.getMercariItemId(), record.getImageUrl());
        }
        return NotifyImageUrls.pickWeChatPicUrl(record.getImageUrl());
    }

}
