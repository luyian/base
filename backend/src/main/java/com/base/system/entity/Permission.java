package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限/菜单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    /**
     * 父级ID（0表示顶级）
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
}
