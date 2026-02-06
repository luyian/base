package com.base.stock.recommend.strategy;

import com.base.stock.recommend.entity.ScoreRule;

/**
 * 打分策略接口
 * 所有打分规则必须实现此接口
 *
 * @author base
 */
public interface ScoreStrategy {

    /**
     * 获取策略编码（对应规则表的rule_code）
     *
     * @return 策略编码
     */
    String getStrategyCode();

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    String getStrategyName();

    /**
     * 执行打分
     *
     * @param context 打分上下文（包含股票代码、K线数据、规则配置等）
     * @return 打分结果
     */
    ScoreResult execute(ScoreContext context);

    /**
     * 验证规则配置是否有效
     *
     * @param rule 规则配置
     * @return true-有效，false-无效
     */
    boolean validateConfig(ScoreRule rule);

    /**
     * 是否支持实时打分（预留接口，未来扩展）
     *
     * @return true-支持，false-不支持
     */
    default boolean supportRealtime() {
        return false;
    }
}
