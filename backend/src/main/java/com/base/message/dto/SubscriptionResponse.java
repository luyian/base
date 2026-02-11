package com.base.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订阅响应 DTO
 *
 * @author base
 */
@Data
public class SubscriptionResponse {

    /** 订阅类型 */
    private String subType;

    /** 订阅类型描述 */
    private String subTypeDesc;

    /** 推送渠道 */
    private String channel;

    /** 渠道描述 */
    private String channelDesc;

    /** 是否启用 */
    private Boolean enabled;

    /** 最后推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastPushTime;

    /** 最后推送状态：0-失败，1-成功 */
    private Integer lastPushStatus;

    /** 最后推送结果描述 */
    private String lastPushMsg;
}
