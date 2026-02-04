package com.base.system.export.dto.field;

import lombok.Data;

/**
 * 导出字段响应
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportFieldResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 导出配置ID
     */
    private Long configId;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段标签
     */
    private String fieldLabel;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段类型描述
     */
    private String fieldTypeDesc;

    /**
     * 列宽度
     */
    private Integer fieldWidth;

    /**
     * 格式化模式
     */
    private String fieldFormat;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 脱敏类型
     */
    private String maskType;

    /**
     * 脱敏类型描述
     */
    private String maskTypeDesc;

    /**
     * 自定义脱敏正则
     */
    private String maskPattern;

    /**
     * 自定义脱敏替换字符
     */
    private String maskReplacement;

    /**
     * 自定义转换器Bean名称
     */
    private String converterBean;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
