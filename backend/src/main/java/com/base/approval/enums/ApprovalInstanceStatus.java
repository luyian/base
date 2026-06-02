package com.base.approval.enums;

/**
 * 审批实例状态
 *
 * @author base
 */
public enum ApprovalInstanceStatus {

    PENDING("PENDING", "审批中"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELED("CANCELED", "已撤回");

    private final String code;
    private final String desc;

    ApprovalInstanceStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == CANCELED;
    }
}
