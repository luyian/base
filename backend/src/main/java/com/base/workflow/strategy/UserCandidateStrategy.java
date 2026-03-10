package com.base.workflow.strategy;

import cn.hutool.json.JSONUtil;
import com.base.system.entity.SysUser;
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
 * 用户候选人策略
 */
@Component
public class UserCandidateStrategy implements CandidateStrategy {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public String getStrategyKey() {
        return CandidateType.USER.getCode();
    }

    @Override
    public List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance) {
        String config = node.getCandidateConfig();
        if (config == null || config.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Map<String, Object> configMap = JSONUtil.toBean(config, Map.class);
            List<Long> userIds = (List<Long>) configMap.get("userIds");
            if (userIds == null || userIds.isEmpty()) {
                return Collections.emptyList();
            }
            return userIds;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
