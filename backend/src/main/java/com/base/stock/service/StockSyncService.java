package com.base.stock.service;

import java.time.LocalDate;

/**
 * 股票同步服务接口
 *
 * @author base
 */
public interface StockSyncService {

    /**
     * 同步股票列表
     *
     * @param market 市场代码
     * @return 同步数量
     */
    int syncStockList(String market);

    /**
     * 同步单只股票的 K 线数据
     *
     * @param stockCode 股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 同步数量
     */
    int syncKlineData(String stockCode, LocalDate startDate, LocalDate endDate);

    /**
     * 批量同步自选股票的 K 线数据
     *
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 同步数量
     */
    int batchSyncKlineData(Long userId, LocalDate startDate, LocalDate endDate);
}
