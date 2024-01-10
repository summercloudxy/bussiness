package com.xy.bussiness.mercari;

import com.xy.bussiness.mercari.constants.ConditionEnum;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import com.xy.bussiness.mercari.sellerbean.DataItem;
import com.xy.bussiness.mercari.sellerbean.SellerItemResponse;
import com.xy.bussiness.notification.mail.MyMailSender;
import com.xy.bussiness.mercari.apibean.*;
import com.xy.bussiness.mercari.constants.CategoryEnum;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class MercariCrawler  {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MyMailSender myMailSender;
    @Value("${mercari.dpop}")
    private String dpop;
    @Value("${mercari.item.dpop}")
    private String itemdpop;
    @Value("${mercari.seller.dpop}")
    private String sellerdpop;
    private Lock lock = new ReentrantLock();


    public List<ItemsItem> getMercariItemsByCondition(MercariSearchCondition mercariSearchCondition) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        SearchItemListRequest searchItemListRequest = new SearchItemListRequest();
        UUID uuid = UUID.randomUUID();
        String uus = uuid.toString().replaceAll("\\-", "");
        searchItemListRequest.setSearchSessionId(uus);
        searchItemListRequest.setIndexRouting("INDEX_ROUTING_UNSPECIFIED");
        searchItemListRequest.setPageSize(120);

        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setOrder("ORDER_DESC");
        searchCondition.setSort("SORT_SCORE");

        searchCondition.setHasCoupon(false);
        searchCondition.setStatus(Collections.singletonList("STATUS_ON_SALE"));
        searchCondition.setSort("SORT_CREATED_TIME");

        searchCondition.setKeyword(mercariSearchCondition.getKeyword());
        searchCondition.setPriceMax(mercariSearchCondition.getPriceMax() == null? 0: mercariSearchCondition.getPriceMax());
        searchCondition.setPriceMin(mercariSearchCondition.getPriceMin() == null? 0 : mercariSearchCondition.getPriceMin());
        if (StringUtils.isNotBlank(mercariSearchCondition.getSearchCategory())){
            String[] split = StringUtils.split(mercariSearchCondition.getSearchCategory(), ",");
            List<Integer> collect = Arrays.stream(split).map(CategoryEnum::getIdByName).filter(Objects::nonNull).collect(Collectors.toList());
            searchCondition.setCategoryId(collect);
        }
        if (StringUtils.isNotBlank(mercariSearchCondition.getItemCondition())){
            String[] split = StringUtils.split(mercariSearchCondition.getItemCondition(), ",");
            List<Integer> collect = Arrays.stream(split).map(ConditionEnum::getIdByName).filter(Objects::nonNull).collect(Collectors.toList());
            searchCondition.setItemConditionId(collect);
        }
        searchItemListRequest.setSearchCondition(searchCondition);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding", "gzip, deflate");
        headers.add("Connection", "keep-alive");
        headers.add("authority", "api.mercari.jp");
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("x-platform", "web");
        headers.add("dpop", dpop);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        headers.add("content-type", "application/json");
        headers.add("origin", "https://jp.mercari.com");
        headers.add("sec-fetch-site", "cross-site");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-dest", "empty");
        headers.add("referer", "https://jp.mercari.com/search?keyword=%E3%82%B7%E3%83%A3%E3%83%8D%E3%83%AB%20%E3%83%AC%E3%83%86%E3%82%A3%E3%82%B5%E3%83%BC%E3%82%B8%E3%83%A5%20%E3%83%A9%E3%83%A1");
        headers.add("accept-language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7");
        // 请求
        HttpEntity<SearchItemListRequest> requst = new HttpEntity<>(searchItemListRequest, headers);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
       ResponseEntity<ItemListResponse> responseEntity = restTemplate.postForEntity("https://api.mercari.jp/v2/entities:search", requst, ItemListResponse.class);
        return responseEntity.getBody().getItems();

    }




    public List<DataItem> getMercariItemsBySeller(MercariSellerSearchCondition mercariSellerSearchCondition) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        SearchItemListRequest searchItemListRequest = new SearchItemListRequest();
        UUID uuid = UUID.randomUUID();
        String uus = uuid.toString().replaceAll("\\-", "");
        searchItemListRequest.setSearchSessionId(uus);
        searchItemListRequest.setIndexRouting("INDEX_ROUTING_UNSPECIFIED");
        searchItemListRequest.setPageSize(120);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Connection", "keep-alive");
        headers.add("authority", "api.mercari.jp");
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("x-platform", "web");
        headers.add("dpop", sellerdpop);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        headers.add("content-type", "application/json");
        headers.add("origin", "https://jp.mercari.com");
        headers.add("sec-fetch-site", "cross-site");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-dest", "empty");
        headers.add("referer", "https://jp.mercari.com/search?keyword=%E3%82%B7%E3%83%A3%E3%83%8D%E3%83%AB%20%E3%83%AC%E3%83%86%E3%82%A3%E3%82%B5%E3%83%BC%E3%82%B8%E3%83%A5%20%E3%83%A9%E3%83%A1");
        headers.add("accept-language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7");
        // 请求

        HttpEntity<SearchItemListRequest> request = new HttpEntity<>(null, headers);
        ResponseEntity<SellerItemResponse> response = restTemplate.exchange(
                "https://api.mercari.jp/items/get_items?seller_id="+ mercariSellerSearchCondition.getSellerId() + "&limit=30&status=on_sale",
                HttpMethod.GET,
                request,
                SellerItemResponse.class,
                1
        );
      return response.getBody().getData();
    }


    public void setDpop(String dpop) throws IOException {
        this.dpop = dpop;
        try {
            Properties properties = new Properties();
            //todo 修改绝对路径
            lock.lock();

            InputStream inStream = new FileInputStream("E:\\myproject\\bussiness\\src\\main\\resources\\application.properties");//获取配置文件输入流
            properties.load(inStream);
            properties.setProperty("mercari.dpop", dpop);

            OutputStream outputStream = new FileOutputStream("E:\\myproject\\bussiness\\src\\main\\resources\\application.properties");
            properties.store(outputStream, "summer");
            outputStream.close();
            lock.unlock();
        }catch (Exception e){

        }
    }

    public void setSellerDpop(String dpop) throws IOException {
        this.sellerdpop = dpop;
        try {
            Properties properties = new Properties();
            //todo 修改绝对路径
            lock.lock();
            InputStream inStream = new FileInputStream("E:\\myproject\\bussiness\\src\\main\\resources\\application.properties");//获取配置文件输入流
//            InputStream inStream = getClass().getResourceAsStream("/application.properties");//获取配置文件输入流
            properties.load(inStream);
            properties.setProperty("mercari.seller.dpop", dpop);

            OutputStream outputStream = new FileOutputStream("E:\\myproject\\bussiness\\src\\main\\resources\\application.properties");
            properties.store(outputStream, "xy");
            outputStream.close();
            lock.unlock();
        }catch (Exception e){

        }
    }


    public String getDpop(){
        return dpop;
    }


    public String getItemdpop() {
        return itemdpop;
    }

    public void setItemdpop(String itemdpop) {
        this.itemdpop = itemdpop;
        try {
            Properties properties = new Properties();
            //todo 修改绝对路径
//            OutputStream outputStream = new FileOutputStream("D:\\bussiness\\src\\main\\resources\\application.properties");
//            InputStream inStream = getClass().getResourceAsStream("/application.properties");//获取配置文件输入流

            InputStream inStream = new FileInputStream("D:\\bussiness\\src\\main\\resources\\application.properties");//获取配置文件输入流
            properties.load(inStream);
            properties.setProperty("mercari.item.dpop", itemdpop);

            OutputStream outputStream = new FileOutputStream("D:\\bussiness\\src\\main\\resources\\application.properties");
            properties.store(outputStream, "summer");
            outputStream.close();
        }catch (Exception e){

        }
    }

    public ItemData getItemDetail(String itemId){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Connection", "keep-alive");
        headers.add("authority", "api.mercari.jp");
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("x-platform", "web");
        headers.add("dpop", itemdpop);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        headers.add("content-type", "application/json");
        headers.add("origin", "https://jp.mercari.com");
        headers.add("sec-fetch-site", "cross-site");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-dest", "empty");
        headers.add("referer", "https://jp.mercari.com/search?keyword=%E3%82%B7%E3%83%A3%E3%83%8D%E3%83%AB%20%E3%83%AC%E3%83%86%E3%82%A3%E3%82%B5%E3%83%BC%E3%82%B8%E3%83%A5%20%E3%83%A9%E3%83%A1");
        headers.add("accept-language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7");
        HttpEntity<SearchItemListRequest> request = new HttpEntity<>(null, headers);
        ResponseEntity<ItemResponse> response = restTemplate.exchange(
                "https://api.mercari.jp/items/get?id=" + itemId,
                HttpMethod.GET,
                request,
                ItemResponse.class,
                1
        );

        return response.getBody().getData();
    }

}
