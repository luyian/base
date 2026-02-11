package com.base.message.constant;

/**
 * 订阅类型枚举
 *
 * @author base
 */
public enum SubscriptionTypeEnum {

    /** 基金估值 */
    FUND_VALUATION("fund_valuation", "基金估值");

    private final String code;
    private final String desc;

    SubscriptionTypeEnum(String code, String desc) {
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
