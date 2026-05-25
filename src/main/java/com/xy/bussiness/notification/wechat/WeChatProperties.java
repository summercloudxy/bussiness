package com.xy.bussiness.notification.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@Data
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

    private static final String WEWORK_WEBHOOK_PREFIX = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    private boolean enabled = false;

    /** pushplus：个人微信推送；wework：企业微信群机器人 */
    private String type = "pushplus";

    /** PushPlus token，见 https://www.pushplus.plus */
    private String token;

    /** 企业微信群机器人 webhook 的 key，或完整 webhook URL */
    private String webhookKey;

    /** 各平台是否发送微信（默认仅煤炉） */
    private Platforms platforms = new Platforms();

    public boolean isPlatformEnabled(String platform) {
        if (!enabled || !StringUtils.hasText(platform)) {
            return false;
        }
        switch (platform.trim().toLowerCase()) {
            case WeChatPlatform.MERCARI:
                return platforms.isMercari();
            case WeChatPlatform.YAHOO:
                return platforms.isYahoo();
            case WeChatPlatform.RAKUTEN:
                return platforms.isRakuten();
            default:
                return false;
        }
    }

    @Data
    public static class Platforms {
        private boolean mercari = true;
        private boolean yahoo = false;
        private boolean rakuten = false;
    }

    public String resolveWeworkWebhookUrl() {
        if (!StringUtils.hasText(webhookKey)) {
            return null;
        }
        String value = webhookKey.trim();
        if (value.contains("qyapi.weixin.qq.com")) {
            return value;
        }
        return WEWORK_WEBHOOK_PREFIX + value;
    }
}
