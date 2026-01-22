package com.base.system.dto.permission;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限响应结果
 */
@Data
public class PermissionResponse {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

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
     * 权限类型名称
     */
    private String typeName;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否可见（0-隐藏 1-显示）
     */
    private Integer visible;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 子权限列表
     */
    private List<PermissionResponse> children;
}
