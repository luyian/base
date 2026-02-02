package com.base.stock.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.client.ITickApiClient;
import com.base.stock.dto.MinuteKlineResponse;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.Watchlist;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.WatchlistMapper;
import com.base.stock.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自选股票服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final WatchlistMapper watchlistMapper;
    private final ITickApiClient iTickApiClient;
    private final StockInfoMapper stockInfoMapper;

    @Override
    public List<Watchlist> listByUserId(Long userId) {
        return watchlistMapper.selectListWithStockInfo(userId);
    }

    @Override
    public List<String> listStockCodesByUserId(Long userId) {
        List<Watchlist> list = listByUserId(userId);
        return list.stream()
                .map(Watchlist::getStockCode)
                .collect(Collectors.toList());
    }

    @Override
    public Long addWatchlist(Long userId, String stockCode, String remark) {
        // 检查是否已存在
        if (isInWatchlist(userId, stockCode)) {
            throw new RuntimeException("该股票已在自选列表中");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setStockCode(stockCode);
        watchlist.setRemark(remark);
        watchlist.setSortOrder(0);

        watchlistMapper.insert(watchlist);
        log.info("添加自选股票成功，userId: {}, stockCode: {}", userId, stockCode);
        return watchlist.getId();
    }

    @Override
    public void deleteWatchlist(Long id) {
        watchlistMapper.deleteById(id);
        log.info("删除自选股票成功，id: {}", id);
    }

    @Override
    public void batchDeleteWatchlist(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            watchlistMapper.deleteBatchIds(ids);
            log.info("批量删除自选股票成功，ids: {}", ids);
        }
    }

    @Override
    public void updateSortOrder(Long id, Integer sortOrder) {
        Watchlist watchlist = watchlistMapper.selectById(id);
        if (watchlist != null) {
            watchlist.setSortOrder(sortOrder);
            watchlistMapper.updateById(watchlist);
        }
    }

    @Override
    public boolean isInWatchlist(Long userId, String stockCode) {
        LambdaQueryWrapper<Watchlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Watchlist::getUserId, userId)
                .eq(Watchlist::getStockCode, stockCode)
                .eq(Watchlist::getDeleted, 0);
        return watchlistMapper.selectCount(wrapper) > 0;
    }

    @Override
    public MinuteKlineResponse getMinuteKline(String stockCode, int kType, Long et, int limit) {
        // 调用 iTick API 获取分钟K线数据
        String jsonResponse = iTickApiClient.fetchMinuteKlineData(stockCode, kType, et, limit);

        MinuteKlineResponse response = new MinuteKlineResponse();
        response.setStockCode(stockCode);
        response.setKType(kType);

        // 查询股票信息
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStockCode, stockCode);
        StockInfo stockInfo = stockInfoMapper.selectOne(wrapper);
        if (stockInfo != null) {
            response.setStockName(stockInfo.getStockName());
            response.setMarket(stockInfo.getMarket());
        }

        // 解析 JSON 响应
        List<MinuteKlineResponse.MinuteKlineItem> klineList = new ArrayList<>();
        try {
            JSONObject root = JSONUtil.parseObj(jsonResponse);
            JSONArray dataArray = root.getJSONArray("data");
            if (dataArray != null) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    MinuteKlineResponse.MinuteKlineItem klineItem = new MinuteKlineResponse.MinuteKlineItem();

                    // 解析时间戳
                    Long timestamp = item.getLong("t");
                    if (timestamp != null) {
                        klineItem.setTimestamp(timestamp);
                        String tradeTime = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault())
                                .format(TIME_FORMATTER);
                        klineItem.setTradeTime(tradeTime);
                    }

                    // 解析价格数据
                    klineItem.setOpenPrice(item.getBigDecimal("o"));
                    klineItem.setHighPrice(item.getBigDecimal("h"));
                    klineItem.setLowPrice(item.getBigDecimal("l"));
                    klineItem.setClosePrice(item.getBigDecimal("c"));
                    klineItem.setVolume(item.getLong("v"));

                    klineList.add(klineItem);
                }
            }
        } catch (Exception e) {
            log.error("解析分钟K线数据失败，stockCode: {}, error: {}", stockCode, e.getMessage());
        }

        response.setKlineList(klineList);

        // 判断是否还有更多数据
        response.setHasMore(klineList.size() >= limit);

        // 设置最早时间戳（用于加载更多历史数据）
        if (!klineList.isEmpty()) {
            response.setEarliestTimestamp(klineList.get(0).getTimestamp());
        }

        return response;
    }
}
