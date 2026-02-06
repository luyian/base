package com.base.stock.recommend.strategy.impl;

import com.base.stock.entity.StockKline;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.strategy.ScoreContext;
import com.base.stock.recommend.strategy.ScoreResult;
import com.base.stock.recommend.strategy.ScoreStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连续上涨策略
 * 判断连续N天收盘价上涨
 *
 * @author base
 */
@Slf4j
@Component
public class ContinuousRiseStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "CONTINUOUS_RISE";
    }

    @Override
    public String getStrategyName() {
        return "连续上涨";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        Map<String, Object> params = context.getRuleParams();

        int minDays = getIntParam(params, "minDays", 3);
        int maxDays = getIntParam(params, "maxDays", 5);

        if (klineData.size() < minDays + 1) {
            return ScoreResult.miss("K线数据不足" + (minDays + 1) + "条");
        }

        // 从最新日期向前遍历，统计连续上涨天数
        int continuousDays = 0;
        for (int i = klineData.size() - 1; i > 0; i--) {
            BigDecimal currentClose = klineData.get(i).getClosePrice();
            BigDecimal prevClose = klineData.get(i - 1).getClosePrice();

            if (currentClose.compareTo(prevClose) > 0) {
                continuousDays++;
            } else {
                break;
            }
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("continuousDays", continuousDays);
        detail.put("minDays", minDays);
        detail.put("maxDays", maxDays);

        if (continuousDays >= minDays) {
            // 动态打分：连续天数越多，分数越高
            ScoreRule rule = context.getRule();
            double score = calculateDynamicScore(continuousDays, minDays, maxDays, rule.getBaseScore(), rule.getMaxScore());
            detail.put("score", score);
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("连续上涨天数不足" + minDays + "天");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算动态分数
     *
     * @param continuousDays 连续上涨天数
     * @param minDays        最小天数
     * @param maxDays        最大天数
     * @param baseScore      基础分数
     * @param maxScore       最高分数
     * @return 动态分数
     */
    private double calculateDynamicScore(int continuousDays, int minDays, int maxDays, int baseScore, int maxScore) {
        // 线性映射：minDays -> baseScore, maxDays -> maxScore
        if (continuousDays >= maxDays) {
            return maxScore;
        }
        double ratio = (double) (continuousDays - minDays) / (maxDays - minDays);
        return baseScore + ratio * (maxScore - baseScore);
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
