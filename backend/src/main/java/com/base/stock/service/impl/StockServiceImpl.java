package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public Page<StockInfo> pageStocks(int page, int size, String market, String keyword) {
        Page<StockInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StockInfo::getDeleted, 0)
                .eq(market != null && !market.isEmpty(), StockInfo::getMarket, market)
                .and(keyword != null && !keyword.isEmpty(), w ->
                        w.like(StockInfo::getStockCode, keyword)
                                .or()
                                .like(StockInfo::getStockName, keyword))
                .orderByAsc(StockInfo::getStockCode);

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
        LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockKline::getStockCode, stockCode)
                .ge(startDate != null, StockKline::getTradeDate, startDate)
                .le(endDate != null, StockKline::getTradeDate, endDate)
                .orderByAsc(StockKline::getTradeDate);
        return stockKlineMapper.selectList(wrapper);
    }
}
