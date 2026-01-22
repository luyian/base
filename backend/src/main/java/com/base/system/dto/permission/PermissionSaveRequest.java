package com.base.system.dto.permission;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限保存请求参数
 */
@Data
public class PermissionSaveRequest {

    /**
     * 权限ID（编辑时必填）
     */
    private Long id;

    /**
     * 父级ID（0表示顶级）
     */
    @NotNull(message = "父级ID不能为空")
    private Long parentId;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    /**
     * 权限类型（1-菜单 2-按钮）
     */
    @NotNull(message = "权限类型不能为空")
    private Integer type;

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
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 是否可见（0-隐藏 1-显示）
     */
    @NotNull(message = "是否可见不能为空")
    private Integer visible;

    /**
     * 备注
     */
    private String remark;
}
