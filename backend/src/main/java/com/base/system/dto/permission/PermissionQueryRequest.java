package com.base.system.dto.permission;

import lombok.Data;

/**
 * 权限查询请求参数
 */
@Data
public class PermissionQueryRequest {

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型（1-菜单 2-按钮）
     */
    private Integer type;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
