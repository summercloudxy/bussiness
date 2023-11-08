package com.xy.bussiness.mercari.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableList;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v112.network.Network;
import org.openqa.selenium.devtools.v112.network.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class DpopService {
    @Autowired
    MercariCrawler mercariCrawler;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    ;
    volatile boolean dpopUpdated = false;
    volatile boolean itemdpopUpdated = false;
    volatile boolean sellerdpopUpdated = false;
    Condition condition = new ReentrantLock().newCondition();
    @Value("${chromedriver.address}")
    private String driverAddress;

    @PostConstruct
    public void init() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                this.updateDpop();
                this.updateSellerDpop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.DAYS);
    }


    public void updateDpop() throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", driverAddress);
        ChromeDriver chromeDriver = new ChromeDriver(options);

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
                    try {
                        mercariCrawler.setDpop(dPoP);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    log.info("更新Dpop");
                    dpopUpdated = true;
                }
            }
        });

        chromeDriver.get("https://jp.mercari.com/search?keyword=chanel&order=desc&sort=created_time&t3_category_id=789%2C792&category_id=789%2C792&t1_category_id=6&t2_category_id=88&item_condition_id=1%2C2&status=on_sale");
        while (!dpopUpdated) {
            Thread.sleep(2000);
        }
        dpopUpdated = false;
        chromeDriver.close();


    }


    public void updateSellerDpop() throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", driverAddress);
        ChromeDriver chromeDriver = new ChromeDriver(options);

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
            if (request.getUrl().contains("get_items")) {
                String dPoP = (String) request.getHeaders().get("DPoP");
                if (StringUtils.isNotBlank(dPoP)) {
                    try {
                        mercariCrawler.setSellerDpop(dPoP);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    log.info("更新sellerDpop");
                    sellerdpopUpdated = true;
                }
            }
        });

        chromeDriver.get("https://jp.mercari.com/user/profile/199329091");
        while (!sellerdpopUpdated) {
            Thread.sleep(2000);
        }
        sellerdpopUpdated = false;
        chromeDriver.close();

    }

    public void updateItemDpop(String reqItemId) throws InterruptedException {


        System.setProperty("webdriver.chrome.driver", driverAddress);
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
            if (request.getUrl().contains("get?id")) {
                String dPoP = (String) request.getHeaders().get("DPoP");
                if (StringUtils.isNotBlank(dPoP)) {
                    mercariCrawler.setItemdpop(dPoP);
                    log.info("更新ItemDpop");
                    itemdpopUpdated = true;
                }
            }
        });

        if (StringUtils.isBlank(reqItemId)) {
            reqItemId =
                    "m18325606132";
        }
        chromeDriver.get("https://jp.mercari.com/item/" + reqItemId);
        while (!itemdpopUpdated) {
            Thread.sleep(2000);
        }
        itemdpopUpdated = false;
        chromeDriver.close();
    }
}
