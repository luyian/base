package com.base.message.service;

/**
 * 消息推送调度服务接口
 *
 * @author base
 */
public interface MessagePushService {

    /**
     * 执行推送（按订阅类型）
     *
     * @param subType 订阅类型
     */
    void executePush(String subType);
}
