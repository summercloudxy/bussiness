package com.xy.bussiness.mercari.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

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
                    log.info("?????????????????????????????????????????????{}??????????????????{}??????", searchCondition.getBrand() + searchCondition.getDescription(), searchCondition.getDuration());
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
                // ???????????????????????????10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration < 1 * 1000L) {
                    Thread.sleep(1 * 1000L - duration);
                }
                log.info("?????????????????????[{}]?????????", poll.getDescription());
                List<ItemsItem> crawl = null;
                try {
                    crawl = mercariCrawler.crawl(poll);
                } catch (ResourceAccessException e) {
                    // ???????????????????????????????????????
                    if (poll.getDuration() > 10) {
                        queue.offer(poll);
                    }
                }
                log.info("?????????[{}]?????????[{}]?????????", poll.getDescription(), crawl == null ? -1 : crawl.size());
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
            log.info("????????????-[{}]???[{}]?????????,????????????", searchCondition.getDescription(), newItems.size());
            notificationService.sendNew(searchCondition, noticeNewItems);
        }
        if (!CollectionUtils.isEmpty(newItems)) {
            itemRecordService.saveBatch(newItems);
        }
        if (!CollectionUtils.isEmpty(priceItems)) {
            log.info("????????????-[{}]?????????,????????????", searchCondition.getDescription());
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
