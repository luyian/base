package com.base.common.feishu.event;

import com.base.common.feishu.config.FeishuConfig;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * 飞书事件监听服务
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuEventService implements ThirdPartyEventService {

    private final FeishuConfig feishuConfig;
    private final FeishuWebSocketClient webSocketClient;

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public void startListening() {
        webSocketClient.connect();
        log.info("飞书事件监听已启动");
    }

    @Override
    public void stopListening() {
        webSocketClient.disconnect();
        log.info("飞书事件监听已停止");
    }

    @Override
    public boolean isConnected() {
        return webSocketClient.isConnected();
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
