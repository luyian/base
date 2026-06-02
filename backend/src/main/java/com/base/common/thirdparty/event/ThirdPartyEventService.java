package com.base.common.thirdparty.event;

import com.base.common.thirdparty.ThirdPartyPlatform;

/**
 * 第三方事件监听服务接口
 *
 * @author base
 */
public interface ThirdPartyEventService {

    /**
     * 获取平台标识
     *
     * @return 平台枚举
     */
    ThirdPartyPlatform getPlatform();

    /**
     * 启动事件监听
     */
    void startListening();

    /**
     * 停止事件监听
     */
    void stopListening();

    /**
     * 是否已连接
     *
     * @return true-已连接
     */
    boolean isConnected();
}
