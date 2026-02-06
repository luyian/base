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
 * 均线多头排列策略
 * 判断短期均线是否在长期均线上方（MA5 > MA10 > MA20 > MA60）
 *
 * @author base
 */
@Slf4j
@Component
public class MaAlignmentStrategy implements ScoreStrategy {

    @Override
    public String getStrategyCode() {
        return "MA_ALIGNMENT";
    }

    @Override
    public String getStrategyName() {
        return "均线多头排列";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        List<StockKline> klineData = context.getKlineData();
        if (klineData == null || klineData.size() < 60) {
            return ScoreResult.miss("K线数据不足60条，无法计算MA60");
        }

        // 获取参数
        Map<String, Object> params = context.getRuleParams();
        int ma5Period = getIntParam(params, "ma5", 5);
        int ma10Period = getIntParam(params, "ma10", 10);
        int ma20Period = getIntParam(params, "ma20", 20);
        int ma60Period = getIntParam(params, "ma60", 60);

        // 计算最新的各周期均线
        BigDecimal ma5 = calculateMA(klineData, ma5Period);
        BigDecimal ma10 = calculateMA(klineData, ma10Period);
        BigDecimal ma20 = calculateMA(klineData, ma20Period);
        BigDecimal ma60 = calculateMA(klineData, ma60Period);

        // 判断是否多头排列
        boolean isAlignment = ma5.compareTo(ma10) > 0
                && ma10.compareTo(ma20) > 0
                && ma20.compareTo(ma60) > 0;

        Map<String, Object> detail = new HashMap<>();
        detail.put("ma5", ma5);
        detail.put("ma10", ma10);
        detail.put("ma20", ma20);
        detail.put("ma60", ma60);
        detail.put("isAlignment", isAlignment);

        if (isAlignment) {
            return ScoreResult.hit(context.getRule().getBaseScore(), detail);
        } else {
            return ScoreResult.miss("未形成多头排列");
        }
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        // 验证配置参数是否合法
        return true;
    }

    /**
     * 计算移动平均线
     *
     * @param klineData K线数据
     * @param period    周期
     * @return 均线值
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
     * 获取整型参数
     *
     * @param params       参数Map
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值
     */
    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
