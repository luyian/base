package com.base.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.base.stock.entity.SyncFailure;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 股票同步失败记录服务接口
 *
 * @author base
 */
public interface SyncFailureService extends IService<SyncFailure> {

    /**
     * 记录同步失败
     *
     * @param stockCode 股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param reason    失败原因
     */
    void recordFailure(String stockCode, LocalDate startDate, LocalDate endDate, String reason);

    /**
     * 查询待重试的失败记录
     *
     * @param stockCode     股票代码（可选）
     * @param maxRetryCount 最大重试次数
     * @return 失败记录列表
     */
    List<SyncFailure> listPendingRetry(String stockCode, int maxRetryCount);

    /**
     * 更新失败记录状态
     *
     * @param id         记录ID
     * @param status     状态
     * @param retryCount 重试次数
     */
    void updateStatus(Long id, int status, int retryCount);

    /**
     * 标记为成功
     *
     * @param id 记录ID
     */
    void markSuccess(Long id);

    /**
     * 标记为放弃重试
     *
     * @param id 记录ID
     */
    void markAbandoned(Long id);

    /**
     * 批量标记为成功
     *
     * @param ids 记录ID列表
     */
    void batchMarkSuccess(List<Long> ids);

    /**
     * 批量标记为放弃重试
     *
     * @param ids 记录ID列表
     */
    void batchMarkAbandoned(List<Long> ids);

    /**
     * 批量更新重试次数
     *
     * @param retryCountMap ID到重试次数的映射
     */
    void batchUpdateRetryCount(Map<Long, Integer> retryCountMap);
}
