package com.xy.bussiness.rakuten;

import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RakutenPageProcessor implements PageProcessor {

    public void process(Page page) {
        Elements empty = page.getHtml().getDocument().body().getElementsByClass("nohit");
        if (empty.size() > 0){
            page.putField("empty", true);
            return;
        }
        Elements products = page.getHtml().getDocument().body().getElementsByClass("item-box");
        if (CollectionUtils.isEmpty(products)){
            page.putField("empty", true);
            return;
        }

        page.putField("empty", false);
        List<RakutenItemRecord> rakutenItemRecords = new ArrayList<>();
        for (Element product : products) {
            RakutenItemRecord yahooItemRecord = new RakutenItemRecord();
            Elements productDetails = product.getElementsByClass("link_search_title");
            Element productDetail = productDetails.get(0);
            String itemUrl = productDetail.attr("href");
            Element child = productDetail.child(0);
            String title = child.text();

            Elements priceElements = product.getElementsByClass("item-box__item-price");
            Element priceElement = priceElements.get(0).child(1);
            String price = priceElement.attr("data-content");


            Elements imageElements = product.getElementsByClass("item-box__image-wrapper");
            Element imageElement = imageElements.get(0).child(0).child(1).child(0);
            String imageUrl = imageElement.attr("src");
            List<String> split = Arrays.asList(itemUrl.split("/"));


            yahooItemRecord.setTitle(title);
            yahooItemRecord.setImageUrl(imageUrl);
            yahooItemRecord.setItemUrl(itemUrl);
            yahooItemRecord.setCurrentPrice(Integer.valueOf(price));
            yahooItemRecord.setItemId(split.get(split.size()-1));
            rakutenItemRecords.add(yahooItemRecord);
        }
        page.putField("items", rakutenItemRecords);
    }

    //可以对爬虫进行一些配置
    private Site site = Site.me().setTimeOut(5000).setCycleRetryTimes(3);

    public Site getSite() {
        return site;
    }
}
