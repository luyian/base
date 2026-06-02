package com.base.common.thirdparty.event;

import com.base.common.thirdparty.ThirdPartyPlatform;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用第三方事件模型
 *
 * @author base
 */
@Data
public class ThirdPartyEvent {

    /**
     * 事件唯一ID
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 来源平台
     */
    private ThirdPartyPlatform platform;

    /**
     * 事件原始 JSON
     */
    private String payload;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;
}
