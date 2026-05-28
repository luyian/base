package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点类型枚举
 */
@Getter
@AllArgsConstructor
public enum NodeType {

    START("START", "开始节点"),
    END("END", "结束节点"),
    APPROVAL("APPROVAL", "审批节点"),
    CONDITION("CONDITION", "条件网关"),
    PARALLEL("PARALLEL", "并行网关"),
    SUB_PROCESS("SUB_PROCESS", "子流程");

    private final String code;
    private final String desc;

    public static NodeType getByCode(String code) {
        for (NodeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
