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
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
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
    @Autowired
    MercariItemRecordService mercariItemRecordService;
    @Value("${mercari.enable:true}")
    private Boolean mercariEnable;

    private LinkedBlockingQueue<MercariSearchCondition> queue = new LinkedBlockingQueue<>(1000);
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
                    MercariTask mercariTask = new MercariTask(searchCondition, queue);
                    log.info("添加查询定时任务，查询关键字为{}，查询间隔为{}分钟", searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
                    scheduledExecutorService.scheduleWithFixedDelay(mercariTask, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
                }
            }
            new Thread(() -> execute()).start();
        }
    }


    public void execute() {
        while (true) {
            try {
                MercariSearchCondition poll = queue.take();
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration <500L) {
                    Thread.sleep(500L - duration);
                }
                log.info("开始查询关键字[{}]的产品", poll.getDescription());
                List<ItemsItem> crawl = null;
                try {
                    crawl = mercariCrawler.getMercariItemsByCondition(poll);
                } catch (ResourceAccessException e) {
                    // 间隔较长的任务，失败了重试
                    if (poll.getDuration() > 10) {
                        queue.offer(poll);
                    }
                }
                log.info("关键字[{}]搜索到[{}]条产品", poll.getDescription(), crawl == null ? -1 : crawl.size());
                if (!CollectionUtils.isEmpty(crawl)) {
                    check(poll, crawl);
                }
                lastExecuteTime = new Date();
            } catch (Exception e) {
                log.error("", e);
            }
        }

    }


    public void check(MercariSearchCondition searchCondition, List<ItemsItem> itemList) throws Exception {
        List<ItemRecord> lastItemList = mercariMapper.getItemRecordsByCondition(searchCondition.getId());
        Map<String, ItemRecord> oldItems = lastItemList.stream().collect(Collectors.toMap(ItemRecord::getMercariItemId, Function.identity()));
        List<ItemRecord> newItems = new ArrayList<>();
        List<ItemRecord> currentAllItems = new ArrayList<>();
        List<ItemRecord> priceItems = new ArrayList<>();
        List<ItemRecord> noticeNewItems = new ArrayList<>();
        for (ItemsItem item : itemList) {
            String mercariItemId = item.getId();
            ItemRecord oldItem = oldItems.get(mercariItemId);
            ItemRecord itemRecord = new ItemRecord();
            itemRecord.setMercariItemId(mercariItemId);
            itemRecord.setSearchConditionId(searchCondition.getId());
            itemRecord.setCurrentPrice(Integer.valueOf(item.getPrice()));
            itemRecord.setCreateDate(new Date(Long.parseLong(item.getCreated()) * 1000));
            itemRecord.setUpdateDate(new Date(Long.parseLong(item.getUpdated()) * 1000));
            itemRecord.setSellerId(item.getSellerId());
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
                    noticeNewItems.add(itemRecord);
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
            notificationService.sendNew(searchCondition, noticeNewItems);
        }
        if (!CollectionUtils.isEmpty(newItems)) {
            itemRecordService.saveBatch(newItems);
        }
        if (!CollectionUtils.isEmpty(priceItems)) {
            log.info("搜索条件-[{}]降价啦,推送通知", searchCondition.getDescription());
            notificationService.sendPrice(searchCondition, priceItems);
            for (ItemRecord priceItem : priceItems) {
                LambdaUpdateWrapper<ItemRecord> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.eq(ItemRecord::getMercariItemId, priceItem.getMercariItemId());
                updateWrapper.set(ItemRecord::getCurrentPrice, priceItem.getCurrentPrice());
                updateWrapper.set(ItemRecord::getUpdateDate, priceItem.getUpdateDate());
                itemRecordService.update(updateWrapper);
            }
        }
    }


}
