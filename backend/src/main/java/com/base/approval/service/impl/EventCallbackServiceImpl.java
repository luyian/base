package com.base.approval.service.impl;

import com.base.approval.entity.EventCallbackLog;
import com.base.approval.enums.EventCallbackStatus;
import com.base.approval.mapper.EventCallbackLogMapper;
import com.base.approval.service.EventCallbackService;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件回调服务实现
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventCallbackServiceImpl implements EventCallbackService {

    private static final int[] RETRY_INTERVALS_MINUTES = {1, 5, 15, 30, 60};

    private final EventCallbackLogMapper eventCallbackLogMapper;

    @Override
    public EventCallbackLog saveEvent(ThirdPartyPlatform platform, String eventId,
                                      String eventType, String payload) {
        EventCallbackLog logEntity = new EventCallbackLog();
        logEntity.setPlatform(platform.getCode());
        logEntity.setEventId(eventId);
        logEntity.setEventType(eventType);
        logEntity.setEventPayload(payload);
        logEntity.setStatus(EventCallbackStatus.PENDING.getCode());
        logEntity.setRetryCount(0);
        logEntity.setMaxRetry(3);
        logEntity.setCreateTime(LocalDateTime.now());

        try {
            eventCallbackLogMapper.insert(logEntity);
            return logEntity;
        } catch (DuplicateKeyException e) {
            log.debug("事件已存在，跳过入库: eventId={}", eventId);
            return null;
        }
    }

    @Override
    public void markSuccess(Long logId) {
        EventCallbackLog logEntity = eventCallbackLogMapper.selectById(logId);
        if (logEntity == null) {
            return;
        }
        logEntity.setStatus(EventCallbackStatus.SUCCESS.getCode());
        logEntity.setProcessedAt(LocalDateTime.now());
        eventCallbackLogMapper.updateById(logEntity);
    }

    @Override
    public void markSkipped(Long logId) {
        EventCallbackLog logEntity = eventCallbackLogMapper.selectById(logId);
        if (logEntity == null) {
            return;
        }
        logEntity.setStatus(EventCallbackStatus.SKIPPED.getCode());
        logEntity.setProcessedAt(LocalDateTime.now());
        eventCallbackLogMapper.updateById(logEntity);
    }

    @Override
    public void markFailed(Long logId, String errorMessage) {
        EventCallbackLog logEntity = eventCallbackLogMapper.selectById(logId);
        if (logEntity == null) {
            return;
        }
        logEntity.setStatus(EventCallbackStatus.FAILED.getCode());
        logEntity.setErrorMessage(truncate(errorMessage, 2000));
        logEntity.setRetryCount(logEntity.getRetryCount() + 1);

        if (logEntity.getRetryCount() < logEntity.getMaxRetry()) {
            int idx = Math.min(logEntity.getRetryCount() - 1, RETRY_INTERVALS_MINUTES.length - 1);
            logEntity.setNextRetryAt(LocalDateTime.now().plusMinutes(RETRY_INTERVALS_MINUTES[idx]));
        }
        eventCallbackLogMapper.updateById(logEntity);
    }

    @Override
    public List<EventCallbackLog> findPendingRetry() {
        LambdaQueryWrapper<EventCallbackLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventCallbackLog::getStatus, EventCallbackStatus.FAILED.getCode())
                .lt(EventCallbackLog::getRetryCount, 3)
                .le(EventCallbackLog::getNextRetryAt, LocalDateTime.now())
                .orderByAsc(EventCallbackLog::getCreateTime)
                .last("LIMIT 50");
        return eventCallbackLogMapper.selectList(wrapper);
    }

    @Override
    public EventCallbackLog getById(Long id) {
        return eventCallbackLogMapper.selectById(id);
    }

    @Override
    public IPage<EventCallbackLog> pageList(int pageNum, int pageSize, String eventType, String status) {
        LambdaQueryWrapper<EventCallbackLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(eventType)) {
            wrapper.eq(EventCallbackLog::getEventType, eventType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(EventCallbackLog::getStatus, status);
        }
        wrapper.orderByDesc(EventCallbackLog::getCreateTime);
        return eventCallbackLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    private String truncate(String str, int maxLen) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLen ? str.substring(0, maxLen) : str;
    }
}
