package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程状态枚举
 */
@Getter
@AllArgsConstructor
public enum ProcessStatus {

    RUNNING("RUNNING", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    TERMINATED("TERMINATED", "已终止"),
    ROLLED_BACK("ROLLED_BACK", "已回退");

    private final String code;
    private final String desc;

    public static ProcessStatus getByCode(String code) {
        for (ProcessStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
