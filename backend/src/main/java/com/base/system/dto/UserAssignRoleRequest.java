package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户分配角色请求参数
 */
@Data
@ApiModel(description = "用户分配角色请求参数")
public class UserAssignRoleRequest {

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @ApiModelProperty(value = "角色ID列表", required = true)
    @NotEmpty(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}
