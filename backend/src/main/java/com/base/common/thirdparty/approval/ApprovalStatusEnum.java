package com.base.common.thirdparty.approval;

/**
 * 通用审批状态枚举
 *
 * @author base
 */
public enum ApprovalStatusEnum {

    PENDING("PENDING", "审批中"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELED("CANCELED", "已撤回"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String desc;

    ApprovalStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ApprovalStatusEnum fromCode(String code) {
        for (ApprovalStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
