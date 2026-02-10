package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.util.ChineseConvertUtil;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockService;
import com.base.stock.service.StockSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 股票查询服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockInfoMapper stockInfoMapper;
    private final StockKlineMapper stockKlineMapper;
    private final StockSyncService stockSyncService;

    @Override
    public Page<StockInfo> pageStocks(int page, int size, String market, String keyword) {
        Page<StockInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StockInfo::getDeleted, 0)
                .eq(market != null && !market.isEmpty(), StockInfo::getMarket, market);

        // 支持简繁体互查
        if (keyword != null && !keyword.isEmpty()) {
            String traditional = ChineseConvertUtil.toTraditional(keyword);
            String simplified = ChineseConvertUtil.toSimplified(keyword);

            wrapper.and(w -> {
                // 按股票代码查询
                w.like(StockInfo::getStockCode, keyword);
                // 按原始关键字查询股票名称
                w.or().like(StockInfo::getStockName, keyword);
                // 按繁体关键字查询（如果与原始不同）
                if (!traditional.equals(keyword)) {
                    w.or().like(StockInfo::getStockName, traditional);
                }
                // 按简体关键字查询（如果与原始不同）
                if (!simplified.equals(keyword)) {
                    w.or().like(StockInfo::getStockName, simplified);
                }
            });
        }

        wrapper.orderByAsc(StockInfo::getStockCode);
        return stockInfoMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public StockInfo getByStockCode(String stockCode) {
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStockCode, stockCode)
                .eq(StockInfo::getDeleted, 0);
        return stockInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<StockKline> listKlineData(String stockCode, LocalDate startDate, LocalDate endDate) {
        // 异步更新股票详情信息
        asyncUpdateStockInfo(stockCode);

        LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockKline::getStockCode, stockCode)
                .ge(startDate != null, StockKline::getTradeDate, startDate)
                .le(endDate != null, StockKline::getTradeDate, endDate)
                .orderByAsc(StockKline::getTradeDate);
        return stockKlineMapper.selectList(wrapper);
    }

    /**
     * 异步更新股票详情信息
     *
     * @param stockCode 股票代码
     */
    @Async
    public void asyncUpdateStockInfo(String stockCode) {
        try {
            log.debug("异步更新股票详情，stockCode: {}", stockCode);
            stockSyncService.syncStockInfo(stockCode);
        } catch (Exception e) {
            log.warn("异步更新股票详情失败，stockCode: {}, error: {}", stockCode, e.getMessage());
        }
    }
}
