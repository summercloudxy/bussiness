package com.xy.bussiness.notification.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.xy.bussiness.UrlConstants.SHUNTONG_MEILU_URL;

/**
 * 企业微信 webhook 调试。
 * <p>
 * 运行集成测试前请确认 application.yml：wechat.enabled=true、type=wework、webhook-key 已配置。
 * 可通过 JVM 参数指定真实商品 ID：-Dwechat.test.itemId=mxxxxxxxxxx
 */
@SpringBootTest(properties = {
        "mercari.enable=false",
        "rakuten.enable=false",
        "yahoo.enable=false",
        "network.proxy.enabled=false"
})
class WeChatNotifierTest {

    private static final String DEFAULT_ITEM_ID = "m12345678901";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private WeChatNotifier weChatNotifier;
    @Autowired
    @Qualifier("httpsRestTemplate")
    private RestTemplate httpsRestTemplate;

    // ---------- 本地校验（不发请求） ----------

    @Test
    void mercariWeChatPicUrl_shouldUseJpgNotWebp() {
        String url = NotifyImageUrls.mercariThumbUrlForWeChat("m999");
        Assertions.assertTrue(url.startsWith("https://"));
        Assertions.assertFalse(url.contains("webp"), "企业微信 picurl 不支持 webp: " + url);
        Assertions.assertTrue(url.endsWith("_1.jpg"));
    }

    @Test
    void pickWeChatPicUrl_shouldRejectWebp() {
        Assertions.assertNull(NotifyImageUrls.pickWeChatPicUrl(
                "https://static.mercdn.net/c!/w=240,f=webp/thumb/photos/m1_1.jpg"));
        Assertions.assertNotNull(NotifyImageUrls.pickWeChatPicUrl(
                "https://static.mercdn.net/c!/w=240/thumb/photos/m1_1.jpg"));
    }

    // ---------- 配置检查 ----------

    @Test
    void printWeChatConfig() {
        System.out.println("=== wechat 配置 ===");
        System.out.println("enabled = " + weChatProperties.isEnabled());
        System.out.println("type = " + weChatProperties.getType());
        System.out.println("webhookKey(raw) = " + weChatProperties.getWebhookKey());
        System.out.println("webhookUrl(resolved) = " + weChatProperties.resolveWeworkWebhookUrl());
        System.out.println("test itemId = " + testItemId());
        System.out.println("weChat picurl = " + NotifyImageUrls.resolveMercariPicUrl(testItemId(), null));
        System.out.println("platforms.mercari = " + weChatProperties.getPlatforms().isMercari());
        System.out.println("platforms.yahoo = " + weChatProperties.getPlatforms().isYahoo());
        System.out.println("platforms.rakuten = " + weChatProperties.getPlatforms().isRakuten());
    }

    // ---------- 集成测试（会真实推送到企业微信群） ----------

    /**
     * 与线上一致：先发带首图的 news，再发含三个链接的 markdown。
     */
    @Test
    void sendLikeProduction_newsAndMarkdown() {
        assumeWeworkConfigured();
        String itemId = testItemId();
        String siteUrl = "https://jp.mercari.com/item/" + itemId;
        String buyUrl = SHUNTONG_MEILU_URL + itemId;
        String interestUrl = "https://example.com/mercari/setInterest?interest=1&itemId=" + itemId;

        WeChatNewsArticle article = WeChatNewsArticle.builder()
                .title("煤炉图文测试")
                .description("成色：全新\n价格：1000\n点击下方消息中的链接操作")
                .url(siteUrl)
                .picurl(NotifyImageUrls.resolveMercariPicUrl(itemId, null))
                .build();

        StringBuilder markdown = new StringBuilder();
        markdown.append("### 煤炉图文测试\n");
        markdown.append("- 成色：全新\n");
        markdown.append("- 价格：1000\n");
        WeChatMarkdownLinks.appendThreeLinks(markdown, siteUrl, buyUrl, interestUrl, "添加关注");

        String topic = "煤炉:测试条件 上新啦";
        System.out.println("picurl = " + article.getPicurl());
        weChatNotifier.send(topic, markdown.toString(), Collections.singletonList(article));
        System.out.println("=== 已发送 news + markdown，请在群里确认首图与三个链接 ===");
    }

