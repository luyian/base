package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.stock.entity.SyncFailure;
import com.base.stock.mapper.SyncFailureMapper;
import com.base.stock.service.SyncFailureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 股票同步失败记录服务实现类
 *
 * @author base
 */
@Slf4j
@Service
public class SyncFailureServiceImpl extends ServiceImpl<SyncFailureMapper, SyncFailure> implements SyncFailureService {

    /**
     * 待重试状态
     */
    private static final int STATUS_PENDING = 0;

    /**
     * 重试成功状态
     */
    private static final int STATUS_SUCCESS = 1;

    /**
     * 放弃重试状态
     */
    private static final int STATUS_ABANDONED = 2;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void recordFailure(String stockCode, LocalDate startDate, LocalDate endDate, String reason) {
        // 查询是否已存在相同的失败记录
        LambdaQueryWrapper<SyncFailure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SyncFailure::getStockCode, stockCode)
                .eq(SyncFailure::getStartDate, startDate)
                .eq(SyncFailure::getEndDate, endDate)
                .eq(SyncFailure::getStatus, STATUS_PENDING);

        SyncFailure existing = getOne(wrapper);

        if (existing != null) {
            // 更新已有记录
            existing.setFailureReason(reason);
            existing.setRetryCount(existing.getRetryCount() + 1);
            existing.setLastRetryTime(LocalDateTime.now());
            updateById(existing);
            log.info("更新失败记录，stockCode: {}, retryCount: {}", stockCode, existing.getRetryCount());
        } else {
            // 新增失败记录
            SyncFailure failure = new SyncFailure();
            failure.setStockCode(stockCode);
            failure.setStartDate(startDate);
            failure.setEndDate(endDate);
            failure.setFailureReason(reason);
            failure.setRetryCount(0);
            failure.setStatus(STATUS_PENDING);
            failure.setCreateTime(LocalDateTime.now());
            save(failure);
            log.info("新增失败记录，stockCode: {}", stockCode);
        }
    }

    @Override
    public List<SyncFailure> listPendingRetry(String stockCode, int maxRetryCount) {
        LambdaQueryWrapper<SyncFailure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SyncFailure::getStatus, STATUS_PENDING)
                .lt(SyncFailure::getRetryCount, maxRetryCount);

        if (stockCode != null && !stockCode.isEmpty()) {
            wrapper.eq(SyncFailure::getStockCode, stockCode);
        }

        wrapper.orderByAsc(SyncFailure::getRetryCount)
                .orderByAsc(SyncFailure::getCreateTime);

        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, int status, int retryCount) {
        SyncFailure failure = getById(id);
        if (failure != null) {
            failure.setStatus(status);
            failure.setRetryCount(retryCount);
            failure.setLastRetryTime(LocalDateTime.now());
            updateById(failure);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long id) {
        updateStatus(id, STATUS_SUCCESS, 0);
        log.info("标记失败记录为成功，id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAbandoned(Long id) {
        SyncFailure failure = getById(id);
        if (failure != null) {
            failure.setStatus(STATUS_ABANDONED);
            failure.setLastRetryTime(LocalDateTime.now());
            updateById(failure);
            log.info("标记失败记录为放弃重试，id: {}", id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkSuccess(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SyncFailure> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SyncFailure::getId, ids)
                .set(SyncFailure::getStatus, STATUS_SUCCESS)
                .set(SyncFailure::getLastRetryTime, LocalDateTime.now());
        update(wrapper);
        log.info("批量标记失败记录为成功，count: {}", ids.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAbandoned(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SyncFailure> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SyncFailure::getId, ids)
                .set(SyncFailure::getStatus, STATUS_ABANDONED)
                .set(SyncFailure::getLastRetryTime, LocalDateTime.now());
        update(wrapper);
        log.info("批量标记失败记录为放弃重试，count: {}", ids.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateRetryCount(Map<Long, Integer> retryCountMap) {
        if (retryCountMap == null || retryCountMap.isEmpty()) {
            return;
        }
        // 由于每条记录的重试次数不同，需要逐条更新
        // 但使用批量查询减少数据库交互
        List<SyncFailure> failures = listByIds(retryCountMap.keySet());
        for (SyncFailure failure : failures) {
            Integer newRetryCount = retryCountMap.get(failure.getId());
            if (newRetryCount != null) {
                failure.setRetryCount(newRetryCount);
                failure.setLastRetryTime(LocalDateTime.now());
            }
        }
        updateBatchById(failures);
        log.info("批量更新重试次数，count: {}", retryCountMap.size());
    }
}
