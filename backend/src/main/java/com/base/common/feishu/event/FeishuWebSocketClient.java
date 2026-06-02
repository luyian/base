package com.base.common.feishu.event;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.feishu.client.FeishuApiClient;
import com.base.common.feishu.config.FeishuConfig;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 飞书 WebSocket 长连接客户端
 * 通过飞书长连接接收事件推送，无需暴露公网地址
 *
 * @author base
 */
@Slf4j
@Component
public class FeishuWebSocketClient {

    private static final String WS_ENDPOINT_URL = "/callback/ws/endpoint";
    private static final long RECONNECT_DELAY_SECONDS = 30;
    private static final long PING_INTERVAL_SECONDS = 120;

    @Autowired
    private FeishuConfig feishuConfig;

    @Autowired
    private FeishuApiClient feishuApiClient;

    @Autowired
    private FeishuEventDispatcher eventDispatcher;

    private WebSocketClient wsClient;
    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    /**
     * 建立 WebSocket 连接
     */
    public synchronized void connect() {
        if (running) {
            log.info("飞书 WebSocket 已在运行中");
            return;
        }
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "feishu-ws-scheduler");
            t.setDaemon(true);
            return t;
        });
        doConnect();
    }

    /**
     * 断开连接
     */
    public synchronized void disconnect() {
        running = false;
        if (wsClient != null) {
            try {
                wsClient.close();
            } catch (Exception e) {
                log.warn("关闭 WebSocket 连接异常", e);
            }
            wsClient = null;
        }
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        log.info("飞书 WebSocket 已断开");
    }

    /**
     * 是否已连接
     */
    public boolean isConnected() {
        return wsClient != null && wsClient.isOpen();
    }

    private void doConnect() {
        try {
            // 获取 WebSocket 连接地址
            Map<String, Object> body = new HashMap<>(4);
            JSONObject result = feishuApiClient.post(WS_ENDPOINT_URL, body);
            JSONObject data = result.getJSONObject("data");
            String wsUrl = data.getString("URL");
            if (wsUrl == null || wsUrl.isEmpty()) {
                wsUrl = data.getString("url");
            }
            if (wsUrl == null || wsUrl.isEmpty()) {
                log.error("获取飞书 WebSocket 地址失败: {}", result);
                scheduleReconnect();
                return;
            }

            log.info("飞书 WebSocket 连接地址: {}", wsUrl);

            URI uri = new URI(wsUrl);
            wsClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("飞书 WebSocket 连接已建立");
                }

                @Override
                public void onMessage(String message) {
                    try {
                        eventDispatcher.dispatch(message);
                    } catch (Exception e) {
                        log.error("处理飞书 WebSocket 消息异常", e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("飞书 WebSocket 连接关闭: code={}, reason={}, remote={}", code, reason, remote);
                    if (running) {
                        scheduleReconnect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    log.error("飞书 WebSocket 连接异常", ex);
                }
            };

            wsClient.setConnectionLostTimeout((int) PING_INTERVAL_SECONDS);
            wsClient.connect();
        } catch (Exception e) {
            log.error("飞书 WebSocket 连接失败", e);
            if (running) {
                scheduleReconnect();
            }
        }
    }

    private void scheduleReconnect() {
        if (!running || scheduler == null || scheduler.isShutdown()) {
            return;
        }
        log.info("将在 {} 秒后重新连接飞书 WebSocket", RECONNECT_DELAY_SECONDS);
        scheduler.schedule(this::doConnect, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
    }
}
