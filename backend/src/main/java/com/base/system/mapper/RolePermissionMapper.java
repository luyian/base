package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 批量插入角色权限关联
     *
     * @param list 角色权限关联列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<RolePermission> list);
}
