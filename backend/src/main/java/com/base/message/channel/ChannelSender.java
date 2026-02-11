package com.base.message.channel;

/**
 * 渠道发送器接口
 *
 * @author base
 */
public interface ChannelSender {

    /**
     * 获取渠道标识
     *
     * @return 渠道标识
     */
    String getChannel();

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    void send(Long userId, String message);
}
