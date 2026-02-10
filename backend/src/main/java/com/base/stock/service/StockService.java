package com.base.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.stock.dto.StockQueryRequest;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 股票查询服务接口
 *
 * @author base
 */
public interface StockService {

    /**
     * 分页查询股票列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<StockInfo> pageStocks(StockQueryRequest request);

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

    /**
     * 查询行业选项列表（英文代码 -> 中文名称）
     *
     * @return 行业选项列表
     */
    List<Map<String, String>> listIndustryOptions();
}
