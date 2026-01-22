package com.base.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.base.system.dto.DeptTreeNode;
import com.base.system.entity.Dept;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author base
 * @since 2026-01-13
 */
public interface DeptService extends IService<Dept> {

    /**
     * 获取部门树
     */
    List<DeptTreeNode> getDeptTree();

    /**
     * 根据父部门ID获取子部门列表
     */
    List<Dept> getChildrenByParentId(Long parentId);

    /**
     * 创建部门
     */
    void createDept(Dept dept);

    /**
     * 更新部门
     */
    void updateDept(Dept dept);

    /**
     * 删除部门
     */
    void deleteDept(Long id);
}
