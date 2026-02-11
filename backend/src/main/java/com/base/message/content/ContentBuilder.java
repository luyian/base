package com.base.message.content;

/**
 * 内容生成器接口
 *
 * @author base
 */
public interface ContentBuilder {

    /**
     * 获取订阅类型标识
     *
     * @return 订阅类型
     */
    String getSubType();

    /**
     * 生成推送内容
     *
     * @param userId 用户ID
     * @return 推送内容文本，无内容返回 null（跳过推送）
     */
    String build(Long userId);
}
