package com.xy.bussiness.notification.mail;

import com.xy.bussiness.mercari.mybean.ItemRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyMailSender {

    @Autowired
    JavaMailSender javaMailSender;
    Long timestamp = System.currentTimeMillis();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    BlockingQueue<MimeMessage> queue = new LinkedBlockingQueue(10000);

    public boolean send(String topic, String content) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(topic);
        helper.setFrom("469654418@qq.com");
        helper.setTo("fengchunzhimei@163.com");
        helper.setSentDate(new Date());
        helper.setText(content, true);
        queue.offer(mimeMessage);
//        javaMailSender.send(mimeMessage);
        return true;
    }


    @PostConstruct
    public void realSend() {
        new Thread(()->{
        while (true) {
            try {
                MimeMessage take = queue.take();
                javaMailSender.send(take);
                Thread.sleep(5000L);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }}).start();
    }


}
