package com.base.stock.factory;

import lombok.Data;

/**
 * 字段映射配置
 *
 * @author base
 */
@Data
public class FieldMapping {

    /**
     * 源字段名
     */
    private String source;

    /**
     * 目标字段名
     */
    private String target;

    /**
     * 字段类型（string, int, long, decimal, date, datetime）
     */
    private String type;

    /**
     * 小数位数（decimal 类型使用）
     */
    private Integer scale;

    /**
     * 日期格式（date, datetime 类型使用）
     */
    private String dateFormat;

    /**
     * 默认值
     */
    private String defaultValue;
}
