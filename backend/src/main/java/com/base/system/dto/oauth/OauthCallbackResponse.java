package com.base.system.dto.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * OAuth 回调响应 DTO
 */
@Data
@ApiModel("OAuth回调响应")
public class OauthCallbackResponse {

    /**
     * JWT Token（已绑定时返回）
     */
    @ApiModelProperty("JWT Token")
    private String token;

    /**
     * Token 过期时间（毫秒）
     */
    @ApiModelProperty("Token过期时间（毫秒）")
    private Long expiresIn;

    /**
     * 临时绑定凭证（未绑定时返回）
     */
    @ApiModelProperty("临时绑定凭证")
    private String oauthToken;

    /**
     * 是否需要绑定
     */
    @ApiModelProperty("是否需要绑定")
    private Boolean needBind;

    /**
     * 第三方平台用户名
     */
    @ApiModelProperty("第三方平台用户名")
    private String oauthName;

    /**
     * 第三方平台头像
     */
    @ApiModelProperty("第三方平台头像")
    private String oauthAvatar;
}
