package com.base.common.thirdparty;

/**
 * 第三方平台枚举
 *
 * @author base
 */
public enum ThirdPartyPlatform {

    FEISHU("feishu", "飞书"),
    DINGTALK("dingtalk", "钉钉");

    private final String code;
    private final String desc;

    ThirdPartyPlatform(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ThirdPartyPlatform fromCode(String code) {
        for (ThirdPartyPlatform platform : values()) {
            if (platform.code.equals(code)) {
                return platform;
            }
        }
        return null;
    }
}
