package com.base.approval.task;

import com.base.approval.entity.EventCallbackLog;
import com.base.approval.service.EventCallbackService;
import com.base.common.feishu.event.FeishuEventDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件回调重试定时任务
 * 每 5 分钟检查失败事件并重试
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventCallbackRetryTask {

    private final EventCallbackService eventCallbackService;
    private final FeishuEventDispatcher feishuEventDispatcher;

    @Scheduled(fixedDelay = 300000)
    public void retryFailedEvents() {
        List<EventCallbackLog> pendingList = eventCallbackService.findPendingRetry();
        if (pendingList.isEmpty()) {
            return;
        }
        log.info("开始重试失败事件，共 {} 条", pendingList.size());
        for (EventCallbackLog logEntity : pendingList) {
            try {
                feishuEventDispatcher.dispatch(logEntity.getEventPayload());
            } catch (Exception e) {
                log.error("事件重试失败: id={}", logEntity.getId(), e);
            }
        }
    }
}
