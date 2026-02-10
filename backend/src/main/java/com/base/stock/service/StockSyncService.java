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

    /**
     * 批量同步所有股票的 K 线数据（按市场）
     *
     * @param market    市场代码（HK/SH/SZ），为空则同步所有市场
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 同步数量
     */
    int batchSyncAllKlineData(String market, LocalDate startDate, LocalDate endDate);

    /**
     * 同步单只股票的详情信息
     *
     * @param stockCode 股票代码
     * @return 是否成功
     */
    boolean syncStockInfo(String stockCode);

    /**
     * 批量同步股票详情信息（按市场）
     *
     * @param market 市场代码（HK/SH/SZ），为空则同步所有市场
     * @return 同步数量
     */
    int batchSyncStockInfo(String market);
}
