package com.base.stock.fund.dto;

import com.base.stock.fund.entity.FundConfig;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基金估值响应 DTO
 *
 * @author base
 */
@Data
public class FundValuationResponse {

    /**
     * 基金ID
     */
    private Long fundId;

    /**
     * 基金名称
     */
    private String fundName;

    /**
     * 基金代码
     */
    private String fundCode;

    /**
     * 基金描述
     */
    private String description;

    /**
     * 持仓数量
     */
    private Integer holdingCount;

    /**
     * 估算涨跌幅(%)
     */
    private BigDecimal estimatedChangePercent;

    /**
     * 成功获取报价的股票数量
     */
    private Integer successCount;

    /**
     * 失败获取报价的股票数量
     */
    private Integer failCount;

    /**
     * 总权重(%)
     */
    private BigDecimal totalWeight;

    /**
     * 股票报价明细
     */
    private List<StockQuote> quotes;

    /**
     * 基金配置信息
     */
    private FundConfig fundConfig;

    /**
     * 是否全部获取成功
     */
    private Boolean allSuccess;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 缓存时间（用于前端显示）
     */
    private Long cacheTime;
}
