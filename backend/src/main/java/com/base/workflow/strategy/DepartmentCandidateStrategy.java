package com.base.workflow.strategy;

import cn.hutool.json.JSONUtil;
import com.base.system.entity.Department;
import com.base.system.entity.SysUser;
import com.base.system.mapper.DepartmentMapper;
import com.base.system.mapper.SysUserMapper;
import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.enums.CandidateType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门候选人策略
 */
@Component
public class DepartmentCandidateStrategy implements CandidateStrategy {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public String getStrategyKey() {
        return CandidateType.DEPARTMENT.getCode();
    }

    @Override
    public List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance) {
        String config = node.getCandidateConfig();
        if (config == null || config.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Map<String, Object> configMap = JSONUtil.toBean(config, Map.class);
            List<Long> deptIds = (List<Long>) configMap.get("deptIds");
            Boolean includeChildren = (Boolean) configMap.getOrDefault("includeChildren", false);

            if (deptIds == null || deptIds.isEmpty()) {
                return Collections.emptyList();
            }

            if (Boolean.TRUE.equals(includeChildren)) {
                deptIds = expandDeptIds(deptIds);
            }

            return userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .in(SysUser::getDeptId, deptIds)
                            .eq(SysUser::getStatus, 1)
            ).stream()
                    .map(SysUser::getId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Long> expandDeptIds(List<Long> deptIds) {
        List<Department> allDepts = departmentMapper.selectList(null);
        List<Long> result = new java.util.ArrayList<>(deptIds);

        for (Long deptId : deptIds) {
            Department dept = allDepts.stream()
                    .filter(d -> d.getId().equals(deptId))
                    .findFirst()
                    .orElse(null);
            if (dept != null) {
                findChildren(deptId, allDepts, result);
            }
        }
        return result;
    }

    private void findChildren(Long parentId, List<Department> allDepts, List<Long> result) {
        for (Department dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                result.add(dept.getId());
                findChildren(dept.getId(), allDepts, result);
            }
        }
    }
}
