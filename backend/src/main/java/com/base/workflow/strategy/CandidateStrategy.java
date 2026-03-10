package com.base.workflow.strategy;

import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessInstance;

import java.util.List;

/**
 * 候选人策略接口
 */
public interface CandidateStrategy {

    /**
     * 获取策略标识
     */
    String getStrategyKey();

    /**
     * 解析候选人
     *
     * @param node           节点配置
     * @param processInstance 流程实例
     * @return 候选人用户ID列表
     */
    List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance);
}
