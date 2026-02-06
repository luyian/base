package com.base.stock.recommend.strategy;

import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.recommend.entity.ScoreRule;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 打分上下文
 * 封装打分所需的所有数据
 *
 * @author base
 */
@Data
@Builder
public class ScoreContext {

    /**
     * 股票信息
     */
    private StockInfo stockInfo;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 打分日期
     */
    private LocalDate scoreDate;

    /**
     * K线数据（按日期升序，最新数据在最后）
     */
    private List<StockKline> klineData;

    /**
     * 规则配置
     */
    private ScoreRule rule;

    /**
     * 规则参数（从config_json解析）
     */
    private Map<String, Object> ruleParams;

    /**
     * 扩展数据（用于策略间传递数据）
     */
    private Map<String, Object> extData;
}
