package com.xy.bussiness.mercari.service;

import com.xy.bussiness.mercari.mybean.MercariSearchCondition;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class MercariTask implements Runnable {

    private MercariSearchCondition searchCondition;
    private BlockingQueue<MercariSearchCondition> queue;
    private Date lastExecuteDate = new Date();

    public MercariTask(MercariSearchCondition searchCondition, BlockingQueue<MercariSearchCondition> queue) {
        this.searchCondition = searchCondition;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            LocalTime localTime = LocalTime.now();
            int hour = localTime.getHour();
            if (hour < 7){
                return;
            }
            queue.put(searchCondition);
        }catch (Exception e){}
    }

}
