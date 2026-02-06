package com.base.stock.recommend.strategy.impl;

import com.base.stock.entity.StockKline;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.strategy.ScoreContext;
import com.base.stock.recommend.strategy.ScoreResult;
import com.base.stock.recommend.strategy.ScoreStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成交量突破策略
 * 判断最新成交量是否突破近期平均水平
 *
 * @author base
 */
@Slf4j
@Component
public class VolumeBreakStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "VOLUME_BREAK";
    }

    @Override
    public String getStrategyName() {
        return "成交量突破";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        Map<String, Object> params = context.getRuleParams();

        int period = getIntParam(params, "period", 20);
        double threshold = getDoubleParam(params, "threshold", 1.5);

        if (klineData.size() < period + 1) {
            return ScoreResult.miss("K线数据不足" + (period + 1) + "条");
        }

        // 最新成交量
        Long latestVolume = klineData.get(klineData.size() - 1).getVolume();
        if (latestVolume == null || latestVolume == 0) {
            return ScoreResult.miss("成交量数据缺失");
        }

        // 计算前N天平均成交量
        long sumVolume = 0;
        int count = 0;
        for (int i = klineData.size() - period - 1; i < klineData.size() - 1; i++) {
            Long volume = klineData.get(i).getVolume();
            if (volume != null && volume > 0) {
                sumVolume += volume;
                count++;
            }
        }

        if (count == 0) {
            return ScoreResult.miss("历史成交量数据不足");
        }

        double avgVolume = (double) sumVolume / count;

        // 计算突破倍数
        double breakRatio = latestVolume / avgVolume;

        Map<String, Object> detail = new HashMap<>();
        detail.put("latestVolume", latestVolume);
        detail.put("avgVolume", avgVolume);
        detail.put("breakRatio", String.format("%.2f", breakRatio));
        detail.put("threshold", threshold);

        if (breakRatio >= threshold) {
            // 动态打分：突破倍数越高，分数越高
            // 分数区间：baseScore ~ maxScore
            ScoreRule rule = context.getRule();
            double score = calculateDynamicScore(breakRatio, threshold, rule.getBaseScore(), rule.getMaxScore());
            detail.put("score", score);
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("成交量未达到突破阈值");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算动态分数
     *
     * @param breakRatio 突破倍数
     * @param threshold  阈值
     * @param baseScore  基础分数
     * @param maxScore   最高分数
     * @return 动态分数
     */
    private double calculateDynamicScore(double breakRatio, double threshold, int baseScore, int maxScore) {
        // 线性映射：threshold -> baseScore, threshold*2 -> maxScore
        double ratio = (breakRatio - threshold) / threshold;
        double score = baseScore + ratio * (maxScore - baseScore);
        return Math.min(score, maxScore);
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
