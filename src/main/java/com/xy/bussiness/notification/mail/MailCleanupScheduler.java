package com.xy.bussiness.notification.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailCleanupScheduler {

    @Autowired
    private MailCleanupProperties cleanupProperties;
    @Autowired
    private MailSentCleanupService mailSentCleanupService;

    @Scheduled(cron = "${mail.cleanup.cron:0 0 3 * * ?}", zone = "Asia/Shanghai")
    public void cleanupSentMailboxDaily() {
        if (!cleanupProperties.isEnabled()) {
            return;
        }
        log.info("开始定时清理发件箱 [{}]", cleanupProperties.getSentFolder());
        mailSentCleanupService.cleanupSentFolder();
    }
}
