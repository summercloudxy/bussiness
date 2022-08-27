package com.xy.bussiness.yahoo;

import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.ArrayList;
import java.util.List;

@Component
public class YahooPageProcessor implements PageProcessor {

    public void process(Page page) {
        Elements empty = page.getHtml().getDocument().body().getElementsByClass("Empty");
        if (empty.size() > 0){
            page.putField("empty", true);
            return;
        }
        Elements note = page.getHtml().getDocument().body().getElementsByClass("Notice__wandText");
        if (note.size() > 0){
            Element element = note.get(0);
            boolean conta = element.text().contains("一致する商品");
            if (conta){
                page.putField("empty", true);
                return;
            }
        }

        page.putField("empty", false);


        Elements products = page.getHtml().getDocument().body().getElementsByClass("Product");
        List<YahooItemRecord> yahooItemRecords = new ArrayList<>();
        for (Element product : products) {
            YahooItemRecord yahooItemRecord = new YahooItemRecord();
            Elements productDetails = product.getElementsByClass("Product__bonus");
            Element productDetail = productDetails.get(0);
            String auctionId = productDetail.attr("data-auction-id");
            String auctionEndTime = productDetail.attr("data-auction-endtime");
            String buyNowPrice = productDetail.attr("data-auction-buynowprice");
            String categoryIdPath = productDetail.attr("data-auction-categoryidpath");
            String auctionPrice = productDetail.attr("data-auction-price");
            String auctionStartPrice = productDetail.attr("data-auction-startprice");
            Elements productImages = product.getElementsByClass("Product__imageData");
            Element productionImage = productImages.get(0);
            String title = productionImage.attr("alt");
            String imageUrl = productionImage.attr("src");


            yahooItemRecord.setAuctionId(auctionId);
            yahooItemRecord.setTitle(title);
            yahooItemRecord.setImageUrl(imageUrl);
            yahooItemRecord.setBuyNowPrice(Integer.valueOf(buyNowPrice));
            yahooItemRecord.setAuctionPrice(Integer.valueOf(auctionPrice));
            yahooItemRecord.setOriginPrice(Integer.valueOf(auctionStartPrice));
            yahooItemRecords.add(yahooItemRecord);
        }
        page.putField("items", yahooItemRecords);
    }

    //可以对爬虫进行一些配置
    private Site site = Site.me().setTimeOut(5000).setCycleRetryTimes(3);

    public Site getSite() {
        return site;
    }

    //WebMagic使用的默认下载器是HttpClient
    public static void main(String[] args) {
        //提供自己定义的PageProcessor
        Spider spider = Spider.create(new YahooPageProcessor())
                //设置初始下载url地址
                .addUrl("https://auctions.yahoo.co.jp/search/search?p=%E3%83%9F%E3%83%A9%E3%83%8E%E3%82%B3%E3%83%AC%E3%82%AF%E3%82%B7%E3%83%A7%E3%83%B3&va=%E3%83%9F%E3%83%A9%E3%83%8E%E3%82%B3%E3%83%AC%E3%82%AF%E3%82%B7%E3%83%A7%E3%83%B3&exflg=1&b=1&n=100&s1=new&o1=d");
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 7890)));
        spider.setDownloader(httpClientDownloader);
//        spider.setUUID()
        spider.run();
    }
}
