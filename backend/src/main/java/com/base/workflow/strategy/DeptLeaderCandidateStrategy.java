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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门负责人候选人策略
 */
@Component
public class DeptLeaderCandidateStrategy implements CandidateStrategy {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public String getStrategyKey() {
        return CandidateType.DEPARTMENT_LEADER.getCode();
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

            if (deptIds == null || deptIds.isEmpty()) {
                if (processInstance.getInitiatorDeptId() != null) {
                    deptIds = Arrays.asList(processInstance.getInitiatorDeptId());
                } else {
                    return Collections.emptyList();
                }
            }

            List<Department> depts = departmentMapper.selectList(
                    new LambdaQueryWrapper<Department>()
                            .in(Department::getId, deptIds)
            );

            List<String> leaders = depts.stream()
                    .map(Department::getLeader)
                    .filter(l -> l != null && !l.isEmpty())
                    .collect(Collectors.toList());

            if (leaders.isEmpty()) {
                return Collections.emptyList();
            }

            return userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .in(SysUser::getUsername, leaders)
                            .eq(SysUser::getStatus, 1)
            ).stream()
                    .map(SysUser::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
