package com.base.stock.fund.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票报价 DTO
 *
 * @author base
 */
@Data
public class StockQuote {

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 市场
     */
    private String market;

    /**
     * 当前价格
     */
    private BigDecimal price;

    /**
     * 昨收价
     */
    private BigDecimal lastClose;

    /**
     * 涨跌额
     */
    private BigDecimal change;

    /**
     * 涨跌幅(%)
     */
    private BigDecimal changePercent;

    /**
     * 权重占比(%)
     */
    private BigDecimal weight;

    /**
     * 加权涨跌幅(%)
     */
    private BigDecimal weightedChangePercent;

    /**
     * 是否获取成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMsg;
}
