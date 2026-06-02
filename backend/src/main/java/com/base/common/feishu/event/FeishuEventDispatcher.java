package com.base.common.feishu.event;

import com.alibaba.fastjson2.JSONObject;
import com.base.approval.entity.EventCallbackLog;
import com.base.approval.service.EventCallbackService;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEvent;
import com.base.common.thirdparty.event.ThirdPartyEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 飞书事件分发器
 * 先入库再处理，保证事件不丢失
 *
 * @author base
 */
@Slf4j
@Component
public class FeishuEventDispatcher {

    @Autowired
    private EventCallbackService eventCallbackService;

    @Autowired(required = false)
    private List<ThirdPartyEventHandler> handlerList;

    private final Map<String, ThirdPartyEventHandler> handlerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (handlerList == null) {
            return;
        }
        for (ThirdPartyEventHandler handler : handlerList) {
            if (handler.getPlatform() == ThirdPartyPlatform.FEISHU) {
                handlerMap.put(handler.getEventType(), handler);
            }
        }
        log.info("飞书事件处理器初始化完成，已注册 {} 个: {}", handlerMap.size(), handlerMap.keySet());
    }

    /**
     * 分发飞书事件消息
     *
     * @param rawMessage WebSocket 收到的原始消息
     */
    public void dispatch(String rawMessage) {
        JSONObject msg;
        try {
            msg = JSONObject.parseObject(rawMessage);
        } catch (Exception e) {
            log.warn("飞书事件消息解析失败: {}", rawMessage, e);
            return;
        }

        // 飞书 WebSocket 协议：type 字段区分消息类型
        String type = msg.getString("type");
        if ("pong".equals(type)) {
            return;
        }

        // 事件消息
        JSONObject header = msg.getJSONObject("header");
        if (header == null) {
            log.debug("非事件消息，跳过: type={}", type);
            return;
        }

        String eventId = header.getString("event_id");
        String eventType = header.getString("event_type");
        String createTime = header.getString("create_time");

        log.info("收到飞书事件: eventId={}, eventType={}", eventId, eventType);

        // 先入库（幂等）
        EventCallbackLog logEntity = eventCallbackService.saveEvent(
                ThirdPartyPlatform.FEISHU, eventId, eventType, rawMessage);
        if (logEntity == null) {
            log.debug("事件已处理过，跳过: eventId={}", eventId);
            return;
        }

        // 查找处理器
        ThirdPartyEventHandler handler = handlerMap.get(eventType);
        if (handler == null) {
            log.debug("无对应处理器，跳过: eventType={}", eventType);
            eventCallbackService.markSkipped(logEntity.getId());
            return;
        }

        // 构建通用事件对象
        ThirdPartyEvent event = new ThirdPartyEvent();
        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setPlatform(ThirdPartyPlatform.FEISHU);
        event.setPayload(rawMessage);
        if (createTime != null) {
            try {
                long ts = Long.parseLong(createTime);
                event.setEventTime(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(ts), ZoneId.systemDefault()));
            } catch (NumberFormatException ignored) {
            }
        }

        // 处理事件
        try {
            handler.handle(event);
            eventCallbackService.markSuccess(logEntity.getId());
        } catch (Exception e) {
            log.error("飞书事件处理失败: eventId={}, eventType={}", eventId, eventType, e);
            eventCallbackService.markFailed(logEntity.getId(), e.getMessage());
        }
    }
}
