package com.base.system.dto.role;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryRequest extends BasePageRequest {

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
}
