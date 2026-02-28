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
 * 布林带突破策略
 * 价格突破布林带上轨，表明强势上涨
 *
 * @author base
 */
@Slf4j
@Component
public class BollingerBreakStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "BOLLINGER_BREAK";
    }

    @Override
    public String getStrategyName() {
        return "布林带突破";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 25) {
            return ScoreResult.miss("K线数据不足25条，无法计算布林带");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int period = getIntParam(params, "period", 20);
        double multiplier = getDoubleParam(params, "multiplier", 2.0);

        // 计算布林带
        int size = klineData.size();
        BigDecimal ma = calculateMA(klineData, period);
        BigDecimal std = calculateStd(klineData, period, ma);

        BigDecimal upper = ma.add(std.multiply(BigDecimal.valueOf(multiplier)));
        BigDecimal lower = ma.subtract(std.multiply(BigDecimal.valueOf(multiplier)));

        // 获取当前价格和前一天价格
        BigDecimal currentClose = klineData.get(size - 1).getClosePrice();
        BigDecimal prevClose = klineData.get(size - 2).getClosePrice();

        Map<String, Object> detail = new HashMap<>();
        detail.put("ma", ma);
        detail.put("upper", upper);
        detail.put("lower", lower);
        detail.put("currentClose", currentClose);
        detail.put("prevClose", prevClose);

        // 判断突破上轨：当前价格突破上轨，且前一天在上轨以下
        boolean breakUpper = currentClose.compareTo(upper) > 0 && prevClose.compareTo(upper) <= 0;

        // 或者价格在上轨附近且持续上涨
        boolean nearUpper = currentClose.compareTo(upper.multiply(BigDecimal.valueOf(0.98))) > 0
                && currentClose.compareTo(prevClose) > 0;

        detail.put("breakUpper", breakUpper);
        detail.put("nearUpper", nearUpper);

        if (breakUpper) {
            return ScoreResult.hit(context.getRule().getMaxScore(), detail);
        } else if (nearUpper) {
            return ScoreResult.hit(context.getRule().getBaseScore(), detail);
        } else {
            return ScoreResult.miss("未出现布林带突破信号");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算移动平均线
     */
    private BigDecimal calculateMA(List<StockKline> klineData, int period) {
        int size = klineData.size();
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = size - period; i < size; i++) {
            sum = sum.add(klineData.get(i).getClosePrice());
        }
        return sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算标准差
     */
    private BigDecimal calculateStd(List<StockKline> klineData, int period, BigDecimal ma) {
        int size = klineData.size();
        BigDecimal sumSquare = BigDecimal.ZERO;
        for (int i = size - period; i < size; i++) {
            BigDecimal diff = klineData.get(i).getClosePrice().subtract(ma);
            sumSquare = sumSquare.add(diff.multiply(diff));
        }
        BigDecimal variance = sumSquare.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
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
