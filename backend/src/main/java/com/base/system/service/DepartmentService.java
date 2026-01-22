package com.base.system.service;

import com.base.system.dto.department.DepartmentQueryRequest;
import com.base.system.dto.department.DepartmentResponse;
import com.base.system.dto.department.DepartmentSaveRequest;
import com.base.system.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 查询部门树
     *
     * @param request 查询条件
     * @return 部门树列表
     */
    List<DepartmentResponse> treeDepartments(DepartmentQueryRequest request);

    /**
     * 根据ID获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    DepartmentResponse getDepartmentById(Long id);

    /**
     * 新增部门
     *
     * @param request 部门信息
     */
    void addDepartment(DepartmentSaveRequest request);

    /**
     * 编辑部门
     *
     * @param request 部门信息
     */
    void updateDepartment(DepartmentSaveRequest request);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 获取所有部门树（用于选择上级部门）
     *
     * @return 部门树列表
     */
    List<DepartmentResponse> getAllDepartmentTree();
}
