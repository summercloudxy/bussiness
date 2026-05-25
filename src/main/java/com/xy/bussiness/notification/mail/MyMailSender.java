package com.xy.bussiness.notification.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MyMailSender {

    @Autowired
    MailProperties mailProperties;
    @Autowired
    DynamicMailSenderFactory mailSenderFactory;
    @Autowired
    MailLimiter mailLimiter;

    BlockingQueue<MimeMessage> queue = new LinkedBlockingQueue<>(10000);

    public boolean send(String topic, String content, int tryCount) throws Exception {
        if (tryCount > 3) {
            return false;
        }
        JavaMailSenderImpl sender = mailSenderFactory.getFromSender();
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(topic);
        helper.setFrom(mailProperties.fromAddress());
        helper.setTo(mailProperties.toAddress());
        helper.setSentDate(new Date());
        helper.setText(content, true);
        mimeMessage.setHeader(MailAppMarker.HEADER_NAME, MailAppMarker.HEADER_VALUE);
        if (!MailLimiter.isAllowed()) {
            Thread.sleep(5000L);
            return send(topic, content, tryCount + 1);
        }
        try {
            sender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return send(topic, content, tryCount + 1);
        }
    }

    @PostConstruct
    public void realSend() {
        new Thread(() -> {
            while (true) {
                try {
                    MimeMessage take = queue.take();
                    mailSenderFactory.getFromSender().send(take);
                    Thread.sleep(5000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
