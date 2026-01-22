package com.base.system.dto.role;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色响应结果
 */
@Data
public class RoleResponse {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 数据权限范围（1-全部数据 2-本部门及以下 3-本部门 4-仅本人 5-自定义）
     */
    private Integer dataScope;

    /**
     * 数据权限范围名称
     */
    private String dataScopeName;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
