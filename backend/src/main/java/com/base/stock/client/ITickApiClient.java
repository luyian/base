package com.base.stock.client;

import java.time.LocalDate;

/**
 * iTick API 客户端接口
 *
 * @author base
 */
public interface ITickApiClient {

    /**
     * 获取股票列表（原始 JSON 数据）
     *
     * @param market 市场代码
     * @return JSON 字符串
     */
    String fetchStockList(String market);

    /**
     * 获取 K 线数据（原始 JSON 数据）
     *
     * @param stockCode 股票代码
     * @param period    周期（day-日K）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return JSON 字符串
     */
    String fetchKlineData(String stockCode, String period, LocalDate startDate, LocalDate endDate);

    /**
     * 获取分钟K线数据（实时查询，不存库）
     *
     * @param stockCode 股票代码
     * @param kType     K线类型：1=1分钟，5=5分钟
     * @param et        结束时间戳（毫秒），用于分页加载历史数据，为null时获取最新数据
     * @param limit     返回条数，默认100
     * @return JSON 字符串
     */
    String fetchMinuteKlineData(String stockCode, int kType, Long et, int limit);
}
