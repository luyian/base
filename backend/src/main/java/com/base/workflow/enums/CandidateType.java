package com.base.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 候选人类型枚举
 */
@Getter
@AllArgsConstructor
public enum CandidateType {

    USER("USER", "指定用户"),
    ROLE("ROLE", "指定角色"),
    DEPARTMENT("DEPARTMENT", "指定部门"),
    DEPARTMENT_LEADER("DEPARTMENT_LEADER", "部门负责人"),
    INITIATOR_DEPT("INITIATOR_DEPT", "发起人所属部门"),
    DEPT_UPWARD("DEPT_UPWARD", "发起人部门及上级部门"),
    EXPRESSION("EXPRESSION", "表达式计算");

    private final String code;
    private final String desc;

    public static CandidateType getByCode(String code) {
        for (CandidateType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
