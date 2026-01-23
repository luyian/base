package com.base.system.dto.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 全局变量保存请求参数
 */
@Data
public class ConfigSaveRequest {

    /**
     * 配置ID（编辑时必填）
     */
    private Long id;

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(min = 2, max = 50, message = "配置键长度必须在2-50个字符之间")
    private String configKey;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    @Size(max = 500, message = "配置值长度不能超过500个字符")
    private String configValue;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    @Size(min = 2, max = 50, message = "配置名称长度必须在2-50个字符之间")
    private String configName;

    /**
     * 参数类型（string、number、boolean、json）
     */
    @NotBlank(message = "参数类型不能为空")
    private String type;

    /**
     * 状态（0-禁用 1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
