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

import static com.xy.bussiness.UrlConstants.SHUNTONG_MEILU_URL;

@Service
public class SellerNotificationService {
    @Autowired
    NotifySender notifySender;
    @Autowired
    WindowsNotification windowsNotification;
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${notification.host}")
    private String notifyHost;


    public boolean sendNew(MercariSellerSearchCondition searchCondition, List<SellerItemRecord> newItems) throws Exception {
        String description = searchCondition.getSellerId();
//        executorService.execute(()->windowsNotification.display("关注的用户上新啦",getNewWindowsContent(newItems)));
        String topic = "关注的用户上新";
        return notifySender.send(topic, getSellerNewMailContent(newItems), getSellerNewWeChatContent(newItems),
                buildSellerNewsArticles(newItems, true), WeChatPlatform.MERCARI, 0);
    }

    public boolean sendPrice(MercariSellerSearchCondition searchCondition, List<SellerItemRecord> priceItems) throws Exception {
        String topic = "关注的用户降价啦";
        return notifySender.send(topic, getSellerPriceMailContent(priceItems), getSellerPriceWeChatContent(priceItems),
                buildSellerNewsArticles(priceItems, false), WeChatPlatform.MERCARI, 0);
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


    public String getSellerNewMailContent(List<SellerItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (SellerItemRecord record : recordList) {

            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='https://jp.mercari.com/item/");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'><img src='http://static.mercdn.net/c!/w=240,f=webp/thumb/photos/");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("_1.jpg'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getMercariItemTitle());
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
            stringBuilder.append("<a href='https://" + notifyHost + "/mercari/seller/setInterest?interest=1&itemId=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>添加关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");
        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }


    public String getSellerPriceMailContent(List<SellerItemRecord> recordList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><META http-equiv=Content-Type content='text/html; charset=UTF-8'></head><body>");
        for (SellerItemRecord record : recordList) {
            stringBuilder.append("<div style='display:flex;width:100%'>");
            stringBuilder.append("<div style='flex: 1'>");
            stringBuilder.append("<a href='https://jp.mercari.com/item/");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'><img src='http://static.mercdn.net/c!/w=240,f=webp/thumb/photos/");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("_1.jpg'/></a>   ");
            stringBuilder.append("</div>");
            stringBuilder.append(" <div style='flex:1'>");
            stringBuilder.append("<div>");
            stringBuilder.append(record.getMercariItemTitle());
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append(record.getOriginPrice()).append("->").append(record.getCurrentPrice());

            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='");
            stringBuilder.append(SHUNTONG_MEILU_URL);
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>顺通购买</a>");
            stringBuilder.append("</div>");


            stringBuilder.append("<div>");
            stringBuilder.append("<a href='https://" + notifyHost + "/mercari/seller/setInterest?interest=0&itemId=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>不再关注</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("</div>");
            stringBuilder.append("</div>");

        }
        stringBuilder.append("</body><html>");
        return stringBuilder.toString();
    }

    public String getSellerNewWeChatContent(List<SellerItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (SellerItemRecord record : recordList) {
            appendSellerItemMarkdown(sb, record, true);
        }
        return sb.toString();
    }

    public String getSellerPriceWeChatContent(List<SellerItemRecord> recordList) {
        StringBuilder sb = new StringBuilder();
        for (SellerItemRecord record : recordList) {
            appendSellerItemMarkdown(sb, record, false);
            sb.append("- 价格：").append(record.getOriginPrice()).append(" → ").append(record.getCurrentPrice()).append("\n\n");
        }
        return sb.toString();
    }

    private void appendSellerItemMarkdown(StringBuilder sb, SellerItemRecord record, boolean isNew) {
        sb.append("### ").append(record.getMercariItemTitle()).append("\n");
        if (isNew) {
            sb.append("- 价格：").append(record.getCurrentPrice()).append("\n");
        }
        String interest = isNew ? "1" : "0";
        String interestLabel = isNew ? "添加关注" : "不再关注";
        WeChatMarkdownLinks.appendThreeLinks(sb,
                "https://jp.mercari.com/item/" + record.getMercariItemId(),
                SHUNTONG_MEILU_URL + record.getMercariItemId(),
                "https://" + notifyHost + "/mercari/seller/setInterest?interest=" + interest
                        + "&itemId=" + record.getMercariItemId(),
                interestLabel);
        sb.append("\n");
    }

    private List<WeChatNewsArticle> buildSellerNewsArticles(List<SellerItemRecord> recordList, boolean isNew) {
        List<WeChatNewsArticle> articles = new ArrayList<>();
        for (SellerItemRecord record : recordList) {
            String picurl = NotifyImageUrls.resolveMercariPicUrl(record.getMercariItemId(), null);
            StringBuilder desc = new StringBuilder();
            if (isNew) {
                desc.append("价格：").append(record.getCurrentPrice()).append("\n");
            } else {
                desc.append("价格：").append(record.getOriginPrice()).append(" → ").append(record.getCurrentPrice()).append("\n");
            }
            desc.append("点击下方消息中的链接操作");
            articles.add(WeChatNewsArticle.builder()
                    .title(record.getMercariItemTitle())
                    .description(desc.toString())
                    .url("https://jp.mercari.com/item/" + record.getMercariItemId())
                    .picurl(picurl)
                    .build());
        }
        return articles;
    }

}
