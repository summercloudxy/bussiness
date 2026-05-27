package com.xy.bussiness.notification.wechat;

import org.springframework.util.StringUtils;

/**
 * 商品首图 URL。邮件可用 webp；企业微信 news 的 picurl 仅支持 JPG/PNG。
 */
public final class NotifyImageUrls {

    /** 邮件/HTML 用（与页面一致） */
    private static final String MERCARI_THUMB_WEBP =
            "https://static.mercdn.net/c!/w=240,f=webp/thumb/photos/";

    /** 企业微信图文 picurl（去掉 webp 变换） */
    private static final String MERCARI_THUMB_JPG =
            "https://static.mercdn.net/c!/w=240/thumb/photos/";

    private NotifyImageUrls() {
    }

    public static String mercariThumbUrl(String mercariItemId) {
        return MERCARI_THUMB_WEBP + mercariItemId + "_1.jpg";
    }

    /**
     * 管理页/邮件 HTML 展示用缩略图，规则与 NotificationService 邮件正文一致。
     */
    public static String resolveMercariHtmlImageUrl(String mercariItemId, String itemType, String imageUrl) {
        if ("ITEM_TYPE_MERCARI".equalsIgnoreCase(itemType) || !StringUtils.hasText(itemType)) {
            return mercariThumbUrl(mercariItemId);
        }
        String normalized = normalizePicUrl(imageUrl);
        if (normalized != null) {
            return normalized;
        }
        if (StringUtils.hasText(mercariItemId)) {
            return mercariThumbUrl(mercariItemId);
        }
        return null;
    }

    public static String mercariThumbUrlForWeChat(String mercariItemId) {
        return MERCARI_THUMB_JPG + mercariItemId + "_1.jpg";
    }

    /**
     * 煤炉商品：优先 API 缩略图（非 webp），否则用 JPG 首图。
     */
    public static String resolveMercariPicUrl(String mercariItemId, String imageUrl) {
        String fromApi = pickWeChatPicUrl(imageUrl);
        if (fromApi != null) {
            return fromApi;
        }
        if (StringUtils.hasText(mercariItemId)) {
            return mercariThumbUrlForWeChat(mercariItemId);
        }
        return null;
    }

    public static String pickWeChatPicUrl(String url) {
        String normalized = normalizePicUrl(url);
        if (normalized == null || containsWebp(normalized)) {
            return null;
        }
        return normalized;
    }

    public static String normalizePicUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("//")) {
            return "https:" + trimmed;
        }
        if (trimmed.startsWith("http://")) {
            return "https://" + trimmed.substring(7);
        }
        return trimmed;
    }

    private static boolean containsWebp(String url) {
        String lower = url.toLowerCase();
        return lower.contains("webp") || lower.contains("f=webp");
    }
}
