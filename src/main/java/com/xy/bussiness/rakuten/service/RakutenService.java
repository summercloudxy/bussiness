package com.xy.bussiness.rakuten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.rakuten.RakutenPageProcessor;
import com.xy.bussiness.rakuten.RakutenPipeline;
import com.xy.bussiness.rakuten.RakutenTask;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.rakuten.mybean.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RakutenService {
    @Autowired
    private RakutenSearchConditionService searchConditionService;
    @Autowired
    private RakutenPipeline pipeline;
    private Map<Integer, RakutenSearchCondition> searchConditionMap;
    @Value("${rakuten.enable:true}")
    private Boolean rakutenEnable;

    private LinkedBlockingQueue<SearchRequest> queue = new LinkedBlockingQueue<>(1000);

    public LinkedBlockingQueue<SearchRequest> getQueue() {
        return queue;
    }

    private Date lastExecuteTime = new Date();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
    @Autowired
    private RakutenPageProcessor pageProcessor;
    ;

    @PostConstruct
    public void init() {
        if (rakutenEnable) {
            LambdaQueryWrapper<RakutenSearchCondition> queryWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<RakutenSearchCondition> eq = queryWrapper.eq(RakutenSearchCondition::isEnable, true);
            List<RakutenSearchCondition> allSearchCondition = searchConditionService.list(eq);
            searchConditionMap = allSearchCondition.stream().collect(Collectors.toMap(RakutenSearchCondition::getId, Function.identity()));
            for (RakutenSearchCondition searchCondition : allSearchCondition) {
                if (searchCondition.isEnable()) {
                    RakutenTask mercariTask = new RakutenTask(searchCondition, queue, 1);
                    log.info("添加乐天查询定时任务，查询关键字为{}，查询间隔为{}分钟", searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
                    scheduledExecutorService.scheduleWithFixedDelay(mercariTask, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
                }
            }

            httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 7890)));
            new Thread(() -> execute()).start();
        }
    }


    public void addTask(String conditionId, String pageNum) throws InterruptedException {
        queue.put(new SearchRequest(searchConditionMap.get(Integer.valueOf(conditionId)), Integer.valueOf(pageNum)));
    }

    public RakutenSearchCondition getSearchCondition(String conditionId){
        return searchConditionMap.get(Integer.valueOf(conditionId));
    }

    public void execute() {
        while (true) {
            try {
                SearchRequest poll = queue.take();
                RakutenSearchCondition searchCondition = poll.getSearchCondition();
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration < 3 * 1000L) {
                    Thread.sleep(3 * 1000L - duration);
                }
                log.info("乐天：开始查询关键字[{}]的产品,第{}页", searchCondition.getDescription(),poll.getPageNum());
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://fril.jp/s?transaction=selling&&order=desc&sort=created_at&query=");
                urlBuilder.append(searchCondition.getKeyword());
                if (StringUtils.isNotBlank(searchCondition.getSearchCategory())) {
                    urlBuilder.append("&category_id=");
                    urlBuilder.append(searchCondition.getSearchCategory());
                }
                if (poll.getPageNum() >1) {
                    urlBuilder.append("&page=");
                    urlBuilder.append(poll.getPageNum());
                }

                log.info("乐天：开始从地址查询数据:{}", urlBuilder);
                Spider spider = Spider.create(pageProcessor)
                        //设置初始下载url地址
                        .addUrl(urlBuilder.toString());
                spider.setDownloader(httpClientDownloader);
                spider.addPipeline(pipeline);
                // conditionId - pageSize
                spider.setUUID(searchCondition.getId() + "-" + poll.getPageNum());
                spider.run();
                lastExecuteTime = new Date();
            }
            catch (Exception e) {
                log.error("", e);
            }
        }

    }

}
