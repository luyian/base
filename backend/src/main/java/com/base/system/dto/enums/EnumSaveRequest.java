package com.base.system.dto.enums;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 枚举保存请求参数
 */
@Data
public class EnumSaveRequest {

    /**
     * 枚举ID（编辑时必填）
     */
    private Long id;

    /**
     * 枚举类型
     */
    @NotBlank(message = "枚举类型不能为空")
    private String enumType;

    /**
     * 枚举值
     */
    @NotBlank(message = "枚举值不能为空")
    private String enumValue;

    /**
     * 枚举标签
     */
    @NotBlank(message = "枚举标签不能为空")
    private String enumLabel;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 状态（0-禁用 1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
