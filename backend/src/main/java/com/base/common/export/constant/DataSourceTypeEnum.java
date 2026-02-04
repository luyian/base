package com.base.common.export.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源类型枚举
 *
 * @author base
 * @since 2026-02-04
 */
@Getter
@AllArgsConstructor
public enum DataSourceTypeEnum {

    /**
     * 服务方法
     */
    SERVICE("SERVICE", "服务方法"),

    /**
     * 自定义SQL
     */
    SQL("SQL", "自定义SQL");

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
    public static DataSourceTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DataSourceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
