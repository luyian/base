package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录响应")
public class LoginResponse {

    /**
     * 访问令牌
     */
    @ApiModelProperty("访问令牌")
    private String token;

    /**
     * 令牌类型
     */
    @ApiModelProperty("令牌类型")
    private String tokenType = "Bearer";

    /**
     * 过期时间（毫秒）
     */
    @ApiModelProperty("过期时间（毫秒）")
    private Long expiresIn;

    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
