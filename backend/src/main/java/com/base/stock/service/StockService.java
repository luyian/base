package com.base.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;

import java.time.LocalDate;
import java.util.List;

/**
 * 股票查询服务接口
 *
 * @author base
 */
public interface StockService {

    /**
     * 分页查询股票列表
     *
     * @param page      页码
     * @param size      每页数量
     * @param market    市场（可选）
     * @param keyword   关键词（可选，匹配代码或名称）
     * @return 分页结果
     */
    Page<StockInfo> pageStocks(int page, int size, String market, String keyword);

    /**
     * 根据股票代码获取股票信息
     *
     * @param stockCode 股票代码
     * @return 股票信息
     */
    StockInfo getByStockCode(String stockCode);

    /**
     * 查询股票 K 线数据
     *
     * @param stockCode 股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return K 线数据列表
     */
    List<StockKline> listKlineData(String stockCode, LocalDate startDate, LocalDate endDate);
}
