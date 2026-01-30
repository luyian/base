package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.client.ITickApiClient;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.factory.DataFactory;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockSyncService;
import com.base.stock.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 股票同步服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockSyncServiceImpl implements StockSyncService {

    private final ITickApiClient iTickApiClient;
    private final DataFactory dataFactory;
    private final StockInfoMapper stockInfoMapper;
    private final StockKlineMapper stockKlineMapper;
    private final WatchlistService watchlistService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncStockList(String market) {
        log.info("开始同步股票列表，market: {}", market);

        // 拉取数据
        String json = iTickApiClient.fetchStockList(market);

        // 转换数据
        List<StockInfo> stockList = dataFactory.transform(json, "itick_stock_list", StockInfo.class);

        if (stockList.isEmpty()) {
            log.warn("未获取到股票数据，market: {}", market);
            return 0;
        }

        int count = 0;
        for (StockInfo stock : stockList) {
            stock.setMarket(market);
            stock.setStatus(1);

            // 检查是否已存在
            LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockInfo::getStockCode, stock.getStockCode());
            StockInfo existing = stockInfoMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新
                stock.setId(existing.getId());
                stockInfoMapper.updateById(stock);
            } else {
                // 新增
                stockInfoMapper.insert(stock);
            }
            count++;
        }

        log.info("同步股票列表完成，market: {}, count: {}", market, count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncKlineData(String stockCode, LocalDate startDate, LocalDate endDate) {
        log.info("开始同步K线数据，stockCode: {}, startDate: {}, endDate: {}", stockCode, startDate, endDate);

        // 如果没有指定开始日期，查询本地最新日期
        if (startDate == null) {
            LocalDate latestDate = stockKlineMapper.selectLatestTradeDate(stockCode);
            if (latestDate != null) {
                startDate = latestDate.plusDays(1);
            } else {
                // 默认拉取近1个月数据
                startDate = LocalDate.now().minusMonths(1);
            }
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 如果开始日期大于结束日期，无需同步
        if (startDate.isAfter(endDate)) {
            log.info("无需同步，startDate > endDate");
            return 0;
        }

        // 拉取数据
        String json = iTickApiClient.fetchKlineData(stockCode, "day", startDate, endDate);

        // 转换数据
        List<StockKline> klineList = dataFactory.transform(json, "itick_kline_daily", StockKline.class);

        if (klineList.isEmpty()) {
            log.warn("未获取到K线数据，stockCode: {}", stockCode);
            return 0;
        }

        int count = 0;
        for (StockKline kline : klineList) {
            kline.setStockCode(stockCode);

            // 检查是否已存在
            LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockKline::getStockCode, kline.getStockCode())
                    .eq(StockKline::getTradeDate, kline.getTradeDate());
            StockKline existing = stockKlineMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新
                kline.setId(existing.getId());
                stockKlineMapper.updateById(kline);
            } else {
                // 新增
                stockKlineMapper.insert(kline);
            }
            count++;
        }

        log.info("同步K线数据完成，stockCode: {}, count: {}", stockCode, count);
        return count;
    }

    @Override
    public int batchSyncKlineData(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("开始批量同步K线数据，userId: {}", userId);

        // 获取用户自选股票列表
        List<String> stockCodes = watchlistService.listStockCodesByUserId(userId);

        if (stockCodes.isEmpty()) {
            log.warn("用户没有自选股票，userId: {}", userId);
            return 0;
        }

        int totalCount = 0;
        for (String stockCode : stockCodes) {
            try {
                int count = syncKlineData(stockCode, startDate, endDate);
                totalCount += count;
            } catch (Exception e) {
                log.error("同步K线数据失败，stockCode: {}", stockCode, e);
            }
        }

        log.info("批量同步K线数据完成，userId: {}, totalCount: {}", userId, totalCount);
        return totalCount;
    }
}
