package com.xy.bussiness.notification.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "mail.cleanup")
public class MailCleanupProperties {

    private boolean enabled = true;

    /** 每天执行时间，默认凌晨 3 点（Asia/Shanghai） */
    private String cron = "0 0 3 * * ?";

    /** 使用哪套邮箱账号：qq | netease */
    private String provider = "qq";

    /** IMAP 发件箱文件夹名，QQ 中文客户端一般为「已发送」 */
    private String sentFolder = "已发送";

    /** 每批标记删除的邮件数 */
    private int batchSize = 200;

    /** 单次任务最多批次数，防止异常死循环 */
    private int maxBatches = 50;

    /** 识别本应用邮件的自定义头（与 {@link MyMailSender} 一致） */
    private String appHeader = MailAppMarker.HEADER_NAME;
    private String appHeaderValue = MailAppMarker.HEADER_VALUE;

    /**
     * 无自定义头时的兜底：主题包含任一关键词则视为本应用通知（兼容历史邮件）。
     */
    private boolean matchSubjectKeywords = true;
    private List<String> subjectKeywords = Arrays.asList(
            "上新啦",
            "的这些商品降价啦",
            "关注的用户上新",
            "关注的用户降价啦"
    );
}
