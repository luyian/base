package com.base.workflow.strategy;

import cn.hutool.json.JSONUtil;
import com.base.system.entity.SysUser;
import com.base.system.entity.SysUserRole;
import com.base.system.mapper.SysUserRoleMapper;
import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.enums.CandidateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色候选人策略
 */
@Component
public class RoleCandidateStrategy implements CandidateStrategy {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public String getStrategyKey() {
        return CandidateType.ROLE.getCode();
    }

    @Override
    public List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance) {
        String config = node.getCandidateConfig();
        if (config == null || config.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Map<String, Object> configMap = JSONUtil.toBean(config, Map.class);
            List<Long> roleIds = (List<Long>) configMap.get("roleIds");
            if (roleIds == null || roleIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<SysUserRole> userRoles = userRoleMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole>()
                            .in(SysUserRole::getRoleId, roleIds)
            );

            return userRoles.stream()
                    .map(SysUserRole::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
