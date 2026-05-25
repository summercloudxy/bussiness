package com.xy.bussiness.notification.mail;

/**
 * 标识由本应用发出的通知邮件，供发件箱清理识别。
 */
public final class MailAppMarker {

    public static final String HEADER_NAME = "X-Bussiness-Notification";
    public static final String HEADER_VALUE = "1";

    private MailAppMarker() {
    }
}
