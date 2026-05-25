package com.xy.bussiness.notification.wechat;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeChatProperties.class)
public class WeChatConfig {
}
