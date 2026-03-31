package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信绑定请求 DTO
 */
@Data
@ApiModel("微信绑定请求")
public class WxBindRequest {

    @ApiModelProperty(value = "微信登录code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @ApiModelProperty(value = "用户名（账号密码登录时必填）")
    private String username;

    @ApiModelProperty(value = "密码（账号密码登录时必填）")
    private String password;
}
