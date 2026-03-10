package com.base.common.feishu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发送飞书消息给系统用户请求 DTO
 *
 * @author base
 * @since 2026-03-10
 */
@Data
@ApiModel("发送飞书消息给系统用户请求")
public class FeishuSendToUserRequest {

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
