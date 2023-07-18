package com.xy.bussiness.controller;

import com.xy.bussiness.notification.wechat.WechatSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat")
public class WechatSenderTestController {
    @Autowired
    private WechatSender wechatSender;


    @GetMapping("/login")
    public void login(){
        wechatSender.login();
    }


    @GetMapping("/logout")
    public void logout(){
        wechatSender.logout();
    }


    @GetMapping("/send")
    public void sendMsg(String toUser,String msg){
        wechatSender.sendMsg(toUser, msg);
    }
}
