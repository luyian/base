package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批类型枚举
 */
@Getter
@AllArgsConstructor
public enum ApproveType {

    SEQUENCE("SEQUENCE", "顺序审批"),
    COUNTER_SIGN("COUNTER_SIGN", "会签"),
    OR_SIGN("OR_SIGN", "或签");

    private final String code;
    private final String desc;

    public static ApproveType getByCode(String code) {
        for (ApproveType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
