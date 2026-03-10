package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程定义状态枚举
 */
@Getter
@AllArgsConstructor
public enum DefinitionStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    DISABLED(2, "禁用");

    private final Integer code;
    private final String desc;

    public static DefinitionStatus getByCode(Integer code) {
        for (DefinitionStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
