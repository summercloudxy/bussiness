package com.xy.bussiness.mercari.notification;

import com.xy.bussiness.mercari.constants.ConditionEnum;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import com.xy.bussiness.mercari.mybean.SellerItemRecord;
import com.xy.bussiness.notification.WindowsNotification;
import com.xy.bussiness.notification.mail.MyMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SellerNotificationService {
    @Autowired
    MyMailSender mailSender;
    @Autowired
    WindowsNotification windowsNotification;
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${notification.host}")
    private String notifyHost;


    public boolean sendNew(MercariSellerSearchCondition searchCondition, List<SellerItemRecord> newItems) throws Exception {
        String description = searchCondition.getSellerId();
//        executorService.execute(()->windowsNotification.display("关注的用户上新啦",getNewWindowsContent(newItems)));
       return mailSender.send("关注的用户上新", getSellerNewMailContent(newItems),0);

    }

    public boolean sendPrice(MercariSellerSearchCondition searchCondition, List<SellerItemRecord> priceItems) throws Exception {
        return mailSender.send("关注的用户降价啦", getSellerPriceMailContent(priceItems),0);
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
            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Mercari/goodsitem.html?url=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>点击购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Meilu/goodsitem.html?url=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>电脑端购买</a>");
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
            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Mercari/goodsitem.html?url=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>点击购买</a>");
            stringBuilder.append("</div>");

            stringBuilder.append("<div>");
            stringBuilder.append("<a href='http://mercari.jpshuntong.com/Meilu/goodsitem.html?url=");
            stringBuilder.append(record.getMercariItemId());
            stringBuilder.append("'>电脑端购买</a>");
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


}
