package com.base.approval.enums;

/**
 * 事件回调处理状态
 *
 * @author base
 */
public enum EventCallbackStatus {

    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "处理成功"),
    FAILED("FAILED", "处理失败"),
    SKIPPED("SKIPPED", "已跳过");

    private final String code;
    private final String desc;

    EventCallbackStatus(String code, String desc) {
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
