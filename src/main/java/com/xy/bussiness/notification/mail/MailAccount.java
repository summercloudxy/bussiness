package com.xy.bussiness.notification.mail;

import lombok.Data;

@Data
public class MailAccount {

    private String host;
    private int port = 465;
    private String username;
    private String password;

    /** IMAP 清理发件箱用，默认 imap.qq.com */
    private String imapHost = "imap.qq.com";
    private int imapPort = 993;
    /** 未配置时复用 password（QQ 需开启 IMAP 并使用授权码） */
    private String imapPassword;
}
