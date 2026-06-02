package com.base.approval.controller;

import com.base.approval.entity.EventCallbackLog;
import com.base.approval.service.EventCallbackService;
import com.base.common.result.Result;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEventService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件日志控制器
 *
 * @author base
 */
@RestController
@RequestMapping("/approval/event-log")
@RequiredArgsConstructor
@Api(tags = "事件回调日志")
public class EventLogController {

    private final EventCallbackService eventCallbackService;
    private final List<ThirdPartyEventService> eventServiceList;

    @GetMapping("/list")
    @ApiOperation("事件日志分页")
    public Result<IPage<EventCallbackLog>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") int pageSize,
            @ApiParam("事件类型") @RequestParam(required = false) String eventType,
            @ApiParam("处理状态") @RequestParam(required = false) String status) {
        return Result.success(eventCallbackService.pageList(pageNum, pageSize, eventType, status));
    }

    @GetMapping("/ws-status")
    @ApiOperation("WebSocket 连接状态")
    public Result<Map<String, Object>> wsStatus() {
        Map<String, Object> statusMap = new HashMap<>(4);
        for (ThirdPartyEventService service : eventServiceList) {
            statusMap.put(service.getPlatform().getCode(), service.isConnected());
        }
        return Result.success(statusMap);
    }

    @PostMapping("/retry/{id}")
    @ApiOperation("手动重试事件")
    public Result<Void> retry(@PathVariable Long id) {
        EventCallbackLog logEntity = eventCallbackService.getById(id);
        if (logEntity == null) {
            return Result.error("事件不存在");
        }
        // 重置状态为待处理，由定时任务拾取
        logEntity.setStatus("PENDING");
        logEntity.setRetryCount(0);
        logEntity.setNextRetryAt(null);
        logEntity.setErrorMessage(null);
        // 直接更新
        eventCallbackService.saveEvent(
                ThirdPartyPlatform.fromCode(logEntity.getPlatform()),
                logEntity.getEventId() + "_retry_" + System.currentTimeMillis(),
                logEntity.getEventType(),
                logEntity.getEventPayload());
        return Result.success();
    }
}
