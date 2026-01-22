package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色部门关联实体类（用于自定义数据权限）
 *
 * @author base
 * @since 2026-01-13
 */
@Data
@TableName("sys_role_department")
public class RoleDepartment {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 部门ID
     */
    private Long departmentId;
}
