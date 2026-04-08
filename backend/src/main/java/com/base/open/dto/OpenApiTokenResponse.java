package com.base.open.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 开放接口 Token 响应
 *
 * @author base
 */
@Data
@Builder
public class OpenApiTokenResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 令牌类型
     */
    private String tokenType;
}
