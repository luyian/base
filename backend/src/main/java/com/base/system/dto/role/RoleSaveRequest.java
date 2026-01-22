package com.base.system.dto.role;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 角色保存请求参数（新增/编辑）
 */
@Data
public class RoleSaveRequest {

    /**
     * 角色ID（编辑时必填）
     */
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 数据权限范围（1-全部数据 2-本部门及以下 3-本部门 4-仅本人 5-自定义）
     */
    @NotNull(message = "数据权限范围不能为空")
    private Integer dataScope;

    /**
     * 状态（0-禁用 1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
