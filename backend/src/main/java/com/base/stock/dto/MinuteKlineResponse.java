package com.base.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分钟K线响应DTO
 *
 * @author base
 */
@Data
public class MinuteKlineResponse {

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
     * K线类型：1=1分钟，5=5分钟
     */
    private Integer kType;

    /**
     * K线数据列表
     */
    private List<MinuteKlineItem> klineList;

    /**
     * 是否还有更多历史数据
     */
    private Boolean hasMore;

    /**
     * 最早的时间戳（用于加载更多历史数据）
     */
    private Long earliestTimestamp;

    /**
     * 分钟K线数据项
     */
    @Data
    public static class MinuteKlineItem {

        /**
         * 时间戳（毫秒）
         */
        private Long timestamp;

        /**
         * 交易时间（格式：yyyy-MM-dd HH:mm）
         */
        private String tradeTime;

        /**
         * 开盘价
         */
        private BigDecimal openPrice;

        /**
         * 最高价
         */
        private BigDecimal highPrice;

        /**
         * 最低价
         */
        private BigDecimal lowPrice;

        /**
         * 收盘价
         */
        private BigDecimal closePrice;

        /**
         * 成交量
         */
        private Long volume;
    }
}
