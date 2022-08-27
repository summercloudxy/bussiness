package com.xy.bussiness.yahoo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.SearchRequest;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

@Service
@Slf4j
public class YahooService {
    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private YahooPipeline pipeline;
    private Map<Integer, YahooSearchCondition> searchConditionMap;
    @Value("${yahoo.enable:true}")
    private Boolean yahooEnable;

    private LinkedBlockingQueue<SearchRequest> queue = new LinkedBlockingQueue<>(1000);

    public LinkedBlockingQueue<SearchRequest> getQueue() {
        return queue;
    }

    private Date lastExecuteTime = new Date();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
    @Autowired
    private YahooPageProcessor pageProcessor;
    ;

    @PostConstruct
    public void init() {
        if (yahooEnable) {
            LambdaQueryWrapper<YahooSearchCondition> queryWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<YahooSearchCondition> eq = queryWrapper.eq(YahooSearchCondition::isEnable, true);
            List<YahooSearchCondition> allSearchCondition = yahooSearchConditionService.list(eq);
            searchConditionMap = allSearchCondition.stream().collect(Collectors.toMap(YahooSearchCondition::getId, Function.identity()));
            for (YahooSearchCondition searchCondition : allSearchCondition) {
                if (searchCondition.isEnable()) {
                    YahooTask mercariTask = new YahooTask(searchCondition, queue, 1);
                    log.info("添加查询定时任务，查询关键字为{}，查询间隔为{}分钟", searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
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

    public YahooSearchCondition getSearchCondition(String conditionId){
        return searchConditionMap.get(Integer.valueOf(conditionId));
    }

    public void execute() {
        while (true) {
            try {
                SearchRequest poll = queue.take();
                YahooSearchCondition searchCondition = poll.getSearchCondition();
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration < 3 * 1000L) {
                    Thread.sleep(3 * 1000L - duration);
                }
                log.info("雅虎：开始查询关键字[{}]的产品,第{}页", searchCondition.getDescription(),poll.getPageNum());
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://auctions.yahoo.co.jp/search/search?p=");
                urlBuilder.append(searchCondition.getKeyword());
                if (StringUtils.isNotBlank(searchCondition.getSearchCategory())) {
                    urlBuilder.append("&auccat=");
                    urlBuilder.append(searchCondition.getSearchCategory());
                }
//                urlBuilder.append("&va=");
//                urlBuilder.append(searchCondition.getKeyword());
                urlBuilder.append("&s1=new&o1=d&exflg=1&rc_ng=1");
                urlBuilder.append("&n=");
                urlBuilder.append(searchCondition.getPageSize());
                urlBuilder.append("&b=");
                urlBuilder.append(searchCondition.getPageSize() * (poll.getPageNum() - 1) + 1);
                log.info("雅虎：开始从地址查询数据:{}", urlBuilder);

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
