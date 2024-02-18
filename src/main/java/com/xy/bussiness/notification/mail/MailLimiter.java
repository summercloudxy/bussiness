package com.xy.bussiness.notification.mail;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Component
public class MailLimiter {
    private static final int LIMIT = 9;
    private static AtomicInteger counter = new AtomicInteger(0);

    private static long lastResetTime = System.currentTimeMillis();

    public static boolean isAllowed(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResetTime > 1000 * 60 ){
            counter.set(0);
            lastResetTime = currentTime;
        }
        return counter.incrementAndGet() <= LIMIT;
    }




}
