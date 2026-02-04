package com.base.common.export.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导出任务状态枚举
 *
 * @author base
 * @since 2026-02-04
 */
@Getter
@AllArgsConstructor
public enum ExportStatusEnum {

    /**
     * 待处理
     */
    PENDING(0, "待处理"),

    /**
     * 处理中
     */
    PROCESSING(1, "处理中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败
     */
    FAILED(3, "失败"),

    /**
     * 已取消
     */
    CANCELLED(4, "已取消");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举实例
     */
    public static ExportStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExportStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
