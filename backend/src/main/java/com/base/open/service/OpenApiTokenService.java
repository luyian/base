package com.base.open.service;

/**
 * 开放接口 Token 服务
 *
 * @author base
 */
public interface OpenApiTokenService {

    /**
     * 签发 Token
     *
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return accessToken
     */
    String createToken(String appId, String appSecret);

    /**
     * 校验 Token
     *
     * @param token 访问令牌
     * @return appId，无效则返回 null
     */
    String validateToken(String token);

    /**
     * 吊销 Token
     *
     * @param appId 应用ID
     */
    void revokeToken(String appId);
}
