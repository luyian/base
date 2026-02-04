package com.base.system.export.dto.field;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 导出字段保存请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportFieldSaveRequest {

    /**
     * 主键ID（编辑时必填）
     */
    private Long id;

    /**
     * 导出配置ID
     */
    private Long configId;

    /**
     * 字段名
     */
    @NotBlank(message = "字段名不能为空")
    @Size(max = 100, message = "字段名长度不能超过100个字符")
    private String fieldName;

    /**
     * 字段标签（Excel列标题）
     */
    @NotBlank(message = "字段标签不能为空")
    @Size(max = 100, message = "字段标签长度不能超过100个字符")
    private String fieldLabel;

    /**
     * 字段类型
     */
    private String fieldType = "STRING";

    /**
     * 列宽度
     */
    private Integer fieldWidth = 20;

    /**
     * 格式化模式
     */
    @Size(max = 50, message = "格式化模式长度不能超过50个字符")
    private String fieldFormat;

    /**
     * 字典类型
     */
    @Size(max = 50, message = "字典类型长度不能超过50个字符")
    private String dictType;

    /**
     * 脱敏类型
     */
    @Size(max = 20, message = "脱敏类型长度不能超过20个字符")
    private String maskType;

    /**
     * 自定义脱敏正则
     */
    @Size(max = 100, message = "自定义脱敏正则长度不能超过100个字符")
    private String maskPattern;

    /**
     * 自定义脱敏替换字符
     */
    @Size(max = 50, message = "自定义脱敏替换字符长度不能超过50个字符")
    private String maskReplacement;

    /**
     * 自定义转换器Bean名称
     */
    @Size(max = 100, message = "自定义转换器Bean名称长度不能超过100个字符")
    private String converterBean;

    /**
     * 默认值
     */
    @Size(max = 100, message = "默认值长度不能超过100个字符")
    private String defaultValue;

    /**
     * 排序
     */
    private Integer sort = 0;

    /**
     * 状态
     */
    private Integer status = 1;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
