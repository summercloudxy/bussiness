package com.xy.bussiness.mercari.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import com.xy.bussiness.mercari.notification.NotificationService;
import com.xy.bussiness.notification.WindowsNotification;
import com.xy.bussiness.notification.mail.MyMailSender;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.apibean.ItemsItem;
import com.xy.bussiness.mercari.mapper.MercariMapper;
import com.xy.bussiness.mercari.mapper.MercariSearchConditionMapper;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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

@Slf4j
@Service
public class MercariSearchService {
    @Autowired
    private MercariCrawler mercariCrawler;
    @Autowired
    private MercariMapper mercariMapper;
    @Autowired
    private MyMailSender mailSender;
    @Autowired
    MercariItemRecordService itemRecordService;
    @Autowired
    MercariSearchConditionMapper searchConditionMapper;
    @Autowired
    MercariSearchConditionService mercariSearchConditionService;
    @Autowired
    WindowsNotification windowsNotification;
    @Autowired
    NotificationService notificationService;
    @Value("${mercari.enable:true}")
    private Boolean mercariEnable;

    private LinkedBlockingQueue<MercariSearchCondition> queue = new LinkedBlockingQueue<>(1000);
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private Date lastExecuteTime = new Date();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    ;

    @PostConstruct
    public void init() {

        if (mercariEnable) {
            LambdaQueryWrapper<MercariSearchCondition> queryWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<MercariSearchCondition> eq = queryWrapper.eq(MercariSearchCondition::isEnable, true);
            List<MercariSearchCondition> allSearchCondition = mercariSearchConditionService.list(eq);
            for (MercariSearchCondition searchCondition : allSearchCondition) {
                if (searchCondition.isEnable()) {
                    scheduleSearchCondition(searchCondition);
                }
            }
            new Thread(() -> execute()).start();
        }
    }


    private void scheduleSearchCondition(MercariSearchCondition searchCondition) {
        MercariTask mercariTask = new MercariTask(searchCondition, queue);
        log.info("添加查询定时任务，查询关键字为{}，查询间隔为{}分钟",
                searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
        ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(
                mercariTask, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
        scheduledTasks.put(searchCondition.getId(), future);
    }

    public String disableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        MercariSearchCondition condition = mercariSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (!condition.isEnable()) {
            return "该关键字已停止关注：" + condition.getDescription();
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MercariSearchCondition::getId, conditionId);
        wrapper.set(MercariSearchCondition::isEnable, false);
        mercariSearchConditionService.update(wrapper);
        cancelScheduledTask(conditionId);
        log.info("已停止关注煤炉关键字[{}]，enable=0，定时任务已取消", condition.getDescription());
        return "已停止关注关键字：" + condition.getDescription();
    }

    public String enableSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        MercariSearchCondition condition = mercariSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        if (condition.isEnable()) {
            return "该关键字已启用：" + condition.getDescription();
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MercariSearchCondition::getId, conditionId);
        wrapper.set(MercariSearchCondition::isEnable, true);
        mercariSearchConditionService.update(wrapper);
        MercariSearchCondition updated = mercariSearchConditionService.getById(conditionId);
        scheduleSearchCondition(updated);
        log.info("已启用煤炉关键字[{}]，定时任务已添加", condition.getDescription());
        return "已启用关键字：" + condition.getDescription();
    }

    public String deleteSearchCondition(Integer conditionId) {
        if (conditionId == null) {
            return "参数无效";
        }
        MercariSearchCondition condition = mercariSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        cancelScheduledTask(conditionId);
        LambdaQueryWrapper<ItemRecord> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.eq(ItemRecord::getSearchConditionId, conditionId);
        itemRecordService.remove(itemWrapper);
        mercariSearchConditionService.removeById(conditionId);
        log.info("已删除煤炉关键字[{}]", condition.getDescription());
        return "已删除关键字：" + condition.getDescription();
    }

