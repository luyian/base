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
 * 发起人部门候选人策略
 */
@Component
public class InitiatorDeptCandidateStrategy implements CandidateStrategy {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public String getStrategyKey() {
        return CandidateType.INITIATOR_DEPT.getCode();
    }

    @Override
    public List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance) {
        if (processInstance.getInitiatorDeptId() == null) {
            return Collections.emptyList();
        }

        String config = node.getCandidateConfig();
        Boolean includeChildren = false;
        Integer levels = null;

        if (config != null && !config.isEmpty()) {
            try {
                Map<String, Object> configMap = JSONUtil.toBean(config, Map.class);
                includeChildren = (Boolean) configMap.getOrDefault("includeChildren", false);
                levels = (Integer) configMap.get("levels");
            } catch (Exception e) {
                // ignore
            }
        }

        List<Long> deptIds = new java.util.ArrayList<>();
        deptIds.add(processInstance.getInitiatorDeptId());

        if (Boolean.TRUE.equals(includeChildren)) {
            deptIds = expandDeptIds(deptIds, levels);
        }

        return userMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getDeptId, deptIds)
                        .eq(SysUser::getStatus, 1)
        ).stream()
                .map(SysUser::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Long> expandDeptIds(List<Long> deptIds, Integer levels) {
        List<Department> allDepts = departmentMapper.selectList(null);
        List<Long> result = new java.util.ArrayList<>(deptIds);

        for (Long deptId : deptIds) {
            expandChildren(deptId, allDepts, result, levels, 0);
        }
        return result;
    }

    private void expandChildren(Long parentId, List<Department> allDepts, List<Long> result, Integer maxLevels, int currentLevel) {
        if (maxLevels != null && currentLevel >= maxLevels) {
            return;
        }
        for (Department dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                result.add(dept.getId());
                expandChildren(dept.getId(), allDepts, result, maxLevels, currentLevel + 1);
            }
        }
    }
}
