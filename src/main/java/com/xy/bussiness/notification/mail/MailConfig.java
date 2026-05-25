package com.xy.bussiness.notification.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({MailProperties.class, MailCleanupProperties.class})
public class MailConfig {
}
