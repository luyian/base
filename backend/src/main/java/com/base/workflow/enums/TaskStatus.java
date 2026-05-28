package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING("PENDING", "待办"),
    COMPLETED("COMPLETED", "已办"),
    REJECTED("REJECTED", "已拒绝"),
    DELEGATED("DELEGATED", "已转办");

    private final String code;
    private final String desc;

    public static TaskStatus getByCode(String code) {
        for (TaskStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
