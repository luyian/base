package com.base.open.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 开放接口 Token 请求
 *
 * @author base
 */
@Data
public class OpenApiTokenRequest {

    /**
     * 应用ID
     */
    @NotBlank(message = "appId不能为空")
    private String appId;

    /**
     * 应用密钥
     */
    @NotBlank(message = "appSecret不能为空")
    private String appSecret;
}
