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
 * 突破前高策略
 * 判断是否突破近期高点
 *
 * @author base
 */
@Slf4j
@Component
public class BreakHighStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "BREAK_HIGH";
    }

    @Override
    public String getStrategyName() {
        return "突破前高";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        Map<String, Object> params = context.getRuleParams();

        int period = getIntParam(params, "period", 20);
        double breakRatio = getDoubleParam(params, "breakRatio", 1.01);

        if (klineData.size() < period + 1) {
            return ScoreResult.miss("K线数据不足" + (period + 1) + "条");
        }

        // 获取最新收盘价
        BigDecimal latestClose = klineData.get(klineData.size() - 1).getClosePrice();

        // 计算前N天的最高价
        BigDecimal periodHigh = BigDecimal.ZERO;
        for (int i = klineData.size() - period - 1; i < klineData.size() - 1; i++) {
            BigDecimal highPrice = klineData.get(i).getHighPrice();
            if (highPrice.compareTo(periodHigh) > 0) {
                periodHigh = highPrice;
            }
        }

        // 计算突破比例
        double actualRatio = latestClose.divide(periodHigh, 4, BigDecimal.ROUND_HALF_UP).doubleValue();

        Map<String, Object> detail = new HashMap<>();
        detail.put("latestClose", latestClose);
        detail.put("periodHigh", periodHigh);
        detail.put("actualRatio", String.format("%.4f", actualRatio));
        detail.put("breakRatio", breakRatio);

        if (actualRatio >= breakRatio) {
            // 动态打分：突破幅度越大，分数越高
            ScoreRule rule = context.getRule();
            double score = calculateDynamicScore(actualRatio, breakRatio, rule.getBaseScore(), rule.getMaxScore());
            detail.put("score", score);
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("未突破前高");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算动态分数
     *
     * @param actualRatio 实际突破比例
     * @param breakRatio  突破阈值
     * @param baseScore   基础分数
     * @param maxScore    最高分数
     * @return 动态分数
     */
    private double calculateDynamicScore(double actualRatio, double breakRatio, int baseScore, int maxScore) {
        // 线性映射：breakRatio -> baseScore, breakRatio*1.1 -> maxScore
        double maxRatio = breakRatio * 1.1;
        if (actualRatio >= maxRatio) {
            return maxScore;
        }
        double ratio = (actualRatio - breakRatio) / (maxRatio - breakRatio);
        return baseScore + ratio * (maxScore - baseScore);
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private double getDoubleParam(Map<String, Object> params, String key, double defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
