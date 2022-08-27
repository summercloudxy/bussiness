package com.xy.bussiness.yahoo;

import com.xy.bussiness.yahoo.mybean.SearchRequest;
import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class YahooTask implements Runnable {

    private YahooSearchCondition searchCondition;
    private Integer pageNum;
    private BlockingQueue<SearchRequest> queue;
    private Date lastExecuteDate = new Date();

    public YahooTask(YahooSearchCondition searchCondition, BlockingQueue<SearchRequest> queue,Integer pageNum) {
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
