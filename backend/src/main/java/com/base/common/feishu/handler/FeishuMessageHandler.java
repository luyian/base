package com.base.common.feishu.handler;

import com.base.common.feishu.constant.FeishuMsgTypeEnum;

/**
 * 飞书消息处理器接口（策略模式核心）
 * 不同消息类型实现此接口，完成消息内容的校验和构建
 *
 * @author base
 * @since 2026-02-11
 */
public interface FeishuMessageHandler {

    /**
     * 获取当前处理器支持的消息类型
     *
     * @return 消息类型枚举
     */
    FeishuMsgTypeEnum getMsgType();

    /**
     * 校验消息内容
     *
     * @param content 消息内容 JSON 字符串
     */
    void validate(String content);

    /**
     * 构建飞书 API 所需的消息内容 JSON 字符串
     *
     * @param content 原始消息内容
     * @return 飞书 API 格式的消息内容
     */
    String buildContent(String content);
}
