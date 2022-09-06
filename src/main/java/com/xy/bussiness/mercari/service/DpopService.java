package com.xy.bussiness.mercari.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableList;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DpopService  {
    @Autowired
    MercariCrawler mercariCrawler;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    ;

    @PostConstruct
    public void init() {
        scheduledExecutorService.scheduleWithFixedDelay(this::updateDpop, 0,1, TimeUnit.DAYS);
    }



    public void updateDpop() {
        System.setProperty("webdriver.chrome.driver", "C://Users//admin//Desktop//chromedriver_win32//chromedriver.exe");
        ChromeDriver chromeDriver = new ChromeDriver();

        DevTools chromeDevTools = chromeDriver.getDevTools();
        chromeDevTools.createSession();

        //enable Network
        chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        //set blocked URL patterns
        chromeDevTools.send(Network.setBlockedURLs(ImmutableList.of("*.css", "*.png")));
//        RequestPattern requestPattern = new RequestPattern("*.css", ResourceType.Stylesheet, InterceptionStage.HeadersReceived);
//        chromeDevTools.send(Network.setRequestInterception(ImmutableList.of(requestPattern)));

        //add event listener to verify that css and png are blocked
        chromeDevTools.addListener(Network.requestWillBeSent(), loadingFailed -> {

//            chromeDevTools.send(
//                    Network.continueInterceptedRequest(loadingFailed.getInterceptionId(),
//                            Optional.empty(),
//                            Optional.empty(),
//                            Optional.empty(), Optional.empty(),
//                            Optional.empty(),
//                            Optional.empty(), Optional.empty()));
            Request request = loadingFailed.getRequest();
            if (request.getUrl().contains("entities")) {
                String dPoP = (String) request.getHeaders().get("DPoP");
                if (StringUtils.isNotBlank(dPoP)) {
                    mercariCrawler.setDpop(dPoP);
                    log.info("更新Dpop");
                }
            }
        });

        chromeDriver.get("https://jp.mercari.com/search?keyword=chanel&order=desc&sort=created_time&t3_category_id=789%2C792&category_id=789%2C792&t1_category_id=6&t2_category_id=88&item_condition_id=1%2C2&status=on_sale");

    }
}
