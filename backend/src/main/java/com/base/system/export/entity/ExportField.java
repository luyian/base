package com.base.system.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.system.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出字段配置实体类
 *
 * @author base
 * @since 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_export_field")
public class ExportField extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 导出配置ID
     */
    private Long configId;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段标签（Excel列标题）
     */
    private String fieldLabel;

    /**
     * 字段类型（STRING/NUMBER/DATE/DATETIME/BOOLEAN）
     */
    private String fieldType;

    /**
     * 列宽度
     */
    private Integer fieldWidth;

    /**
     * 格式化模式（如日期格式：yyyy-MM-dd）
     */
    private String fieldFormat;

    /**
     * 字典类型（关联sys_enum表的enum_type）
     */
    private String dictType;

    /**
     * 脱敏类型（PHONE/ID_CARD/EMAIL/BANK_CARD/NAME/CUSTOM）
     */
    private String maskType;

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
     * 默认值（字段值为空时使用）
     */
    private String defaultValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
