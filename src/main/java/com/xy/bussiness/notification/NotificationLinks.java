package com.xy.bussiness.notification;

/**
 * 通知邮件/微信中的公共链接。
 * notification.host 须含协议前缀，如 https://example.com:8111
 */
public final class NotificationLinks {

    private NotificationLinks() {
    }

    public static String url(String notificationHost, String path) {
        String base = notificationHost == null ? "" : notificationHost.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (path == null || path.isEmpty()) {
            return base;
        }
        return path.startsWith("/") ? base + path : base + "/" + path;
    }

    public static String disableKeywordUrl(String notificationHost, String platform, Integer conditionId) {
        return url(notificationHost, "/" + platform + "/disableSearchCondition?conditionId=" + conditionId);
    }

    public static void appendDisableKeywordMailFooter(StringBuilder html, String notificationHost,
                                                        String platform, Integer conditionId, String description) {
        html.append("<div style='margin-top:24px;padding-top:12px;border-top:1px solid #ddd'>");
        html.append("<a href='").append(disableKeywordUrl(notificationHost, platform, conditionId)).append("'>");
        html.append("不再关注该关键字");
        if (description != null && !description.isEmpty()) {
            html.append("（").append(description).append("）");
        }
        html.append("</a></div>");
    }

    public static void appendDisableKeywordMarkdown(StringBuilder markdown, String notificationHost,
                                                      String platform, Integer conditionId, String description) {
        markdown.append("\n---\n[不再关注该关键字");
        if (description != null && !description.isEmpty()) {
            markdown.append("（").append(description).append("）");
        }
        markdown.append("](").append(disableKeywordUrl(notificationHost, platform, conditionId)).append(")\n");
    }
}
