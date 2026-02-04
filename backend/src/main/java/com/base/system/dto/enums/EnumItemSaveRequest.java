package com.base.system.dto.enums;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 枚举项保存请求参数（用于批量保存）
 */
@Data
public class EnumItemSaveRequest {

    /**
     * 枚举ID（编辑时有值，新增时为空）
     */
    private Long id;

    /**
     * 枚举编码
     */
    @NotBlank(message = "枚举编码不能为空")
    private String enumCode;

    /**
     * 枚举值/显示文本
     */
    @NotBlank(message = "枚举值不能为空")
    private String enumValue;

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
}
