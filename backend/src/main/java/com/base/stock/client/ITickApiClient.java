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
}
