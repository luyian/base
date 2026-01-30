package com.base.stock.service;

import com.base.stock.entity.Watchlist;

import java.util.List;

/**
 * 自选股票服务接口
 *
 * @author base
 */
public interface WatchlistService {

    /**
     * 查询用户自选股票列表
     *
     * @param userId 用户ID
     * @return 自选股票列表
     */
    List<Watchlist> listByUserId(Long userId);

    /**
     * 查询所有自选股票的股票代码
     *
     * @param userId 用户ID
     * @return 股票代码列表
     */
    List<String> listStockCodesByUserId(Long userId);

    /**
     * 添加自选股票
     *
     * @param userId    用户ID
     * @param stockCode 股票代码
     * @param remark    备注
     * @return 自选ID
     */
    Long addWatchlist(Long userId, String stockCode, String remark);

    /**
     * 删除自选股票
     *
     * @param id 自选ID
     */
    void deleteWatchlist(Long id);

    /**
     * 批量删除自选股票
     *
     * @param ids 自选ID列表
     */
    void batchDeleteWatchlist(List<Long> ids);

    /**
     * 调整排序
     *
     * @param id        自选ID
     * @param sortOrder 排序号
     */
    void updateSortOrder(Long id, Integer sortOrder);

    /**
     * 检查是否已添加自选
     *
     * @param userId    用户ID
     * @param stockCode 股票代码
     * @return 是否已添加
     */
    boolean isInWatchlist(Long userId, String stockCode);
}
