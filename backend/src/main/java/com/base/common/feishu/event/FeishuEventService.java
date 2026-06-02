package com.base.common.feishu.event;

import com.base.common.feishu.config.FeishuConfig;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEventService;
import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.ws.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 飞书事件监听服务（基于官方 SDK WebSocket 长连接）
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuEventService implements ThirdPartyEventService {

    private final FeishuConfig feishuConfig;
    private final FeishuEventDispatcher eventDispatcher;

    private Client wsClient;
    private Thread wsThread;

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public void startListening() {
        if (wsClient != null) {
            log.info("飞书 WebSocket 已在运行中");
            return;
        }

        try {
            // SDK 事件分发器：注册自定义事件处理器
            EventDispatcher sdkDispatcher = EventDispatcher.newBuilder(
                            feishuConfig.getVerificationToken(),
                            feishuConfig.getEncryptKey())
                    .onCustomizedEvent("approval_instance", new CustomEventHandler() {
                        @Override
                        public void handle(EventReq event) throws Exception {
                            String rawMessage = new String(event.getBody(), StandardCharsets.UTF_8);
                            eventDispatcher.dispatch(rawMessage);
                        }
                    })
                    .onCustomizedEvent("contact.user.updated_v3", new CustomEventHandler() {
                        @Override
                        public void handle(EventReq event) throws Exception {
                            String rawMessage = new String(event.getBody(), StandardCharsets.UTF_8);
                            eventDispatcher.dispatch(rawMessage);
                        }
                    })
                    .build();

            // 创建 SDK WebSocket 客户端
            wsClient = new Client.Builder(
                            feishuConfig.getAppId(),
                            feishuConfig.getAppSecret())
                    .eventHandler(sdkDispatcher)
                    .build();

            // SDK 的 start() 是阻塞方法，需要在独立线程中运行
            wsThread = new Thread(() -> {
                try {
                    log.info("正在启动飞书 WebSocket 长连接...");
                    wsClient.start();
                } catch (Exception e) {
                    log.error("飞书 WebSocket 连接异常", e);
                }
            }, "feishu-sdk-ws");
            wsThread.setDaemon(true);
            wsThread.start();

            log.info("飞书事件监听已启动");

        } catch (Exception e) {
            log.error("飞书事件监听启动失败", e);
        }
    }

    @Override
    public void stopListening() {
        if (wsClient != null) {
            try {
                if (wsThread != null) {
                    wsThread.interrupt();
                }
            } catch (Exception e) {
                log.warn("停止飞书 WebSocket 异常", e);
            }
            wsClient = null;
            wsThread = null;
        }
        log.info("飞书事件监听已停止");
    }

    @Override
    public boolean isConnected() {
        return wsClient != null && wsThread != null && wsThread.isAlive();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (Boolean.TRUE.equals(feishuConfig.getEnabled())
                && Boolean.TRUE.equals(feishuConfig.getEventEnabled())) {
            log.info("应用启动完成，自动连接飞书事件监听");
            startListening();
        } else {
            log.info("飞书事件监听未启用（enabled={}, eventEnabled={}）",
                    feishuConfig.getEnabled(), feishuConfig.getEventEnabled());
        }
    }
}
