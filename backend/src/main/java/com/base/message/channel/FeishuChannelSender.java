package com.base.message.channel;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.feishu.service.FeishuMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 飞书渠道发送器
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeishuChannelSender implements ChannelSender {

    private final FeishuMessageService feishuMessageService;

    @Override
    public String getChannel() {
        return "feishu";
    }

    @Override
    public void send(Long userId, String message) {
        // 构建飞书文本消息内容
        JSONObject content = new JSONObject();
        content.put("text", message);
        feishuMessageService.sendMessageToUser(userId, "text", content.toJSONString());
        log.info("飞书消息发送成功，userId: {}", userId);
    }
}
