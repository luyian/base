package com.base.system.dto.enums;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 枚举类型批量保存请求参数
 */
@Data
public class EnumTypeBatchSaveRequest {

    /**
     * 枚举类型
     */
    @NotBlank(message = "枚举类型不能为空")
    private String enumType;

    /**
     * 枚举类型中文描述
     */
    private String typeDesc;

    /**
     * 枚举项列表
     */
    @NotEmpty(message = "枚举项列表不能为空")
    @Valid
    private List<EnumItemSaveRequest> items;
}