    @Test
    void sendNewsOnly_viaNotifier() {
        assumeWeworkConfigured();
        String itemId = testItemId();
        WeChatNewsArticle article = WeChatNewsArticle.builder()
                .title("仅图文测试")
                .description("价格：1000")
                .url("https://jp.mercari.com/item/" + itemId)
                .picurl(NotifyImageUrls.resolveMercariPicUrl(itemId, null))
                .build();
        weChatNotifier.send("仅图文", null, Collections.singletonList(article));
    }

    @Test
    void sendMarkdownOnly_viaNotifier() {
        assumeWeworkConfigured();
        String itemId = testItemId();
        StringBuilder sb = new StringBuilder();
        sb.append("### 仅链接测试\n");
        WeChatMarkdownLinks.appendThreeLinks(sb,
                "https://jp.mercari.com/item/" + itemId,
                SHUNTONG_MEILU_URL + itemId,
                "https://example.com/mercari/setInterest?interest=1&itemId=" + itemId,
                "添加关注");
        weChatNotifier.send("仅 Markdown", sb.toString());
    }

    @Test
    void sendNewsDirectApi_jpgPicurl() {
        assumeWeworkConfigured();
        String itemId = testItemId();
        String picurl = NotifyImageUrls.resolveMercariPicUrl(itemId, null);
        Assertions.assertNotNull(picurl);
        Assertions.assertFalse(picurl.contains("webp"));

        JSONObject item = new JSONObject();
        item.put("title", "直连 news（JPG 首图）");
        item.put("description", "价格：1000");
        item.put("url", "https://jp.mercari.com/item/" + itemId);
        item.put("picurl", picurl);

        JSONArray articles = new JSONArray();
        articles.add(item);
        JSONObject news = new JSONObject();
        news.put("articles", articles);
        JSONObject body = new JSONObject();
        body.put("msgtype", "news");
        body.put("news", news);

        String response = postWebhook(body);
        assertWeworkOk(response);
        System.out.println("picurl=" + picurl);
        System.out.println(response);
    }

    @Test
    void sendMarkdownDirectApi() {
        assumeWeworkConfigured();
        JSONObject markdownObj = new JSONObject();
        markdownObj.put("content", "## 直连 markdown\n\n- [原网站](https://jp.mercari.com/item/" + testItemId() + ")");
        JSONObject body = new JSONObject();
        body.put("msgtype", "markdown");
        body.put("markdown", markdownObj);
        assertWeworkOk(postWebhook(body));
    }

    private void assumeWeworkConfigured() {
        Assumptions.assumeTrue(weChatProperties.isEnabled(), "wechat.enabled=false，跳过集成测试");
        Assumptions.assumeTrue("wework".equalsIgnoreCase(weChatProperties.getType()), "非 wework，跳过");
        Assumptions.assumeTrue(weChatProperties.resolveWeworkWebhookUrl() != null, "未配置 webhook-key");
    }

    private String testItemId() {
        String fromProp = System.getProperty("wechat.test.itemId");
        return StringUtils.hasText(fromProp) ? fromProp.trim() : DEFAULT_ITEM_ID;
    }

    private String postWebhook(JSONObject body) {
        String webhookUrl = weChatProperties.resolveWeworkWebhookUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
        System.out.println("POST " + webhookUrl);
        System.out.println("body: " + body.toJSONString());
        return httpsRestTemplate.postForObject(webhookUrl, entity, String.class);
    }

    private static void assertWeworkOk(String response) {
        JSONObject json = JSONObject.parseObject(response);
        Assertions.assertEquals(0, json.getIntValue("errcode"),
                "企业微信错误: " + json.getString("errmsg") + ", response=" + response);
    }
}
