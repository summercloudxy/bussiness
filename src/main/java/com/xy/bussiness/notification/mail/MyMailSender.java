package com.xy.bussiness.notification.mail;

import com.xy.bussiness.mercari.mybean.ItemRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyMailSender {

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    MailLimiter mailLimiter;
    @Value("${mail.passwordArray}")
    String[] password;

    int passwordIndex = 0;
    String currentPassword;

    BlockingQueue<MimeMessage> queue = new LinkedBlockingQueue(10000);

    public boolean send(String topic, String content, int tryCount) throws Exception {
        if (tryCount > 3) {
            return false;
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(topic);
        helper.setFrom("469654418@qq.com");
        helper.setTo("fengchunzhimei@163.com");
        helper.setSentDate(new Date());
        helper.setText(content, true);
        if (!mailLimiter.isAllowed()) {
            Thread.sleep(5000L);
            return send(topic, content, tryCount + 1);
        } else {
            try {
//                send(topic, content, tryCount + 1);
//                sendMessage(topic, content);
                javaMailSender.send(mimeMessage);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                currentPassword = password[passwordIndex++ % password.length];
                Thread.sleep(5000L);
                return send(topic, content, tryCount + 1);
            }
        }
    }


    public boolean sendMessage(String topic, String content) throws Exception {
        String host = "smtp.qq.com"; // SMTP服务器地址
        String username = "469654418@qq.com"; // SMTP服务器用户名
//        String password = "hwkbyihwtwghbhcb"; // SMTP服务器密码

        String to = "fengchunzhimei@163.com"; // 收件人电子邮件地址
        String from = "469654418@qq.com"; // 发件人电子邮件地址
        String subject = topic; // 邮件主题
        String messageText = content; // 邮件正文


        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, currentPassword != null ? currentPassword : password[0]);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        // 发送网页
        message.setContent(messageText, "text/html;charset=UTF-8");

        Transport.send(message);

        return true;
    }

    @PostConstruct
    public void realSend() {
        new Thread(() -> {
            while (true) {
                try {
                    MimeMessage take = queue.take();
                    javaMailSender.send(take);
                    Thread.sleep(5000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


}
