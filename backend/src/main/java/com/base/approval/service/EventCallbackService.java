package com.base.approval.service;

import com.base.approval.entity.EventCallbackLog;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 事件回调服务接口
 *
 * @author base
 */
public interface EventCallbackService {

    /**
     * 保存事件（幂等，重复事件返回 null）
     */
    EventCallbackLog saveEvent(ThirdPartyPlatform platform, String eventId,
                               String eventType, String payload);

    /**
     * 标记处理成功
     */
    void markSuccess(Long logId);

    /**
     * 标记已跳过（无对应处理器）
     */
    void markSkipped(Long logId);

    /**
     * 标记处理失败，设置重试
     */
    void markFailed(Long logId, String errorMessage);

    /**
     * 查询待重试的事件列表
     */
    List<EventCallbackLog> findPendingRetry();

    /**
     * 根据ID查询事件日志
     */
    EventCallbackLog getById(Long id);

    /**
     * 分页查询事件日志
     */
    IPage<EventCallbackLog> pageList(int pageNum, int pageSize, String eventType, String status);
}
