package com.xy.bussiness.yahoo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.yahoo.mybatisservice.YahooItemRecordService;
import com.xy.bussiness.yahoo.mybatisservice.YahooSearchConditionService;
import com.xy.bussiness.yahoo.mybean.SearchRequest;
import com.xy.bussiness.yahoo.mybean.YahooItemRecord;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class YahooService {
    @Autowired
    private YahooSearchConditionService yahooSearchConditionService;
    @Autowired
    private YahooItemRecordService yahooItemRecordService;
    @Autowired
    private YahooPipeline pipeline;
    private Map<Integer, YahooSearchCondition> searchConditionMap;
    @Value("${yahoo.enable:true}")
    private Boolean yahooEnable;
    @Value("${network.proxy.enabled:false}")
    private boolean proxyEnabled;
    @Value("${network.proxy.host:127.0.0.1}")
    private String proxyHost;
    @Value("${network.proxy.port:7897}")
    private int proxyPort;

    private LinkedBlockingQueue<SearchRequest> queue = new LinkedBlockingQueue<>(1000);
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

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
                    scheduleSearchCondition(searchCondition);
                }
            }
            if (proxyEnabled) {
                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(proxyHost, proxyPort)));
            }
            new Thread(() -> execute()).start();
        }
    }


    private void scheduleSearchCondition(YahooSearchCondition searchCondition) {
        YahooTask task = new YahooTask(searchCondition, queue, 1);
        log.info("添加查询定时任务，查询关键字为{}，查询间隔为{}分钟",
                searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
        ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(
                task, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
        scheduledTasks.put(searchCondition.getId(), future);
    }

    public YahooSearchCondition registerSearchCondition(YahooSearchCondition condition) {
        yahooSearchConditionService.save(condition);
        if (searchConditionMap == null) {
            searchConditionMap = new ConcurrentHashMap<>();
        }
        searchConditionMap.put(condition.getId(), condition);
        if (yahooEnable && condition.isEnable()) {
            scheduleSearchCondition(condition);
        }
        return condition;
    }

    public String disableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        YahooSearchCondition condition = yahooSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (!condition.isEnable()) {
            return "该关键字已停止关注：" + condition.getDescription();
        }
        LambdaUpdateWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(YahooSearchCondition::getId, conditionId);
        wrapper.set(YahooSearchCondition::isEnable, false);
        yahooSearchConditionService.update(wrapper);
        condition.setEnable(false);
        if (searchConditionMap != null) {
            searchConditionMap.put(conditionId, condition);
        }
        cancelScheduledTask(conditionId);
        log.info("已停止关注雅虎关键字[{}]，enable=0，定时任务已取消", condition.getDescription());
        return "已停止关注关键字：" + condition.getDescription();
    }

    public String enableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        YahooSearchCondition condition = yahooSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (condition.isEnable()) {
            return "该关键字已启用：" + condition.getDescription();
        }
        LambdaUpdateWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(YahooSearchCondition::getId, conditionId);
        wrapper.set(YahooSearchCondition::isEnable, true);
        yahooSearchConditionService.update(wrapper);
        YahooSearchCondition updated = yahooSearchConditionService.getById(conditionId);
        if (searchConditionMap == null) {
            searchConditionMap = new ConcurrentHashMap<>();
        }
        searchConditionMap.put(conditionId, updated);
        if (yahooEnable) {
            scheduleSearchCondition(updated);
        }
        log.info("已启用雅虎关键字[{}]", condition.getDescription());
        return "已启用关键字：" + condition.getDescription();
    }

    public String deleteSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        YahooSearchCondition condition = yahooSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        cancelScheduledTask(conditionId);
        LambdaQueryWrapper<YahooItemRecord> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.eq(YahooItemRecord::getSearchConditionId, conditionId);
        yahooItemRecordService.remove(itemWrapper);
        yahooSearchConditionService.removeById(conditionId);
        if (searchConditionMap != null) {
            searchConditionMap.remove(conditionId);
        }
        log.info("已删除雅虎关键字[{}]", condition.getDescription());
        return "已删除关键字：" + condition.getDescription();
    }

    public String updateSearchConditionDuration(Integer conditionId, Integer duration) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (duration == null || duration < 1) {
            return "查询间隔必须大于0分钟";
        }
        YahooSearchCondition condition = yahooSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        LambdaUpdateWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(YahooSearchCondition::getId, conditionId);
        wrapper.set(YahooSearchCondition::getDuration, duration);
        yahooSearchConditionService.update(wrapper);
        YahooSearchCondition updated = yahooSearchConditionService.getById(conditionId);
        if (searchConditionMap != null) {
            searchConditionMap.put(conditionId, updated);
        }
        if (updated.isEnable()) {
            cancelScheduledTask(conditionId);
            if (yahooEnable) {
                scheduleSearchCondition(updated);
            }
        }
        return "已更新查询间隔为 " + duration + " 分钟";
    }

    public String createSearchCondition(String brand, String description, String keyword,
                                      Integer duration, Boolean enable,
                                      String searchCategory, Integer pageSize) {
        if (StringUtils.isBlank(keyword)) {
            return "搜索关键字不能为空";
        }
        if (StringUtils.isBlank(brand)) {
            return "品牌不能为空";
        }
        if (StringUtils.isBlank(description)) {
            return "描述不能为空";
        }
        String trimmedKeyword = keyword.trim();
        LambdaQueryWrapper<YahooSearchCondition> dupWrapper = Wrappers.lambdaQuery();
        dupWrapper.eq(YahooSearchCondition::getKeyword, trimmedKeyword);
        if (yahooSearchConditionService.count(dupWrapper) > 0) {
            return "关键字已存在：" + trimmedKeyword;
        }
        YahooSearchCondition condition = new YahooSearchCondition();
        condition.setKeyword(trimmedKeyword);
        condition.setDescription(description.trim());
        condition.setBrand(brand.trim());
        condition.setDuration(duration != null && duration > 0 ? duration : 120);
        condition.setEnable(enable != null && enable);
        condition.setSearchCategory(StringUtils.trimToNull(searchCategory));
        condition.setPageSize(pageSize != null && pageSize > 0 ? pageSize : 100);
        registerSearchCondition(condition);
        return "已新增关键字：" + condition.getDescription();
    }

    public String updateSearchConditionKeyword(Integer conditionId, String keyword) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (StringUtils.isBlank(keyword)) {
            return "搜索关键字不能为空";
        }
        YahooSearchCondition condition = yahooSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        String trimmedKeyword = keyword.trim();
        if (!trimmedKeyword.equals(condition.getKeyword())) {
            LambdaQueryWrapper<YahooSearchCondition> dupWrapper = Wrappers.lambdaQuery();
            dupWrapper.eq(YahooSearchCondition::getKeyword, trimmedKeyword);
            dupWrapper.ne(YahooSearchCondition::getId, conditionId);
            if (yahooSearchConditionService.count(dupWrapper) > 0) {
                return "关键字已被其他记录使用：" + trimmedKeyword;
            }
        }
        LambdaUpdateWrapper<YahooSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(YahooSearchCondition::getId, conditionId);
        wrapper.set(YahooSearchCondition::getKeyword, trimmedKeyword);
        yahooSearchConditionService.update(wrapper);
        YahooSearchCondition updated = yahooSearchConditionService.getById(conditionId);
        if (searchConditionMap != null && updated != null) {
            searchConditionMap.put(conditionId, updated);
        }
        return "已更新关键字：" + condition.getDescription();
    }

    private void cancelScheduledTask(Integer conditionId) {
        ScheduledFuture<?> future = scheduledTasks.remove(conditionId);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void addTask(String conditionId, String pageNum) throws InterruptedException {
        YahooSearchCondition condition = searchConditionMap.get(Integer.valueOf(conditionId));
        if (condition == null || !condition.isEnable()) {
            return;
        }
        queue.put(new SearchRequest(condition, Integer.valueOf(pageNum)));
    }

    public YahooSearchCondition getSearchCondition(String conditionId){
        return searchConditionMap.get(Integer.valueOf(conditionId));
    }

    public void execute() {
        while (true) {
            try {
                SearchRequest poll = queue.take();
                YahooSearchCondition searchCondition = poll.getSearchCondition();
                if (searchCondition == null || !searchCondition.isEnable()) {
                    continue;
                }
                YahooSearchCondition current = yahooSearchConditionService.getById(searchCondition.getId());
                if (current == null || !current.isEnable()) {
                    continue;
                }
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration < 3 * 1000L) {
                    Thread.sleep(3 * 1000L - duration);
                }
                log.info("雅虎：开始查询关键字[{}]的产品,第{}页", current.getDescription(), poll.getPageNum());
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://auctions.yahoo.co.jp/search/search?p=");
                urlBuilder.append(current.getKeyword());
                if (StringUtils.isNotBlank(current.getSearchCategory())) {
                    urlBuilder.append("&auccat=");
                    urlBuilder.append(current.getSearchCategory());
                }
//                urlBuilder.append("&va=");
//                urlBuilder.append(searchCondition.getKeyword());
                urlBuilder.append("&s1=new&o1=d&exflg=1&rc_ng=1");
                urlBuilder.append("&n=");
                urlBuilder.append(current.getPageSize());
                urlBuilder.append("&b=");
                urlBuilder.append(current.getPageSize() * (poll.getPageNum() - 1) + 1);
                log.info("雅虎：开始从地址查询数据:{}", urlBuilder);

                Spider spider = Spider.create(pageProcessor)
                        //设置初始下载url地址
                        .addUrl(urlBuilder.toString());
                spider.setDownloader(httpClientDownloader);
                spider.addPipeline(pipeline);
                // conditionId - pageSize
                spider.setUUID(current.getId() + "-" + poll.getPageNum());
                spider.run();
                lastExecuteTime = new Date();
            }
            catch (Exception e) {
                log.error("", e);
            }
        }

    }

}
