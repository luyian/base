package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信小程序登录请求 DTO
 */
@Data
@ApiModel("微信小程序登录请求")
public class WxLoginRequest {

    /**
     * 微信登录code
     */
    @ApiModelProperty(value = "微信登录code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 用户信息（可选）
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    /**
     * 头像URL（可选）
     */
    @ApiModelProperty(value = "头像URL")
    private String avatarUrl;

    /**
     * 性别（0未知 1男 2女）
     */
    @ApiModelProperty(value = "性别")
    private Integer gender;
}