package com.xy.bussiness.notification.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class WeChatNotifier {

    private static final String PUSHPLUS_URL = "http://www.pushplus.plus/send";
    /** 企业微信图文消息每条最多 8 篇 */
    private static final int WEWORK_NEWS_BATCH_SIZE = 8;

    @Autowired
    private WeChatProperties properties;
    @Autowired
    @Qualifier("httpsRestTemplate")
    private RestTemplate httpsRestTemplate;

    public void send(String title, String markdownContent) {
        send(title, markdownContent, null);
    }

    public void send(String title, String markdownContent, List<WeChatNewsArticle> newsArticles) {
        if (!properties.isEnabled()) {
            return;
        }
        boolean hasNews = !CollectionUtils.isEmpty(newsArticles);
        if (!hasNews && !StringUtils.hasText(markdownContent)) {
            return;
        }
        try {
            if ("wework".equalsIgnoreCase(properties.getType())) {
                // news 仅支持卡片主链接一个；三个可点击链接走 markdown
                if (hasNews) {
                    sendWeworkNews(newsArticles);
                }
                if (StringUtils.hasText(markdownContent)) {
                    sendWeworkMarkdown(title, markdownContent);
                }
            } else if (StringUtils.hasText(markdownContent)) {
                sendPushPlus(title, markdownContent);
            }
        } catch (Exception e) {
            log.warn("微信通知发送失败: {}", title, e);
        }
    }

    private void sendPushPlus(String title, String content) {
        if (!StringUtils.hasText(properties.getToken())) {
            log.warn("微信 PushPlus 未配置 token，跳过发送");
            return;
        }
        JSONObject body = new JSONObject();
        body.put("token", properties.getToken());
        body.put("title", title);
        body.put("content", content);
        body.put("template", "markdown");
        postJson(PUSHPLUS_URL, body);
    }

    private void sendWeworkMarkdown(String title, String content) {
        String webhookUrl = properties.resolveWeworkWebhookUrl();
        if (!StringUtils.hasText(webhookUrl)) {
            log.warn("微信企业微信未配置 webhook-key，跳过发送");
            return;
        }
        String markdown = "## " + title + "\n\n" + content;
        JSONObject markdownObj = new JSONObject();
        markdownObj.put("content", markdown);
        JSONObject body = new JSONObject();
        body.put("msgtype", "markdown");
        body.put("markdown", markdownObj);
        String response = postJson(webhookUrl, body);
        checkWeworkResponse(response);
    }

    private void sendWeworkNews(List<WeChatNewsArticle> articles) {
        String webhookUrl = properties.resolveWeworkWebhookUrl();
        if (!StringUtils.hasText(webhookUrl)) {
            log.warn("微信企业微信未配置 webhook-key，跳过发送");
            return;
        }
        for (int i = 0; i < articles.size(); i += WEWORK_NEWS_BATCH_SIZE) {
            int end = Math.min(i + WEWORK_NEWS_BATCH_SIZE, articles.size());
            JSONArray articleArray = new JSONArray();
            for (int j = i; j < end; j++) {
                WeChatNewsArticle article = articles.get(j);
                if (!StringUtils.hasText(article.getPicurl())) {
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("title", truncateUtf8(article.getTitle(), 128));
                item.put("description", truncateUtf8(article.getDescription(), 512));
                item.put("url", article.getUrl());
                item.put("picurl", article.getPicurl());
                articleArray.add(item);
            }
            if (articleArray.isEmpty()) {
                continue;
            }
            JSONObject news = new JSONObject();
            news.put("articles", articleArray);
            JSONObject body = new JSONObject();
            body.put("msgtype", "news");
            body.put("news", news);
            String response = postJson(webhookUrl, body);
            checkWeworkResponse(response);
        }
    }

    static String truncateUtf8(String text, int maxBytes) {
        if (text == null) {
            return "";
        }
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return text;
        }
        int len = maxBytes;
        while (len > 0 && (bytes[len] & 0xC0) == 0x80) {
            len--;
        }
        return new String(bytes, 0, len, StandardCharsets.UTF_8);
    }

    private void checkWeworkResponse(String response) {
        if (!StringUtils.hasText(response)) {
            throw new IllegalStateException("企业微信无响应体");
        }
        JSONObject json = JSONObject.parseObject(response);
        int errcode = json.getIntValue("errcode");
        if (errcode != 0) {
            throw new IllegalStateException("企业微信返回 errcode=" + errcode + ", errmsg=" + json.getString("errmsg"));
        }
    }

    String postJson(String url, JSONObject body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
        log.info("微信通知请求 url={} body={}", url, body.toJSONString());
        String response = httpsRestTemplate.postForObject(url, entity, String.class);
        log.info("微信通知响应: {}", response);
        return response;
    }
}
