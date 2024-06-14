package com.xy.bussiness.notification.mail;

import javax.mail.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

// 删除发件箱
public class ImapEmailDownloader {
    public static void main(String[] args) {
        // 邮件服务器配置
        String host = "";
        String username = "469654418@qq.com";
        String password = "hwkbyihwtwghbhcb";
        // 连接属性配置
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", "imap.qq.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        //本地文件夹路径，请换成实际的文件夹路径。
        String folderPath = "D:\\文件夹1\\文件夹1-1";


        for (int a =0; a<800 ;a ++) {
            try {
                // 创建会话对象
                Session session = Session.getDefaultInstance(properties);
                // 连接到邮件服务器
                Store store = session.getStore();
                store.connect(username, password);
                // 打开收件箱
                Folder inbox = store.getFolder("已发送");
                inbox.open(Folder.READ_WRITE);
                // 获取邮件列表
                Message[] messages = inbox.getMessages();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = "20220101";
                // 遍历邮件并下载EML格式的邮件
                int i = 1;
                for (Message message : messages) {
                    if (message.getSentDate().after(simpleDateFormat.parse(date))) {
                        message.setFlag(Flags.Flag.DELETED, true);
                        i++;
                    }
                    if (i == 1000) {
                        break;
                    }
                }
                // 关闭连接
                inbox.close(false);
                store.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}