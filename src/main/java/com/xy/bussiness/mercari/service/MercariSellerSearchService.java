package com.xy.bussiness.mercari.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xy.bussiness.mercari.MercariCrawler;
import com.xy.bussiness.mercari.mapper.MercariMapper;
import com.xy.bussiness.mercari.mapper.MercariSellerSearchConditionMapper;
import com.xy.bussiness.mercari.mybatisservice.MercariItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSellerItemRecordService;
import com.xy.bussiness.mercari.mybatisservice.MercariSellerSearchConditionService;
import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;
import com.xy.bussiness.mercari.mybean.SellerItemRecord;
import com.xy.bussiness.mercari.notification.NotificationService;
import com.xy.bussiness.mercari.notification.SellerNotificationService;
import com.xy.bussiness.mercari.sellerbean.DataItem;
import com.xy.bussiness.notification.WindowsNotification;
import com.xy.bussiness.notification.mail.MyMailSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MercariSellerSearchService {
    @Autowired
    private MercariCrawler mercariCrawler;
    @Autowired
    private MercariMapper mercariMapper;
    @Autowired
    MercariSellerSearchConditionMapper searchConditionMapper;
    @Autowired
    MercariSellerSearchConditionService mercariSearchConditionService;
    @Autowired
    SellerNotificationService notificationService;
    @Autowired
    MercariSellerItemRecordService mercariSellerItemRecordService;
    @Value("${mercari.enable:true}")
    private Boolean mercariEnable;

    private LinkedBlockingQueue<MercariSellerSearchCondition> queue = new LinkedBlockingQueue<>(1000);
    private Date lastExecuteTime = new Date();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    ;

    @PostConstruct
    public void init() {

        if (mercariEnable) {
            LambdaQueryWrapper<MercariSellerSearchCondition> queryWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<MercariSellerSearchCondition> eq = queryWrapper.eq(MercariSellerSearchCondition::isEnable, true);
            List<MercariSellerSearchCondition> allSearchCondition = mercariSearchConditionService.list(eq);
            for (MercariSellerSearchCondition searchCondition : allSearchCondition) {
                if (searchCondition.isEnable()) {
                    MercariSellerTask mercariTask = new MercariSellerTask(searchCondition, queue);
                    log.info("添加查询定时任务，查询用户为{}，查询间隔为{}分钟", searchCondition.getBrand() + searchCondition.getSellerId(), searchCondition.getDuration());
                    scheduledExecutorService.scheduleWithFixedDelay(mercariTask, 0, searchCondition.getDuration(), TimeUnit.MINUTES);
                }
            }
            new Thread(() -> execute()).start();
        }
    }


    public void execute() {
        while (true) {
            try {
                MercariSellerSearchCondition poll = queue.take();
                // 两次执行间隔要大于10s
                long duration = new Date().getTime() - lastExecuteTime.getTime();
                if (duration <500L) {
                    Thread.sleep(500L - duration);
                }
//                log.info("开始查询用户[{}]的产品", poll.getSellerId());
                List<DataItem> crawl = null;
                try {
                    crawl = mercariCrawler.getMercariItemsBySeller(poll);
                } catch (ResourceAccessException e) {
                    // 间隔较长的任务，失败了重试
                    if (poll.getDuration() > 10) {
                        queue.offer(poll);
                    }
                }
                log.info("用户[{}]搜索到[{}]条产品", poll.getSellerId(), crawl == null ? -1 : crawl.size());
                if (!CollectionUtils.isEmpty(crawl)) {
                    check(poll, crawl);
                }
                lastExecuteTime = new Date();
            } catch (Exception e) {
                log.error("", e);
            }
        }

    }


    public void check(MercariSellerSearchCondition searchCondition, List<DataItem> itemList) throws Exception {
        List<ItemRecord> itemInConditionList = mercariMapper.getItemRecordsBySeller(searchCondition.getSellerId());
        List<SellerItemRecord> lastItemList = mercariSellerItemRecordService.list();
        Set<String> itemInConditionSet = itemInConditionList.stream().map(ItemRecord::getMercariItemId).collect(Collectors.toSet());

        Map<String, SellerItemRecord> oldItems = lastItemList.stream().collect(Collectors.toMap(SellerItemRecord::getMercariItemId, Function.identity()));
        List<SellerItemRecord> newItems = new ArrayList<>();
        List<SellerItemRecord> currentAllItems = new ArrayList<>();
        List<SellerItemRecord> priceItems = new ArrayList<>();
        for (DataItem item : itemList) {
            // 根据condition查询已经有这条记录，不再二次推送
            if (itemInConditionSet.contains(item.getId())){
                continue;
            }
            if (searchCondition.getRootCategory()!=null && searchCondition.getRootCategory() != item.getRoot_category_id()){
                continue;
            }
            String mercariItemId = item.getId();
            SellerItemRecord oldItem = oldItems.get(mercariItemId);
            SellerItemRecord itemRecord = new SellerItemRecord();
            itemRecord.setMercariItemId(mercariItemId);
            itemRecord.setSellerId(searchCondition.getSellerId());
            itemRecord.setCurrentPrice(item.getPrice());
            itemRecord.setCreateDate(new Date(item.getCreated() * 1000L));
            itemRecord.setUpdateDate(new Date(item.getUpdated() * 1000L));
            itemRecord.setMercariItemTitle(item.getName());
            if (oldItem == null) {
                itemRecord.setOriginPrice(item.getPrice());
                itemRecord.setInterest(false);
                newItems.add(itemRecord);

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

        if (!CollectionUtils.isEmpty(newItems)) {
            log.info("搜索用户-[{}]有[{}]条上新,推送通知", searchCondition.getSellerId(), newItems.size());
            notificationService.sendNew(searchCondition, newItems);
            mercariSellerItemRecordService.saveBatch(newItems);
        }

        if (!CollectionUtils.isEmpty(priceItems)) {
            log.info("搜索用户-[{}]降价啦,推送通知", searchCondition.getSellerId());
            notificationService.sendPrice(searchCondition, priceItems);
            for (SellerItemRecord priceItem : priceItems) {
                LambdaUpdateWrapper<SellerItemRecord> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.eq(SellerItemRecord::getMercariItemId, priceItem.getMercariItemId());
                updateWrapper.set(SellerItemRecord::getCurrentPrice, priceItem.getCurrentPrice());
                updateWrapper.set(SellerItemRecord::getUpdateDate, priceItem.getUpdateDate());
                mercariSellerItemRecordService.update(updateWrapper);
            }
        }
    }


}
