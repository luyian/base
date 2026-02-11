package com.base.common.feishu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发送飞书消息请求 DTO
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@ApiModel("发送飞书消息请求")
public class FeishuSendMessageRequest {

    /**
     * 接收者 ID（飞书 open_id / user_id / union_id / email）
     */
    @NotBlank(message = "接收者 ID 不能为空")
    @ApiModelProperty(value = "接收者 ID", required = true)
    private String receiveId;

    /**
     * 接收者 ID 类型，默认 open_id
     */
    @ApiModelProperty(value = "接收者 ID 类型", example = "open_id")
    private String receiveIdType = "open_id";

    /**
     * 消息类型（text / image / file）
     */
    @NotBlank(message = "消息类型不能为空")
    @ApiModelProperty(value = "消息类型", required = true, example = "text")
    private String msgType;

    /**
     * 消息内容 JSON 字符串
     */
    @NotBlank(message = "消息内容不能为空")
    @ApiModelProperty(value = "消息内容", required = true)
    private String content;
}
