package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联 Mapper 接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
