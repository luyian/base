package com.base.message.service;

import com.base.message.dto.SubscriptionResponse;

import java.util.List;

/**
 * 订阅管理服务接口
 *
 * @author base
 */
public interface SubscriptionService {

    /**
     * 查询当前用户的订阅列表
     *
     * @return 订阅列表
     */
    List<SubscriptionResponse> listMySubscriptions();

    /**
     * 开启/关闭订阅
     *
     * @param subType 订阅类型
     * @param channel 推送渠道
     * @param enabled 是否启用
     */
    void toggleSubscription(String subType, String channel, boolean enabled);
}
