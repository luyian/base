package com.base.common.feishu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 绑定飞书账号请求 DTO
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@ApiModel("绑定飞书账号请求")
public class FeishuBindRequest {

    /**
     * 飞书 open_id
     */
    @NotBlank(message = "飞书 open_id 不能为空")
    @ApiModelProperty(value = "飞书 open_id", required = true)
    private String openId;

    /**
     * 飞书用户名（可选）
     */
    @ApiModelProperty(value = "飞书用户名")
    private String feishuName;
}
