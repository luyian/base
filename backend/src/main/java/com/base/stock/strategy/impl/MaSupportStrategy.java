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
 * 均线支撑策略
 * 价格回踩均线后获得支撑并反弹，表明均线支撑有效
 *
 * @author base
 */
@Slf4j
@Component
public class MaSupportStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "MA_SUPPORT";
    }

    @Override
    public String getStrategyName() {
        return "均线支撑";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 25) {
            return ScoreResult.miss("K线数据不足25条");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int maPeriod = getIntParam(params, "maPeriod", 20);
        double touchRatio = getDoubleParam(params, "touchRatio", 0.02);

        int size = klineData.size();
        StockKline current = klineData.get(size - 1);
        StockKline prev = klineData.get(size - 2);

        // 计算均线
        BigDecimal ma = calculateMA(klineData, maPeriod);

        // 计算价格与均线的距离比例
        double currentDistanceRatio = current.getClosePrice().subtract(ma)
                .divide(ma, 6, RoundingMode.HALF_UP).doubleValue();
        double prevDistanceRatio = prev.getClosePrice().subtract(ma)
                .divide(ma, 6, RoundingMode.HALF_UP).doubleValue();

        // 最低价与均线的距离
        double lowDistanceRatio = current.getLowPrice().subtract(ma)
                .divide(ma, 6, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> detail = new HashMap<>();
        detail.put("ma", ma);
        detail.put("currentClose", current.getClosePrice());
        detail.put("currentLow", current.getLowPrice());
        detail.put("currentDistanceRatio", currentDistanceRatio);
        detail.put("prevDistanceRatio", prevDistanceRatio);
        detail.put("lowDistanceRatio", lowDistanceRatio);

        // 判断均线支撑：
        // 1. 最低价接近或触及均线（在均线上下touchRatio范围内）
        // 2. 收盘价在均线上方
        // 3. 收盘价高于开盘价（收阳线）
        boolean touchMa = Math.abs(lowDistanceRatio) <= touchRatio;
        boolean closeAboveMa = currentDistanceRatio > 0;
        boolean isPositive = current.getClosePrice().compareTo(current.getOpenPrice()) > 0;

        // 或者前一天跌破均线，当天收回均线上方
        boolean recoverMa = prevDistanceRatio < 0 && currentDistanceRatio > 0;

        detail.put("touchMa", touchMa);
        detail.put("closeAboveMa", closeAboveMa);
        detail.put("isPositive", isPositive);
        detail.put("recoverMa", recoverMa);

        if ((touchMa && closeAboveMa && isPositive) || recoverMa) {
            // 动态打分
            double score = context.getRule().getBaseScore();
            if (recoverMa && isPositive) {
                score = context.getRule().getMaxScore();
            } else if (touchMa && closeAboveMa && isPositive) {
                score = (context.getRule().getBaseScore() + context.getRule().getMaxScore()) / 2.0;
            }
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("未出现均线支撑信号");
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
