package com.xy.bussiness.notification.wechat;

/**
 * 企业微信 markdown 消息中的三个链接：原网站、购买、关注。
 */
public final class WeChatMarkdownLinks {

    private WeChatMarkdownLinks() {
    }

    public static void appendThreeLinks(StringBuilder sb,
                                        String siteUrl,
                                        String buyUrl,
                                        String interestUrl,
                                        String interestLabel) {
        sb.append("- [原网站](").append(siteUrl).append(")\n");
        sb.append("- [顺通购买](").append(buyUrl).append(")\n");
        sb.append("- [").append(interestLabel).append("](").append(interestUrl).append(")\n");
    }
}
