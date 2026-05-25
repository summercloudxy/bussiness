package com.xy.bussiness.notification;

import com.xy.bussiness.notification.mail.MailProperties;
import com.xy.bussiness.notification.mail.MyMailSender;
import com.xy.bussiness.notification.wechat.WeChatNewsArticle;
import com.xy.bussiness.notification.wechat.WeChatNotifier;
import com.xy.bussiness.notification.wechat.WeChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NotifySender {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MyMailSender mailSender;
    @Autowired
    private WeChatNotifier weChatNotifier;
    @Autowired
    private WeChatProperties weChatProperties;

    public boolean send(String topic, String htmlContent, int tryCount) throws Exception {
        return send(topic, htmlContent, null, null, null, tryCount);
    }

    public boolean send(String topic, String htmlContent, String wechatMarkdown, int tryCount) throws Exception {
        return send(topic, htmlContent, wechatMarkdown, null, null, tryCount);
    }

    /**
     * @param platform 平台标识：{@link com.xy.bussiness.notification.wechat.WeChatPlatform}，为 null 则不推微信
     */
    public boolean send(String topic, String htmlContent, String wechatMarkdown,
                        List<WeChatNewsArticle> wechatNews, String platform, int tryCount) throws Exception {
        boolean mailOk;
        if (mailProperties.isEnabled()) {
            mailOk = mailSender.send(topic, htmlContent, tryCount);
        } else {
            log.debug("邮件通知已关闭，跳过: {}", topic);
            mailOk = true;
        }
        boolean sendWechat = weChatProperties.isPlatformEnabled(platform);
        if (sendWechat && (wechatMarkdown != null || (wechatNews != null && !wechatNews.isEmpty()))) {
            weChatNotifier.send(topic, wechatMarkdown, wechatNews);
        } else if (!sendWechat && platform != null) {
            log.debug("微信通知已关闭平台 [{}]，跳过: {}", platform, topic);
        }
        return mailOk;
    }
}