    public String updateSearchConditionDuration(Integer conditionId, Integer duration) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (duration == null || duration < 1) {
            return "查询间隔必须大于0分钟";
        }
        MercariSearchCondition condition = mercariSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MercariSearchCondition::getId, conditionId);
        wrapper.set(MercariSearchCondition::getDuration, duration);
        mercariSearchConditionService.update(wrapper);
        MercariSearchCondition updated = mercariSearchConditionService.getById(conditionId);
        if (updated.isEnable()) {
            cancelScheduledTask(conditionId);
            scheduleSearchCondition(updated);
        }
        log.info("已更新煤炉关键字[{}]查询间隔为{}分钟", condition.getDescription(), duration);
        return "已更新查询间隔为 " + duration + " 分钟";
    }

    private void cancelScheduledTask(Integer conditionId) {
        ScheduledFuture<?> future = scheduledTasks.remove(conditionId);
        if (future != null) {
            future.cancel(false);
        }
    }

    public MercariSearchCondition registerSearchCondition(MercariSearchCondition condition) {
        mercariSearchConditionService.save(condition);
        if (mercariEnable && condition.isEnable()) {
            scheduleSearchCondition(condition);
        }
        return condition;
    }

    public String createSearchCondition(String brand, String description, String keyword,
                                       String enKeyword, Integer duration, Boolean enable) {
        if (StringUtils.isBlank(keyword)) {
            return "日文关键字不能为空";
        }
        if (StringUtils.isBlank(brand)) {
            return "品牌不能为空";
        }
        if (StringUtils.isBlank(description)) {
            return "描述不能为空";
        }
        String trimmedKeyword = keyword.trim();
        LambdaQueryWrapper<MercariSearchCondition> dupWrapper = Wrappers.lambdaQuery();
        dupWrapper.eq(MercariSearchCondition::getKeyword, trimmedKeyword);
        if (mercariSearchConditionService.count(dupWrapper) > 0) {
            return "日文关键字已存在：" + trimmedKeyword;
        }
        MercariSearchCondition condition = new MercariSearchCondition();
        condition.setKeyword(trimmedKeyword);
        condition.setEnKeyword(StringUtils.isBlank(enKeyword) ? null : enKeyword.trim());
        condition.setDescription(description.trim());
        condition.setBrand(brand.trim());
        condition.setDuration(duration != null && duration > 0 ? duration : 60);
        condition.setEnable(enable != null && enable);
        condition.setSearchCategory("HUAZHUANGPIN");
        condition.setItemCondition("QUANXIN,JINQUANXIN");
        registerSearchCondition(condition);
        log.info("新增煤炉关键字[{}] brand={}", condition.getDescription(), condition.getBrand());
        return "已新增关键字：" + condition.getDescription();
    }

    public String updateSearchConditionKeywords(Integer conditionId, String keyword, String enKeyword) {
        if (conditionId == null) {
            return "参数无效";
        }
        if (StringUtils.isBlank(keyword)) {
            return "日文关键字不能为空";
        }
        MercariSearchCondition condition = mercariSearchConditionService.getById(conditionId);
        if (condition == null) {
            return "关键字不存在";
        }
        String trimmedKeyword = keyword.trim();
        if (!trimmedKeyword.equals(condition.getKeyword())) {
            LambdaQueryWrapper<MercariSearchCondition> dupWrapper = Wrappers.lambdaQuery();
            dupWrapper.eq(MercariSearchCondition::getKeyword, trimmedKeyword);
            dupWrapper.ne(MercariSearchCondition::getId, conditionId);
            if (mercariSearchConditionService.count(dupWrapper) > 0) {
                return "日文关键字已被其他记录使用：" + trimmedKeyword;
            }
        }
        LambdaUpdateWrapper<MercariSearchCondition> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MercariSearchCondition::getId, conditionId);
        wrapper.set(MercariSearchCondition::getKeyword, trimmedKeyword);
        wrapper.set(MercariSearchCondition::getEnKeyword, StringUtils.isBlank(enKeyword) ? null : enKeyword.trim());
        mercariSearchConditionService.update(wrapper);
        log.info("更新煤炉关键字[{}] 日文/英文", condition.getDescription());
        return "已更新关键字：" + condition.getDescription();
    }


    public void execute() {
        while (true) {
            try {
                MercariSearchCondition poll = queue.take();
                MercariSearchCondition current = mercariSearchConditionService.getById(poll.getId());
                if (current == null || !current.isEnable()) {
                    continue;
                }
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
//                if (duration <500L) {
//                    Thread.sleep(500L - duration);
//                }
                log.info("开始查询关键字[{}]的产品", poll.getDescription());
                List<ItemsItem> crawl = null;
                try {
                    crawl = mercariCrawler.getMercariItemsByCondition(current);
                } catch (ResourceAccessException e) {
                    // 间隔较长的任务，失败了重试
                    if (poll.getDuration() > 10) {
                        queue.offer(poll);
                    }
                }
                log.info("关键字[{}]搜索到[{}]条产品", poll.getDescription(), crawl == null ? -1 : crawl.size());
                if (!CollectionUtils.isEmpty(crawl)) {
                    check(current, crawl);
                }
                lastExecuteTime = new Date();
            } catch (Exception e) {
                log.error("", e);
            }
        }

    }


    public void check(MercariSearchCondition searchCondition, List<ItemsItem> itemList) throws Exception {
        List<ItemRecord> lastItemList = mercariMapper.getItemRecordsByCondition(searchCondition.getId());
        Map<String, ItemRecord> oldItems = lastItemList.stream().collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity(), (a, b) -> a));
        List<ItemRecord> newItems = new ArrayList<>();
        List<ItemRecord> currentAllItems = new ArrayList<>();
        List<ItemRecord> priceItems = new ArrayList<>();
        List<ItemRecord> noticeNewItems = new ArrayList<>();
        List<ItemRecord> excludeNewItems = new ArrayList<>();
        for (ItemsItem item : itemList) {
            String mercariItemId = item.getId();
            ItemRecord oldItem = oldItems.get(mercariItemId);
            ItemRecord itemRecord = new ItemRecord();
            itemRecord.setMercariItemId(mercariItemId);
            itemRecord.setItemType(item.getItemType());
            itemRecord.setSearchConditionId(searchCondition.getId());
            if (!CollectionUtils.isEmpty(item.getThumbnails())){
                itemRecord.setImageUrl(item.getThumbnails().get(0));
            }
            itemRecord.setCurrentPrice(Integer.valueOf(item.getPrice()));
            itemRecord.setCreateDate(new Date(Long.parseLong(item.getCreated()) * 1000));
            itemRecord.setUpdateDate(new Date(Long.parseLong(item.getUpdated()) * 1000));
            itemRecord.setSellerId(item.getSellerId());

            itemRecord.setRecordCreateDate(new Date());
            if (StringUtils.isNotBlank(item.getItemConditionId())) {
                itemRecord.setItemConditionId(Integer.valueOf(item.getItemConditionId()));
            }
            itemRecord.setMercariItemTitle(item.getName());
            if (oldItem == null) {
                itemRecord.setOriginPrice(Integer.valueOf(item.getPrice()));
                itemRecord.setInterest(false);
                newItems.add(itemRecord);
                ItemRecord itemRecordByMercariId = mercariMapper.getItemRecordByMercariId(mercariItemId);
                if (itemRecordByMercariId == null) {
                    boolean needExclude = false;
                    if (StringUtils.isNotBlank(searchCondition.getExcludeKeyword())) {
                        String[] excludeKeywordList = searchCondition.getExcludeKeyword().split(",");
                        for (String exclude : excludeKeywordList) {
                            if (item.getName().contains(exclude)) {
                                needExclude = true;
                            }
                        }
                    }
                    itemRecord.setExclude(needExclude);
                    if (needExclude){
                        excludeNewItems.add(itemRecord);
                    }else {
                        noticeNewItems.add(itemRecord);
                    }
                }
            } else {
                itemRecord.setInterest(oldItem.isInterest());
                itemRecord.setId(oldItem.getId());
                if (itemRecord.isInterest() && oldItem.getCurrentPrice() > itemRecord.getCurrentPrice()) {
                    itemRecord.setOriginPrice(oldItem.getOriginPrice());
                    priceItems.add(itemRecord);
                }
            }
            currentAllItems.add(itemRecord);
        }

        if (!CollectionUtils.isEmpty(noticeNewItems)) {
            log.info("搜索条件-[{}]有[{}]条上新,推送通知", searchCondition.getDescription(), newItems.size());
            boolean sendResult = notificationService.sendNew(searchCondition, noticeNewItems);
            if (sendResult && !CollectionUtils.isEmpty(newItems)) {
                itemRecordService.saveBatch(newItems);
            }
        }

        if (!CollectionUtils.isEmpty(priceItems)) {
            log.info("搜索条件-[{}]降价啦,推送通知", searchCondition.getDescription());
            boolean sendResult = notificationService.sendPrice(searchCondition, priceItems);
            if (sendResult) {
                for (ItemRecord priceItem : priceItems) {
                    LambdaUpdateWrapper<ItemRecord> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.eq(ItemRecord::getMercariItemId, priceItem.getMercariItemId());
                    updateWrapper.set(ItemRecord::getCurrentPrice, priceItem.getCurrentPrice());
                    updateWrapper.set(ItemRecord::getUpdateDate, priceItem.getUpdateDate());
                    itemRecordService.update(updateWrapper);
                }
            }
        }
        if (!CollectionUtils.isEmpty(excludeNewItems)) {
            itemRecordService.saveBatch(excludeNewItems);
        }
    }


}