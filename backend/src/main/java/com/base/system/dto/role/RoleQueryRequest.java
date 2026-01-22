package com.base.system.dto.role;

import lombok.Data;

/**
 * 角色查询请求参数
 */
@Data
public class RoleQueryRequest {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
