package com.base.message.constant;

/**
 * 推送渠道枚举
 *
 * @author base
 */
public enum ChannelEnum {

    /** 飞书 */
    FEISHU("feishu", "飞书"),
    /** 钉钉 */
    DINGTALK("dingtalk", "钉钉"),
    /** 邮件 */
    EMAIL("email", "邮件");

    private final String code;
    private final String desc;

    ChannelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
