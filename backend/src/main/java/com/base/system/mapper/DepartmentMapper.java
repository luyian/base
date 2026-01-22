package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper 接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
