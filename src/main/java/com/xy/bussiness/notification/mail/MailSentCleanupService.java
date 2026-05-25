package com.xy.bussiness.notification.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class MailSentCleanupService {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MailCleanupProperties cleanupProperties;

    /**
     * 删除发件箱中本应用发出的邮件（自定义头或主题关键词匹配），分批 expunge。
     *
     * @return 本次删除的邮件数量
     */
    public int cleanupSentFolder() {
        MailAccount account = mailProperties.account(cleanupProperties.getProvider());
        String username = account.getUsername();
        String password = resolveImapPassword(account);
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("发件箱清理跳过：未配置 {} 邮箱账号或 IMAP 密码", cleanupProperties.getProvider());
            return 0;
        }

        int totalDeleted = 0;
        for (int batch = 0; batch < cleanupProperties.getMaxBatches(); batch++) {
            int deleted = deleteBatch(account, username, password);
            totalDeleted += deleted;
            if (deleted == 0) {
                break;
            }
            log.info("发件箱清理第 {} 批，本批删除 {} 封", batch + 1, deleted);
        }
        if (totalDeleted > 0) {
            log.info("发件箱清理完成，共删除 {} 封", totalDeleted);
        } else {
            log.info("发件箱清理完成，无待删除邮件");
        }
        return totalDeleted;
    }

    private int deleteBatch(MailAccount account, String username, String password) {
        Store store = null;
        Folder folder = null;
        try {
            Session session = Session.getInstance(imapProperties(account));
            store = session.getStore("imap");
            store.connect(account.getImapHost(), account.getImapPort(), username, password);
            folder = store.getFolder(cleanupProperties.getSentFolder());
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            if (messages.length == 0) {
                return 0;
            }
            int deleted = 0;
            for (Message message : messages) {
                if (deleted >= cleanupProperties.getBatchSize()) {
                    break;
                }
                if (isAppMessage(message)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    deleted++;
                }
            }
            if (deleted == 0) {
                folder.close(false);
                folder = null;
                store.close();
                store = null;
                return 0;
            }
            folder.close(true);
            folder = null;
            store.close();
            store = null;
            return deleted;
        } catch (Exception e) {
            log.error("发件箱清理失败: {}", e.getMessage(), e);
            return 0;
        } finally {
            closeQuietly(folder);
            closeQuietly(store);
        }
    }

    private Properties imapProperties(MailAccount account) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", account.getImapHost());
        properties.put("mail.imap.port", String.valueOf(account.getImapPort()));
        properties.put("mail.imap.ssl.enable", "true");
        return properties;
    }

    private boolean isAppMessage(Message message) throws MessagingException {
        if (headerMatches(message)) {
            return true;
        }
        if (!cleanupProperties.isMatchSubjectKeywords()) {
            return false;
        }
        String subject = message.getSubject();
        if (subject == null) {
            return false;
        }
        List<String> keywords = cleanupProperties.getSubjectKeywords();
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (keyword != null && subject.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean headerMatches(Message message) throws MessagingException {
        String headerName = cleanupProperties.getAppHeader();
        if (!StringUtils.hasText(headerName)) {
            return false;
        }
        String[] values = message.getHeader(headerName);
        if (values == null || values.length == 0) {
            return false;
        }
        String expected = cleanupProperties.getAppHeaderValue();
        if (!StringUtils.hasText(expected)) {
            return true;
        }
        for (String value : values) {
            if (expected.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private String resolveImapPassword(MailAccount account) {
        if (StringUtils.hasText(account.getImapPassword())) {
            return account.getImapPassword();
        }
        return account.getPassword();
    }

    private void closeQuietly(Folder folder) {
        if (folder == null || !folder.isOpen()) {
            return;
        }
        try {
            folder.close(false);
        } catch (Exception ignored) {
        }
    }

    private void closeQuietly(Store store) {
        if (store == null || !store.isConnected()) {
            return;
        }
        try {
            store.close();
        } catch (Exception ignored) {
        }
    }
}
