package com.base.common.thirdparty.event;

import com.base.common.thirdparty.ThirdPartyPlatform;

/**
 * 第三方事件处理器接口
 *
 * @author base
 */
public interface ThirdPartyEventHandler {

    /**
     * 获取处理的事件类型
     *
     * @return 事件类型标识
     */
    String getEventType();

    /**
     * 获取所属平台
     *
     * @return 平台枚举
     */
    ThirdPartyPlatform getPlatform();

    /**
     * 处理事件
     *
     * @param event 事件对象
     */
    void handle(ThirdPartyEvent event);
}
