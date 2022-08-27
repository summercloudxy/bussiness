package com.xy.bussiness.mercari;

import com.xy.bussiness.mail.MyMailSender;
import com.xy.bussiness.mercari.apibean.ItemsItem;
import com.xy.bussiness.mercari.apibean.ItemsResponse;
import com.xy.bussiness.mercari.apibean.SearchCondition;
import com.xy.bussiness.mercari.apibean.SearchRequest;
import com.xy.bussiness.mercari.constants.CategoryEnum;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MercariCrawler  {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MyMailSender myMailSender;
    @Value("${mercari.dpop}")
    private String dpop;



    public List<ItemsItem> crawl(MercariSearchCondition mercariSearchCondition) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        SearchRequest searchRequest = new SearchRequest();
        UUID uuid = UUID.randomUUID();
        String uus = uuid.toString().replaceAll("\\-", "");
        searchRequest.setSearchSessionId(uus);
        searchRequest.setIndexRouting("INDEX_ROUTING_UNSPECIFIED");
        searchRequest.setPageSize(120);

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
        searchRequest.setSearchCondition(searchCondition);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding", "gzip, deflate, br");
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
        HttpEntity<SearchRequest> requst = new HttpEntity<>(searchRequest, headers);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        ResponseEntity<ItemsResponse> responseEntity = restTemplate.postForEntity("https://api.mercari.jp/v2/entities:search", requst, ItemsResponse.class);
        return responseEntity.getBody().getItems();

    }


    public void setDpop(String dpop){
        this.dpop = dpop;
    }


}
