package com.base.common.export.mask;

/**
 * 数据脱敏器接口
 *
 * @author base
 * @since 2026-02-04
 */
public interface DataMasker {

    /**
     * 脱敏处理
     *
     * @param value 原始值
     * @return 脱敏后的值
     */
    String mask(String value);

    /**
     * 获取脱敏类型
     *
     * @return 脱敏类型
     */
    String getMaskType();
}
