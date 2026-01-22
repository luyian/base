package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper接口
 *
 * @author base
 * @since 2026-01-13
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询部门及所有子部门ID（递归查询）
     *
     * @param deptId 部门ID
     * @return 部门ID列表（包含自身和所有子部门）
     */
    @Select("WITH RECURSIVE dept_tree AS (" +
            "  SELECT id FROM sys_dept WHERE id = #{deptId} AND deleted = 0 " +
            "  UNION ALL " +
            "  SELECT d.id FROM sys_dept d " +
            "  INNER JOIN dept_tree dt ON d.parent_id = dt.id " +
            "  WHERE d.deleted = 0" +
            ") SELECT id FROM dept_tree")
    List<Long> selectDeptAndChildIds(@Param("deptId") Long deptId);
}
