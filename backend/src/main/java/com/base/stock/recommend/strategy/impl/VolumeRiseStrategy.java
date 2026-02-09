package com.base.stock.recommend.strategy.impl;

import com.base.stock.entity.StockKline;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.strategy.ScoreContext;
import com.base.stock.recommend.strategy.ScoreResult;
import com.base.stock.recommend.strategy.ScoreStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 放量上涨策略
 * 成交量显著放大且价格上涨，表明资金积极入场
 *
 * @author base
 */
@Slf4j
@Component
public class VolumeRiseStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "VOLUME_RISE";
    }

    @Override
    public String getStrategyName() {
        return "放量上涨";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 10) {
            return ScoreResult.miss("K线数据不足10条");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int period = getIntParam(params, "period", 5);
        double volumeRatio = getDoubleParam(params, "volumeRatio", 1.5);
        double priceRatio = getDoubleParam(params, "priceRatio", 0.02);

        int size = klineData.size();
        StockKline current = klineData.get(size - 1);
        StockKline prev = klineData.get(size - 2);

        // 计算平均成交量
        long avgVolume = calculateAvgVolume(klineData, period);

        // 当前成交量与平均成交量的比值
        double currentVolumeRatio = (double) current.getVolume() / avgVolume;

        // 计算涨幅
        double priceChange = current.getClosePrice().subtract(prev.getClosePrice())
                .divide(prev.getClosePrice(), 6, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> detail = new HashMap<>();
        detail.put("currentVolume", current.getVolume());
        detail.put("avgVolume", avgVolume);
        detail.put("volumeRatio", currentVolumeRatio);
        detail.put("priceChange", priceChange);
        detail.put("thresholdVolumeRatio", volumeRatio);
        detail.put("thresholdPriceRatio", priceRatio);

        // 判断放量上涨：成交量放大且价格上涨
        boolean isVolumeUp = currentVolumeRatio >= volumeRatio;
        boolean isPriceUp = priceChange >= priceRatio;

        detail.put("isVolumeUp", isVolumeUp);
        detail.put("isPriceUp", isPriceUp);

        if (isVolumeUp && isPriceUp) {
            // 动态打分：放量越大、涨幅越高得分越高
            double score = context.getRule().getBaseScore();
            if (currentVolumeRatio >= 2.0 && priceChange >= 0.05) {
                score = context.getRule().getMaxScore();
            } else if (currentVolumeRatio >= 1.8 || priceChange >= 0.03) {
                score = (context.getRule().getBaseScore() + context.getRule().getMaxScore()) / 2.0;
            }
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("未出现放量上涨信号");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算平均成交量
     */
    private long calculateAvgVolume(List<StockKline> klineData, int period) {
        int size = klineData.size();
        long sum = 0;
        // 不包含当天
        for (int i = size - period - 1; i < size - 1; i++) {
            sum += klineData.get(i).getVolume();
        }
        return sum / period;
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
