package com.xy.bussiness.notification.mail;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicMailSenderFactory {

    private final MailProperties mailProperties;
    private final Map<String, JavaMailSenderImpl> senders = new ConcurrentHashMap<>();

    public DynamicMailSenderFactory(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    public JavaMailSenderImpl getFromSender() {
        return getSender(mailProperties.getFrom());
    }

    public JavaMailSenderImpl getSender(String provider) {
        String key = normalize(provider);
        return senders.computeIfAbsent(key, k -> build(mailProperties.account(k)));
    }

    private static String normalize(String provider) {
        String key = provider == null ? "" : provider.trim().toLowerCase();
        if ("163".equals(key)) {
            return "netease";
        }
        return key;
    }

    private static JavaMailSenderImpl build(MailAccount account) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(account.getHost());
        sender.setPort(account.getPort());
        sender.setUsername(account.getUsername());
        sender.setPassword(account.getPassword());
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        sender.setJavaMailProperties(smtpProperties(account.getPort()));
        return sender;
    }

    private static Properties smtpProperties(int port) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        if (port == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", "465");
        } else if (port == 587) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        return props;
    }
}
