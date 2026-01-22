package com.base.common.enums;

import lombok.Getter;

/**
 * 数据权限范围枚举
 *
 * @author base
 * @since 2026-01-13
 */
@Getter
public enum DataScopeEnum {

    /**
     * 全部数据权限
     */
    ALL(1, "全部数据权限"),

    /**
     * 自定义数据权限
     */
    CUSTOM(2, "自定义数据权限"),

    /**
     * 本部门数据权限
     */
    DEPT(3, "本部门数据权限"),

    /**
     * 本部门及以下数据权限
     */
    DEPT_AND_CHILD(4, "本部门及以下数据权限"),

    /**
     * 仅本人数据权限
     */
    SELF(5, "仅本人数据权限");

    private final Integer code;
    private final String name;

    DataScopeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DataScopeEnum getByCode(Integer code) {
        for (DataScopeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
