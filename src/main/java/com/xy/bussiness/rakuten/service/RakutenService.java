package com.xy.bussiness.rakuten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.rakuten.RakutenPageProcessor;
import com.xy.bussiness.rakuten.RakutenPipeline;
import com.xy.bussiness.rakuten.RakutenTask;
import com.xy.bussiness.rakuten.mybatisservice.RakutenItemRecordService;
import com.xy.bussiness.rakuten.mybatisservice.RakutenSearchConditionService;
import com.xy.bussiness.rakuten.mybean.RakutenItemRecord;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RakutenService {
    @Autowired
    private RakutenSearchConditionService searchConditionService;
    @Autowired
    private RakutenItemRecordService rakutenItemRecordService;
    @Autowired
    private RakutenPipeline pipeline;
    private Map<Integer, RakutenSearchCondition> searchConditionMap;
    @Value("${rakuten.enable:true}")
    private Boolean rakutenEnable;
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
                    scheduleSearchCondition(searchCondition);
                }
            }
            if (proxyEnabled) {
                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(proxyHost, proxyPort)));
            }
            new Thread(() -> execute()).start();
        }
    }


    private void scheduleSearchCondition(RakutenSearchCondition searchCondition) {
        RakutenTask task = new RakutenTask(searchCondition, queue, 1);
        log.info("添加乐天查询定时任务，查询关键字为{}，查询间隔为{}分钟",
                searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
        ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(
                task, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
        scheduledTasks.put(searchCondition.getId(), future);
    }

    public RakutenSearchCondition registerSearchCondition(RakutenSearchCondition condition) {
        searchConditionService.save(condition);
        if (searchConditionMap == null) {
            searchConditionMap = new ConcurrentHashMap<>();
        }
        searchConditionMap.put(condition.getId(), condition);
        if (rakutenEnable && condition.isEnable()) {
            scheduleSearchCondition(condition);
        }
        return condition;
    }

    public String disableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        RakutenSearchCondition condition = searchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (!condition.isEnable()) {
            return "该关键字已停止关注：" + condition.getDescription();
        }
        LambdaUpdateWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RakutenSearchCondition::getId, conditionId);
        wrapper.set(RakutenSearchCondition::isEnable, false);
        searchConditionService.update(wrapper);
        condition.setEnable(false);
        if (searchConditionMap != null) {
            searchConditionMap.put(conditionId, condition);
        }
        cancelScheduledTask(conditionId);
        log.info("已停止关注乐天关键字[{}]，enable=0，定时任务已取消", condition.getDescription());
        return "已停止关注关键字：" + condition.getDescription();
    }

    public String enableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        RakutenSearchCondition condition = searchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (condition.isEnable()) {
            return "该关键字已启用：" + condition.getDescription();
        }
        LambdaUpdateWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RakutenSearchCondition::getId, conditionId);
        wrapper.set(RakutenSearchCondition::isEnable, true);
        searchConditionService.update(wrapper);
        RakutenSearchCondition updated = searchConditionService.getById(conditionId);
        if (searchConditionMap == null) {
            searchConditionMap = new ConcurrentHashMap<>();
        }
        searchConditionMap.put(conditionId, updated);
        if (rakutenEnable) {
            scheduleSearchCondition(updated);
        }
        return "已启用关键字：" + condition.getDescription();
    }

    public String deleteSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        RakutenSearchCondition condition = searchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        cancelScheduledTask(conditionId);
        LambdaQueryWrapper<RakutenItemRecord> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.eq(RakutenItemRecord::getSearchConditionId, conditionId);
        rakutenItemRecordService.remove(itemWrapper);
        searchConditionService.removeById(conditionId);
        if (searchConditionMap != null) {
            searchConditionMap.remove(conditionId);
        }
        return "已删除关键字：" + condition.getDescription();
    }

    public String updateSearchConditionDuration(Integer conditionId, Integer duration) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (duration == null || duration < 1) {
            return "查询间隔必须大于0分钟";
        }
        RakutenSearchCondition condition = searchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        LambdaUpdateWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RakutenSearchCondition::getId, conditionId);
        wrapper.set(RakutenSearchCondition::getDuration, duration);
        searchConditionService.update(wrapper);
        RakutenSearchCondition updated = searchConditionService.getById(conditionId);
        if (searchConditionMap != null) {
            searchConditionMap.put(conditionId, updated);
        }
        if (updated.isEnable()) {
            cancelScheduledTask(conditionId);
            if (rakutenEnable) {
                scheduleSearchCondition(updated);
            }
        }
        return "已更新查询间隔为 " + duration + " 分钟";
    }

    public String createSearchCondition(String brand, String description, String keyword,
                                        Integer duration, Boolean enable,
                                        String searchCategory, Integer maxPageNum, String status) {
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
        LambdaQueryWrapper<RakutenSearchCondition> dupWrapper = Wrappers.lambdaQuery();
        dupWrapper.eq(RakutenSearchCondition::getKeyword, trimmedKeyword);
        if (searchConditionService.count(dupWrapper) > 0) {
            return "关键字已存在：" + trimmedKeyword;
        }
        RakutenSearchCondition condition = new RakutenSearchCondition();
        condition.setKeyword(trimmedKeyword);
        condition.setDescription(description.trim());
        condition.setBrand(brand.trim());
        condition.setDuration(duration != null && duration > 0 ? duration : 120);
        condition.setEnable(enable != null && enable);
        condition.setSearchCategory(StringUtils.trimToNull(searchCategory));
        condition.setMaxPageNum(maxPageNum != null && maxPageNum > 0 ? maxPageNum : 3);
        condition.setStatus(StringUtils.isNotBlank(status) ? status.trim() : "new");
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
        RakutenSearchCondition condition = searchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        String trimmedKeyword = keyword.trim();
        if (!trimmedKeyword.equals(condition.getKeyword())) {
            LambdaQueryWrapper<RakutenSearchCondition> dupWrapper = Wrappers.lambdaQuery();
            dupWrapper.eq(RakutenSearchCondition::getKeyword, trimmedKeyword);
            dupWrapper.ne(RakutenSearchCondition::getId, conditionId);
            if (searchConditionService.count(dupWrapper) > 0) {
                return "关键字已被其他记录使用：" + trimmedKeyword;
            }
        }
        LambdaUpdateWrapper<RakutenSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RakutenSearchCondition::getId, conditionId);
        wrapper.set(RakutenSearchCondition::getKeyword, trimmedKeyword);
        searchConditionService.update(wrapper);
        RakutenSearchCondition updated = searchConditionService.getById(conditionId);
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
        RakutenSearchCondition condition = searchConditionMap.get(Integer.valueOf(conditionId));
        if (condition == null || !condition.isEnable()) {
            return;
        }
        queue.put(new SearchRequest(condition, Integer.valueOf(pageNum)));
    }

    public RakutenSearchCondition getSearchCondition(String conditionId){
        return searchConditionMap.get(Integer.valueOf(conditionId));
    }

    public void execute() {
        while (true) {
            try {
                SearchRequest poll = queue.take();
                RakutenSearchCondition searchCondition = poll.getSearchCondition();
                if (searchCondition == null || !searchCondition.isEnable()) {
                    continue;
                }
                RakutenSearchCondition current = searchConditionService.getById(searchCondition.getId());
                if (current == null || !current.isEnable()) {
                    continue;
                }
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration < 3 * 1000L) {
                    Thread.sleep(3 * 1000L - duration);
                }
                log.info("乐天：开始查询关键字[{}]的产品,第{}页", current.getDescription(), poll.getPageNum());
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://fril.jp/s?transaction=selling&&order=desc&sort=created_at&query=");
                urlBuilder.append(current.getKeyword());
                if (StringUtils.isNotBlank(current.getSearchCategory())) {
                    urlBuilder.append("&category_id=");
                    urlBuilder.append(current.getSearchCategory());
                }
                if ("new".equalsIgnoreCase(current.getStatus())) {
                    urlBuilder.append("&status=new");
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
