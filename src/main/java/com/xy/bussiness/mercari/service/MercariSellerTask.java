package com.xy.bussiness.mercari.service;

import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import com.xy.bussiness.mercari.mybean.MercariSellerSearchCondition;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class MercariSellerTask implements Runnable {

    private MercariSellerSearchCondition searchCondition;
    private BlockingQueue<MercariSellerSearchCondition> queue;
    private Date lastExecuteDate = new Date();

    public MercariSellerTask(MercariSellerSearchCondition searchCondition, BlockingQueue<MercariSellerSearchCondition> queue) {
        this.searchCondition = searchCondition;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            LocalTime localTime = LocalTime.now();
            int hour = localTime.getHour();
            int minute = localTime.getMinute();
            if (hour < 7 ){
                Integer duration = searchCondition.getDuration();
                // 夜间每小时只执行一次，加两分钟为了防止队列堆积延迟
                if (minute < duration + 2){
                    queue.put(searchCondition);
                }
                return;
            }
            queue.put(searchCondition);
        }catch (Exception e){
            System.out.println("用户" + searchCondition.getSellerId() + "添加队列失败");
            System.out.println(e.getMessage());
        }
    }

}
