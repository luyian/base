package com.base.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围枚举
 *
 * @author base
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * 全部数据
     */
    ALL(1, "全部数据"),

    /**
     * 本部门及以下
     */
    DEPT_AND_BELOW(2, "本部门及以下"),

    /**
     * 本部门
     */
    DEPT(3, "本部门"),

    /**
     * 仅本人
     */
    SELF(4, "仅本人"),

    /**
     * 自定义
     */
    CUSTOM(5, "自定义");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据编码获取描述
     *
     * @param code 编码
     * @return 描述
     */
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (DataScopeEnum scope : values()) {
            if (scope.getCode().equals(code)) {
                return scope.getDesc();
            }
        }
        return "未知";
    }
}
