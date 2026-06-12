package com.base.dev.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分支紧急程度枚举
 *
 * @author base
 */
@Getter
@AllArgsConstructor
public enum PriorityEnum {

    /**
     * 普通
     */
    NORMAL(0, "普通"),

    /**
     * 紧急
     */
    URGENT(1, "紧急"),

    /**
     * 特急
     */
    CRITICAL(2, "特急");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值，不存在返回 NORMAL
     */
    public static PriorityEnum getByCode(Integer code) {
        if (code == null) {
            return NORMAL;
        }
        for (PriorityEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return NORMAL;
    }
}
