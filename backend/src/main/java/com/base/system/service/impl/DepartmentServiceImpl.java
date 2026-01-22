package com.base.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.department.DepartmentQueryRequest;
import com.base.system.dto.department.DepartmentResponse;
import com.base.system.dto.department.DepartmentSaveRequest;
import com.base.system.entity.Department;
import com.base.system.entity.User;
import com.base.system.mapper.DepartmentMapper;
import com.base.system.mapper.UserMapper;
import com.base.system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final UserMapper userMapper;

    @Override
    public List<DepartmentResponse> treeDepartments(DepartmentQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(request.getDeptName()), Department::getDeptName, request.getDeptName())
                .like(StrUtil.isNotBlank(request.getDeptCode()), Department::getDeptCode, request.getDeptCode())
                .eq(request.getStatus() != null, Department::getStatus, request.getStatus())
                .orderByAsc(Department::getSort)
                .orderByDesc(Department::getCreateTime);

        // 查询所有部门
        List<Department> departmentList = list(wrapper);

        // 转换为树形结构
        return buildDepartmentTree(departmentList, 0L);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        return BeanUtil.copyProperties(department, DepartmentResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDepartment(DepartmentSaveRequest request) {
        // 检查部门编码是否已存在
        long count = count(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeptCode, request.getDeptCode()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "部门编码已存在");
        }

        // 保存部门
        Department department = BeanUtil.copyProperties(request, Department.class);
        save(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(DepartmentSaveRequest request) {
        // 检查部门是否存在
        Department department = getById(request.getId());
        if (department == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 检查部门编码是否已存在（排除自己）
        long count = count(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeptCode, request.getDeptCode())
                .ne(Department::getId, request.getId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "部门编码已存在");
        }

        // 检查是否将部门设置为自己的子部门
        if (request.getId().equals(request.getParentId())) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "不能将部门设置为自己的上级部门");
        }

        // 检查是否将部门设置为自己的后代部门
        if (isDescendant(request.getId(), request.getParentId())) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "不能将部门设置为自己的后代部门");
        }

        // 更新部门
        BeanUtil.copyProperties(request, department, "id");
        updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        // 检查部门是否存在
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 检查是否有子部门
        long count = count(new LambdaQueryWrapper<Department>()
                .eq(Department::getParentId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "该部门下存在子部门，无法删除");
        }

        // 检查是否有用户
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getDeptId, id));
        if (userCount > 0) {
            throw new BusinessException(ResultCode.DATA_IN_USE.getCode(), "该部门下存在用户，无法删除");
        }

        // 删除部门
        removeById(id);
    }

    @Override
    public List<DepartmentResponse> getAllDepartmentTree() {
        // 查询所有正常状态的部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getStatus, 1)
                .orderByAsc(Department::getSort)
                .orderByDesc(Department::getCreateTime);

        List<Department> departmentList = list(wrapper);

        // 转换为树形结构
        return buildDepartmentTree(departmentList, 0L);
    }

    /**
     * 构建部门树
     *
     * @param departmentList 部门列表
     * @param parentId       父级ID
     * @return 部门树列表
     */
    private List<DepartmentResponse> buildDepartmentTree(List<Department> departmentList, Long parentId) {
        if (CollUtil.isEmpty(departmentList)) {
            return new ArrayList<>();
        }

        // 按父级ID分组
        Map<Long, List<Department>> departmentMap = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getParentId));

        // 递归构建树形结构
        return buildTree(departmentMap, parentId);
    }

    /**
     * 递归构建树形结构
     *
     * @param departmentMap 部门分组Map
     * @param parentId      父级ID
     * @return 部门树列表
     */
    private List<DepartmentResponse> buildTree(Map<Long, List<Department>> departmentMap, Long parentId) {
        List<Department> children = departmentMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        return children.stream().map(department -> {
            DepartmentResponse response = BeanUtil.copyProperties(department, DepartmentResponse.class);

            // 递归查询子部门
            List<DepartmentResponse> childrenList = buildTree(departmentMap, department.getId());
            if (CollUtil.isNotEmpty(childrenList)) {
                response.setChildren(childrenList);
            }

            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 判断目标部门是否是当前部门的后代
     *
     * @param currentId 当前部门ID
     * @param targetId  目标部门ID
     * @return 是否是后代
     */
    private boolean isDescendant(Long currentId, Long targetId) {
        if (targetId == null || targetId == 0) {
            return false;
        }

        Department target = getById(targetId);
        if (target == null) {
            return false;
        }

        // 如果目标部门的父级是当前部门，则是后代
        if (currentId.equals(target.getParentId())) {
            return true;
        }

        // 递归检查目标部门的父级
        return isDescendant(currentId, target.getParentId());
    }
}
