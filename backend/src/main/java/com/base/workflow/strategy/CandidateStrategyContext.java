package com.base.workflow.strategy;

import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 候选人策略上下文
 */
@Component
public class CandidateStrategyContext {

    private final Map<String, CandidateStrategy> strategyMap;

    @Autowired
    public CandidateStrategyContext(List<CandidateStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(CandidateStrategy::getStrategyKey, Function.identity()));
    }

    /**
     * 解析候选人
     */
    public List<Long> resolveCandidates(ProcessNode node, ProcessInstance processInstance) {
        CandidateStrategy strategy = strategyMap.get(node.getCandidateType());
        if (strategy == null) {
            throw new RuntimeException("未找到候选人策略: " + node.getCandidateType());
        }
        return strategy.resolveCandidates(node, processInstance);
    }

    /**
     * 获取所有策略
     */
    public Map<String, CandidateStrategy> getAllStrategies() {
        return strategyMap;
    }
}
