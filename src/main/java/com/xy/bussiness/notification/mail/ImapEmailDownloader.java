package com.xy.bussiness.notification.mail;

/**
 * 历史工具类，发件箱清理已改为 {@link MailSentCleanupService} + {@link MailCleanupScheduler} 定时执行。
 * 手动触发可在测试中调用 MailSentCleanupService#cleanupSentFolder()。
 */
public final class ImapEmailDownloader {

    private ImapEmailDownloader() {
    }
}
