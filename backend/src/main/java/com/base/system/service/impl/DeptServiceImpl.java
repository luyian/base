package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.DeptTreeNode;
import com.base.system.entity.Dept;
import com.base.system.mapper.DeptMapper;
import com.base.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 *
 * @author base
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<DeptTreeNode> getDeptTree() {
        // 查询所有未删除的部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getDeleted, 0)
                .orderByAsc(Dept::getOrderNum);
        List<Dept> deptList = deptMapper.selectList(wrapper);

        // 转换为树形结构
        return buildDeptTree(deptList, 0L);
    }

    @Override
    public List<Dept> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, parentId)
                .eq(Dept::getDeleted, 0)
                .orderByAsc(Dept::getOrderNum);
        return deptMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDept(Dept dept) {
        // 验证部门编码是否已存在
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getDeptCode, dept.getDeptCode())
                .eq(Dept::getDeleted, 0);
        Long count = deptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "部门编码已存在");
        }

        // 设置默认值
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }
        if (dept.getDeleted() == null) {
            dept.setDeleted(0);
        }

        deptMapper.insert(dept);
        log.info("创建部门成功: {}", dept.getDeptName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Dept dept) {
        // 验证部门是否存在
        Dept existDept = deptMapper.selectById(dept.getId());
        if (existDept == null || existDept.getDeleted() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "部门不存在");
        }

        // 验证部门编码是否已被其他部门使用
        if (dept.getDeptCode() != null && !dept.getDeptCode().equals(existDept.getDeptCode())) {
            LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dept::getDeptCode, dept.getDeptCode())
                    .eq(Dept::getDeleted, 0)
                    .ne(Dept::getId, dept.getId());
            Long count = deptMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "部门编码已存在");
            }
        }

        // 验证不能将部门设置为自己的子部门
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不能将部门设置为自己的子部门");
        }

        deptMapper.updateById(dept);
        log.info("更新部门成功: {}", dept.getDeptName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        // 验证部门是否存在
        Dept dept = deptMapper.selectById(id);
        if (dept == null || dept.getDeleted() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "部门不存在");
        }

        // 验证是否有子部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, id)
                .eq(Dept::getDeleted, 0);
        Long count = deptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该部门下存在子部门，无法删除");
        }

        // 逻辑删除
        dept.setDeleted(1);
        deptMapper.updateById(dept);
        log.info("删除部门成功: {}", dept.getDeptName());
    }

    /**
     * 构建部门树
     */
    private List<DeptTreeNode> buildDeptTree(List<Dept> deptList, Long parentId) {
        List<DeptTreeNode> treeNodes = new ArrayList<>();

        for (Dept dept : deptList) {
            if (dept.getParentId().equals(parentId)) {
                DeptTreeNode node = new DeptTreeNode();
                BeanUtils.copyProperties(dept, node);

                // 递归查找子部门
                List<DeptTreeNode> children = buildDeptTree(deptList, dept.getId());
                node.setChildren(children);

                treeNodes.add(node);
            }
        }

        return treeNodes;
    }
}
