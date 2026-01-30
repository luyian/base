package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.entity.Watchlist;
import com.base.stock.mapper.WatchlistMapper;
import com.base.stock.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final WatchlistMapper watchlistMapper;

    @Override
    public List<Watchlist> listByUserId(Long userId) {
        LambdaQueryWrapper<Watchlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Watchlist::getUserId, userId)
                .eq(Watchlist::getDeleted, 0)
                .orderByAsc(Watchlist::getSortOrder)
                .orderByDesc(Watchlist::getCreateTime);
        return watchlistMapper.selectList(wrapper);
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
}
