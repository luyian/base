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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MACD金叉策略
 * 判断DIF上穿DEA，形成金叉买入信号
 *
 * @author base
 */
@Slf4j
@Component
public class MacdGoldenCrossStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "MACD_GOLDEN_CROSS";
    }

    @Override
    public String getStrategyName() {
        return "MACD金叉";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        Map<String, Object> params = context.getRuleParams();

        int fastPeriod = getIntParam(params, "fastPeriod", 12);
        int slowPeriod = getIntParam(params, "slowPeriod", 26);
        int signalPeriod = getIntParam(params, "signalPeriod", 9);

        // 需要足够的数据计算MACD
        int requiredData = slowPeriod + signalPeriod + 10;
        if (klineData.size() < requiredData) {
            return ScoreResult.miss("K线数据不足" + requiredData + "条");
        }

        try {
            // 计算EMA12和EMA26
            List<BigDecimal> ema12List = calculateEMA(klineData, fastPeriod);
            List<BigDecimal> ema26List = calculateEMA(klineData, slowPeriod);

            // 计算DIF = EMA12 - EMA26
            List<BigDecimal> difList = new ArrayList<>();
            for (int i = 0; i < ema12List.size(); i++) {
                difList.add(ema12List.get(i).subtract(ema26List.get(i)));
            }

            // 计算DEA = DIF的9日EMA
            List<BigDecimal> deaList = calculateEMAFromValues(difList, signalPeriod);

            // 判断是否金叉：前一天DIF < DEA，今天DIF > DEA
            int lastIndex = difList.size() - 1;
            BigDecimal currentDif = difList.get(lastIndex);
            BigDecimal currentDea = deaList.get(lastIndex);
            BigDecimal prevDif = difList.get(lastIndex - 1);
            BigDecimal prevDea = deaList.get(lastIndex - 1);

            boolean isGoldenCross = prevDif.compareTo(prevDea) < 0 && currentDif.compareTo(currentDea) > 0;

            Map<String, Object> detail = new HashMap<>();
            detail.put("currentDif", currentDif);
            detail.put("currentDea", currentDea);
            detail.put("prevDif", prevDif);
            detail.put("prevDea", prevDea);
            detail.put("isGoldenCross", isGoldenCross);

            if (isGoldenCross) {
                return ScoreResult.hit(context.getRule().getBaseScore(), detail);
            } else {
                // 检查是否在近3天内发生过金叉
                for (int i = lastIndex - 1; i >= Math.max(0, lastIndex - 3); i--) {
                    BigDecimal dif = difList.get(i);
                    BigDecimal dea = deaList.get(i);
                    BigDecimal prevDif2 = difList.get(i - 1);
                    BigDecimal prevDea2 = deaList.get(i - 1);

                    if (prevDif2.compareTo(prevDea2) < 0 && dif.compareTo(dea) > 0) {
                        detail.put("goldenCrossDaysAgo", lastIndex - i);
                        return ScoreResult.hit(context.getRule().getBaseScore(), detail);
                    }
                }
                return ScoreResult.miss("未形成MACD金叉");
            }

        } catch (Exception e) {
            log.error("计算MACD失败: {}", e.getMessage(), e);
            return ScoreResult.miss("计算MACD失败: " + e.getMessage());
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }

    /**
     * 计算EMA（指数移动平均）
     *
     * @param klineData K线数据
     * @param period    周期
     * @return EMA列表
     */
    private List<BigDecimal> calculateEMA(List<StockKline> klineData, int period) {
        List<BigDecimal> emaList = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        // 第一个EMA使用简单移动平均
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(klineData.get(i).getClosePrice());
        }
        BigDecimal firstEma = sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
        emaList.add(firstEma);

        // 后续EMA计算：EMA = (当前价格 - 昨日EMA) * 乘数 + 昨日EMA
        for (int i = period; i < klineData.size(); i++) {
            BigDecimal currentPrice = klineData.get(i).getClosePrice();
            BigDecimal prevEma = emaList.get(emaList.size() - 1);
            BigDecimal ema = currentPrice.subtract(prevEma)
                    .multiply(multiplier)
                    .add(prevEma)
                    .setScale(4, RoundingMode.HALF_UP);
            emaList.add(ema);
        }

        return emaList;
    }

    /**
     * 从数值列表计算EMA
     *
     * @param values 数值列表
     * @param period 周期
     * @return EMA列表
     */
    private List<BigDecimal> calculateEMAFromValues(List<BigDecimal> values, int period) {
        List<BigDecimal> emaList = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        // 第一个EMA使用简单移动平均
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(values.get(i));
        }
        BigDecimal firstEma = sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
        emaList.add(firstEma);

        // 后续EMA计算
        for (int i = period; i < values.size(); i++) {
            BigDecimal currentValue = values.get(i);
            BigDecimal prevEma = emaList.get(emaList.size() - 1);
            BigDecimal ema = currentValue.subtract(prevEma)
                    .multiply(multiplier)
                    .add(prevEma)
                    .setScale(4, RoundingMode.HALF_UP);
            emaList.add(ema);
        }

        return emaList;
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
