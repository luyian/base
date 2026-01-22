package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.RoleDepartment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色部门关联Mapper接口
 *
 * @author base
 * @since 2026-01-13
 */
@Mapper
public interface RoleDepartmentMapper extends BaseMapper<RoleDepartment> {
}
