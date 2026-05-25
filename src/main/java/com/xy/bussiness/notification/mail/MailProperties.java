package com.xy.bussiness.notification.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    /** 是否发送邮件通知 */
    private boolean enabled = true;

    /** 发信 SMTP 账号：qq | netease（163） */
    private String from = "qq";
    /** 收信邮箱账号：qq | netease（163），仅使用其 username 作为收件地址 */
    private String to = "netease";

    private MailAccount qq = new MailAccount();
    private MailAccount netease = new MailAccount();

    public MailAccount account(String provider) {
        String key = provider == null ? "" : provider.trim().toLowerCase();
        if ("qq".equals(key)) {
            return qq;
        }
        if ("netease".equals(key) || "163".equals(key)) {
            return netease;
        }
        throw new IllegalArgumentException("未知邮箱提供商: " + provider + "，可选: qq, netease(163)");
    }

    public String fromAddress() {
        return account(from).getUsername();
    }

    public String toAddress() {
        return account(to).getUsername();
    }
}
