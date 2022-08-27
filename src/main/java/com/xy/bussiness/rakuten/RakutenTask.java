package com.xy.bussiness.rakuten;

import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import com.xy.bussiness.rakuten.mybean.SearchRequest;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class RakutenTask implements Runnable {

    private RakutenSearchCondition searchCondition;
    private Integer pageNum;
    private BlockingQueue<SearchRequest> queue;
    private Date lastExecuteDate = new Date();

    public RakutenTask(RakutenSearchCondition searchCondition, BlockingQueue<SearchRequest> queue, Integer pageNum) {
        this.searchCondition = searchCondition;
        this.queue = queue;
        this.pageNum = pageNum;
    }

    @Override
    public void run() {
        try {
            LocalTime localTime = LocalTime.now();
            int hour = localTime.getHour();
            if (hour < 7){
                return;
            }
            queue.put(new SearchRequest(searchCondition,pageNum));
        }catch (Exception e){}
    }

}
