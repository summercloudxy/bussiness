package com.xy.bussiness.notification.wechat;

import com.alibaba.fastjson.JSON;
import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.WeChatApi;
import io.github.biezhi.wechat.api.WeChatApiImpl;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.model.LoginSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class WechatSender {

    WeChatApi api;
    WeChatBot helloBot;

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    public static final String SESSION_KEY = "WECHAT_SESSION";

//    @PostConstruct
    public void init(){
        helloBot = new WeChatBot(Config.load("/application.properties").autoLogin(true).showTerminal(true));
        api = new WeChatApiImpl(helloBot);
        Thread thread = new Thread(this::loadSession);
        thread.start();
    }

//    @PostConstruct
    public void login(){
        api.login(true);
        LoginSession session = helloBot.session();
        redisTemplate.opsForValue().set(SESSION_KEY, JSON.toJSONString(session));
    }


    public void loadSession(){
        String sessionStr = redisTemplate.opsForValue().get(SESSION_KEY);
        if (StringUtils.isNotBlank(sessionStr)) {
            LoginSession loginSession = JSON.parseObject(sessionStr, LoginSession.class);
            helloBot.setSession(loginSession);
        }else {
            login();
        }
    }


    public void logout(){
        if (api != null) {
            api.logout();
            redisTemplate.delete(SESSION_KEY);
        }
    }

    public void sendMsg(String toUser,String msg){
        if (api != null) {
            if (StringUtils.isBlank(toUser)){
                toUser = "filehelper";
            }
            api.sendText(toUser, msg);
        }
    }



    
}