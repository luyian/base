package com.base.stock.recommend.engine;

import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.strategy.ScoreContext;
import com.base.stock.recommend.strategy.ScoreResult;
import com.base.stock.recommend.strategy.ScoreStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 打分引擎
 * 负责协调所有打分策略的执行
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreEngine {

    private final ObjectMapper objectMapper;

    /**
     * 所有打分策略（Spring自动注入）
     */
    private final List<ScoreStrategy> strategies;

    /**
     * 执行打分
     *
     * @param stockInfo 股票信息
     * @param klineData K线数据
     * @param rules     启用的规则列表
     * @param scoreDate 打分日期
     * @return 打分结果Map<规则编码, 打分结果>
     */
    public Map<String, ScoreResult> executeScore(
            StockInfo stockInfo,
            List<StockKline> klineData,
            List<ScoreRule> rules,
            LocalDate scoreDate) {

        Map<String, ScoreResult> resultMap = new HashMap<>();

        // 构建策略映射
        Map<String, ScoreStrategy> strategyMap = strategies.stream()
                .collect(Collectors.toMap(ScoreStrategy::getStrategyCode, s -> s));

        log.debug("开始对股票 {} 执行打分，规则数量：{}", stockInfo.getStockCode(), rules.size());

        // 按排序号执行规则
        for (ScoreRule rule : rules) {
            ScoreStrategy strategy = strategyMap.get(rule.getRuleCode());
            if (strategy == null) {
                log.warn("规则 {} 未找到对应的策略实现", rule.getRuleCode());
                resultMap.put(rule.getRuleCode(), ScoreResult.miss("策略实现不存在"));
                continue;
            }

            try {
                // 解析规则参数
                Map<String, Object> ruleParams = parseRuleParams(rule.getConfigJson());

                // 构建打分上下文
                ScoreContext context = ScoreContext.builder()
                        .stockInfo(stockInfo)
                        .stockCode(stockInfo.getStockCode())
                        .scoreDate(scoreDate)
                        .klineData(klineData)
                        .rule(rule)
                        .ruleParams(ruleParams)
                        .extData(new HashMap<>())
                        .build();

                // 执行打分
                ScoreResult result = strategy.execute(context);
                resultMap.put(rule.getRuleCode(), result);

                log.debug("规则 {} 执行完成，命中：{}，得分：{}",
                        rule.getRuleCode(), result.isHit(), result.getScore());

            } catch (Exception e) {
                log.error("规则 {} 执行失败: {}", rule.getRuleCode(), e.getMessage(), e);
                resultMap.put(rule.getRuleCode(), ScoreResult.miss("执行异常: " + e.getMessage()));
            }
        }

        return resultMap;
    }

    /**
     * 解析规则参数
     *
     * @param configJson 配置JSON字符串
     * @return 参数Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseRuleParams(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(configJson, Map.class);
        } catch (Exception e) {
            log.error("解析规则参数失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
