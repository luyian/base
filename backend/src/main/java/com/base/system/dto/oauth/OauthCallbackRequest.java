package com.base.system.dto.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * OAuth 回调请求 DTO
 */
@Data
@ApiModel("OAuth回调请求")
public class OauthCallbackRequest {

    /**
     * 授权码
     */
    @NotBlank(message = "授权码不能为空")
    @ApiModelProperty("授权码")
    private String code;

    /**
     * 状态码（防CSRF）
     */
    @NotBlank(message = "状态码不能为空")
    @ApiModelProperty("状态码")
    private String state;
}
