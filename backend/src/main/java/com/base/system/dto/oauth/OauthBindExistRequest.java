package com.base.system.dto.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 绑定已有账号请求 DTO
 */
@Data
@ApiModel("绑定已有账号请求")
public class OauthBindExistRequest {

    /**
     * 临时绑定凭证
     */
    @NotBlank(message = "绑定凭证不能为空")
    @ApiModelProperty("临时绑定凭证")
    private String oauthToken;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;
}
