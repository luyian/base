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
 * KDJ金叉策略
 * K线上穿D线形成金叉，且J值从低位回升
 *
 * @author base
 */
@Slf4j
@Component
public class KdjGoldenCrossStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "KDJ_GOLDEN_CROSS";
    }

    @Override
    public String getStrategyName() {
        return "KDJ金叉";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 15) {
            return ScoreResult.miss("K线数据不足15条，无法计算KDJ");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int period = getIntParam(params, "period", 9);

        // 计算最近两天的KDJ
        double[] currentKdj = calculateKDJ(klineData, period, 0);
        double[] prevKdj = calculateKDJ(klineData, period, 1);

        double currentK = currentKdj[0];
        double currentD = currentKdj[1];
        double currentJ = currentKdj[2];
        double prevK = prevKdj[0];
        double prevD = prevKdj[1];
        double prevJ = prevKdj[2];

        Map<String, Object> detail = new HashMap<>();
        detail.put("currentK", currentK);
        detail.put("currentD", currentD);
        detail.put("currentJ", currentJ);
        detail.put("prevK", prevK);
        detail.put("prevD", prevD);
        detail.put("prevJ", prevJ);

        // 判断金叉：前一天K<=D，当天K>D，且J值在低位（<50）
        boolean isGoldenCross = prevK <= prevD && currentK > currentD;
        boolean isLowPosition = prevJ < 50 || currentJ < 80;

        detail.put("isGoldenCross", isGoldenCross);
        detail.put("isLowPosition", isLowPosition);

        if (isGoldenCross && isLowPosition) {
            // 动态打分：J值越低得分越高
            double score = context.getRule().getBaseScore();
            if (prevJ < 20) {
                score = context.getRule().getMaxScore();
            } else if (prevJ < 30) {
                score = (context.getRule().getBaseScore() + context.getRule().getMaxScore()) / 2.0;
            }
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("未出现KDJ金叉信号");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算KDJ指标
     *
     * @param klineData K线数据
     * @param period    周期
     * @param offset    偏移量
     * @return [K, D, J]
     */
    private double[] calculateKDJ(List<StockKline> klineData, int period, int offset) {
        int size = klineData.size();
        int endIndex = size - 1 - offset;

        if (endIndex < period) {
            return new double[]{50, 50, 50};
        }

        // 计算RSV
        BigDecimal close = klineData.get(endIndex).getClosePrice();
        BigDecimal highest = BigDecimal.ZERO;
        BigDecimal lowest = new BigDecimal("999999");

        for (int i = endIndex - period + 1; i <= endIndex; i++) {
            StockKline kline = klineData.get(i);
            if (kline.getHighPrice().compareTo(highest) > 0) {
                highest = kline.getHighPrice();
            }
            if (kline.getLowPrice().compareTo(lowest) < 0) {
                lowest = kline.getLowPrice();
            }
        }

        double rsv = 50;
        if (highest.compareTo(lowest) != 0) {
            rsv = close.subtract(lowest)
                    .divide(highest.subtract(lowest), 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // 简化计算：使用当日RSV近似K值
        // 实际应用中应使用递归计算
        double k = rsv;
        double d = (k + 50) / 2;
        double j = 3 * k - 2 * d;

        return new double[]{k, d, j};
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
