package com.xy.bussiness.mail;

import com.xy.bussiness.mercari.mybean.ItemRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class MyMailSender {

    @Autowired
    JavaMailSender javaMailSender;

    public void send(String topic, String content) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(topic);
        helper.setFrom("469654418@qq.com");
        helper.setTo("fengchunzhimei@163.com");
        helper.setSentDate(new Date());
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }



}
