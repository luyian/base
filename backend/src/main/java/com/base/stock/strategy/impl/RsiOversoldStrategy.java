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
 * RSI超卖反弹策略
 * RSI指标低于超卖线后回升，表明可能出现反弹
 *
 * @author base
 */
@Slf4j
@Component
public class RsiOversoldStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "RSI_OVERSOLD";
    }

    @Override
    public String getStrategyName() {
        return "RSI超卖反弹";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 20) {
            return ScoreResult.miss("K线数据不足20条，无法计算RSI");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int period = getIntParam(params, "period", 14);
        int oversoldLine = getIntParam(params, "oversoldLine", 30);
        int recoveryLine = getIntParam(params, "recoveryLine", 40);

        // 计算最近两天的RSI
        BigDecimal currentRsi = calculateRSI(klineData, period, 0);
        BigDecimal prevRsi = calculateRSI(klineData, period, 1);

        Map<String, Object> detail = new HashMap<>();
        detail.put("currentRsi", currentRsi);
        detail.put("prevRsi", prevRsi);
        detail.put("oversoldLine", oversoldLine);
        detail.put("recoveryLine", recoveryLine);

        // 判断是否从超卖区反弹：前一天RSI低于超卖线，当天RSI回升
        boolean isOversoldRecovery = prevRsi.compareTo(BigDecimal.valueOf(oversoldLine)) <= 0
                && currentRsi.compareTo(prevRsi) > 0;

        // 或者当前RSI在超卖区但开始回升
        boolean isRecovering = currentRsi.compareTo(BigDecimal.valueOf(recoveryLine)) < 0
                && currentRsi.compareTo(prevRsi) > 0
                && prevRsi.compareTo(BigDecimal.valueOf(oversoldLine)) <= 0;

        detail.put("isOversoldRecovery", isOversoldRecovery);
        detail.put("isRecovering", isRecovering);

        if (isOversoldRecovery || isRecovering) {
            // 动态打分：RSI越低反弹得分越高
            double score = context.getRule().getBaseScore();
            if (prevRsi.compareTo(BigDecimal.valueOf(20)) < 0) {
                score = context.getRule().getMaxScore();
            } else if (prevRsi.compareTo(BigDecimal.valueOf(25)) < 0) {
                score = (context.getRule().getBaseScore() + context.getRule().getMaxScore()) / 2.0;
            }
            return ScoreResult.hit(score, detail);
        } else {
            return ScoreResult.miss("未出现RSI超卖反弹信号");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算RSI指标
     *
     * @param klineData K线数据
     * @param period    周期
     * @param offset    偏移量（0表示最新，1表示前一天）
     * @return RSI值
     */
    private BigDecimal calculateRSI(List<StockKline> klineData, int period, int offset) {
        int size = klineData.size();
        int endIndex = size - 1 - offset;

        if (endIndex < period) {
            return BigDecimal.valueOf(50);
        }

        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;

        // 计算涨跌幅
        for (int i = endIndex - period + 1; i <= endIndex; i++) {
            BigDecimal change = klineData.get(i).getClosePrice()
                    .subtract(klineData.get(i - 1).getClosePrice());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }

        avgGain = avgGain.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, 6, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 4, RoundingMode.HALF_UP)
        );

        return rsi;
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
