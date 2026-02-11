package com.base.common.feishu.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 飞书发送消息响应 DTO
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuSendMessageResponse extends FeishuBaseResponse {

    /**
     * 响应数据
     */
    private MessageData data;

    /**
     * 消息数据
     */
    @Data
    public static class MessageData {

        /**
         * 消息 ID
         */
        private String messageId;
    }
}
