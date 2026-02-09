package com.base.stock.recommend.service;

import java.time.LocalDate;

/**
 * 打分服务接口
 *
 * @author base
 */
public interface ScoreService {

    /**
     * 对单只股票执行打分
     *
     * @param stockCode 股票代码
     * @param scoreDate 打分日期
     */
    void executeStockScore(String stockCode, LocalDate scoreDate);

    /**
     * 批量打分
     *
     * @param stockCodes 股票代码列表
     * @param scoreDate  打分日期
     */
    void executeBatchScore(java.util.List<String> stockCodes, LocalDate scoreDate);

    /**
     * 对所有股票执行打分
     *
     * @param scoreDate 打分日期
     */
    void executeAllStockScore(LocalDate scoreDate);
}
