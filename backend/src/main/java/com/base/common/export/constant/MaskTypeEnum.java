package com.base.common.export.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脱敏类型枚举
 *
 * @author base
 * @since 2026-02-04
 */
@Getter
@AllArgsConstructor
public enum MaskTypeEnum {

    /**
     * 手机号脱敏
     */
    PHONE("PHONE", "手机号"),

    /**
     * 身份证脱敏
     */
    ID_CARD("ID_CARD", "身份证"),

    /**
     * 邮箱脱敏
     */
    EMAIL("EMAIL", "邮箱"),

    /**
     * 银行卡脱敏
     */
    BANK_CARD("BANK_CARD", "银行卡"),

    /**
     * 姓名脱敏
     */
    NAME("NAME", "姓名"),

    /**
     * 自定义脱敏
     */
    CUSTOM("CUSTOM", "自定义");

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
    public static MaskTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (MaskTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
