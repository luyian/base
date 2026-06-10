package com.base.system.dto.dict;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 字典数据保存请求参数
 *
 * @author base
 * @since 2026-06-09
 */
@Data
public class DictDataSaveRequest {

    /**
     * 主键ID（编辑时必填）
     */
    private Long id;

    /**
     * 字典类型编码
     */
    @NotBlank(message = "字典类型编码不能为空")
    private String dictType;

    /**
     * 字典标签（显示值）
     */
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    /**
     * 字典键值（编码）
     */
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;

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
