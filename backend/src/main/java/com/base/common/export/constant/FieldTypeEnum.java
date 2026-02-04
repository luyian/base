package com.base.common.export.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段类型枚举
 *
 * @author base
 * @since 2026-02-04
 */
@Getter
@AllArgsConstructor
public enum FieldTypeEnum {

    /**
     * 字符串
     */
    STRING("STRING", "字符串"),

    /**
     * 数字
     */
    NUMBER("NUMBER", "数字"),

    /**
     * 日期
     */
    DATE("DATE", "日期"),

    /**
     * 日期时间
     */
    DATETIME("DATETIME", "日期时间"),

    /**
     * 布尔值
     */
    BOOLEAN("BOOLEAN", "布尔值");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param code 类型编码
     * @return 枚举实例
     */
    public static FieldTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (FieldTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
