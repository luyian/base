package com.base.workflow.listener;

import cn.hutool.json.JSONUtil;
import com.base.system.entity.SysUser;
import com.base.system.entity.SysUserRole;
import com.base.system.mapper.SysUserMapper;
import com.base.system.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 候选人动态分配 TaskListener
 * <p>
 * 在 BPMN UserTask 上通过 flowable:taskListener 引用此 Bean。
 * 读取流程变量 candidateType_{activityId} 和 candidateConfig_{activityId} 动态分配候选人。
 * </p>
 */
@Component("candidateAssignmentListener")
public class CandidateAssignmentTaskListener implements TaskListener {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public void notify(DelegateTask delegateTask) {
        String activityId = delegateTask.getTaskDefinitionKey();
        String candidateType = (String) delegateTask.getVariable("candidateType_" + activityId);
        String candidateConfig = (String) delegateTask.getVariable("candidateConfig_" + activityId);

        if (candidateType == null || candidateType.isEmpty()) {
            return;
        }

        List<String> candidates = resolveCandidates(candidateType, candidateConfig, delegateTask);
        if (candidates.isEmpty()) {
            return;
        }

        if (candidates.size() == 1) {
            delegateTask.setAssignee(candidates.get(0));
        } else {
            for (String candidate : candidates) {
                delegateTask.addCandidateUser(candidate);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> resolveCandidates(String candidateType, String candidateConfig, DelegateTask task) {
        if (candidateConfig == null || candidateConfig.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> config;
        try {
            config = JSONUtil.toBean(candidateConfig, Map.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }

        List<Long> userIds;
        switch (candidateType) {
            case "USER":
                userIds = getListAsLong(config, "userIds");
                break;
            case "ROLE":
                List<Long> roleIds = getListAsLong(config, "roleIds");
                userIds = findUsersByRoleIds(roleIds);
                break;
            case "DEPARTMENT":
                List<Long> deptIds = getListAsLong(config, "deptIds");
                userIds = findUsersByDeptIds(deptIds);
                break;
            case "DEPARTMENT_LEADER":
                List<Long> leaderDeptIds = getListAsLong(config, "deptIds");
                userIds = findDeptLeaders(leaderDeptIds);
                break;
            case "INITIATOR_DEPT":
                String initiator = (String) task.getVariable("initiator");
                userIds = findInitiatorDeptUsers(initiator);
                break;
            default:
                return Collections.emptyList();
        }

        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 将用户 ID 转换为用户名
        List<SysUser> users = userMapper.selectBatchIds(userIds);
        return users.stream()
                .map(SysUser::getUsername)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Long> getListAsLong(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value instanceof List) {
            return ((List<?>) value).stream()
                    .map(v -> Long.valueOf(v.toString()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Long> findUsersByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds)
        );
        return userRoles.stream()
                .map(SysUserRole::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Long> findUsersByDeptIds(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getDeptId, deptIds)
                        .eq(SysUser::getStatus, 1)
        );
        return users.stream().map(SysUser::getId).collect(Collectors.toList());
    }

    private List<Long> findDeptLeaders(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询部门负责人（需要根据实际部门表结构调整）
        return findUsersByDeptIds(deptIds);
    }

    private List<Long> findInitiatorDeptUsers(String initiator) {
        if (initiator == null) {
            return Collections.emptyList();
        }
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, initiator)
        );
        if (user == null || user.getDeptId() == null) {
            return Collections.emptyList();
        }
        return findUsersByDeptIds(Collections.singletonList(user.getDeptId()));
    }
}
