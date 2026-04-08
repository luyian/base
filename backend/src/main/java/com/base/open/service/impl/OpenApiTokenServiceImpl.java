package com.base.open.service.impl;

import com.base.common.exception.BusinessException;
import com.base.open.config.OpenApiConfig;
import com.base.open.service.OpenApiTokenService;
import com.base.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 开放接口 Token 服务实现
 *
 * @author base
 */
@Slf4j
@Service
public class OpenApiTokenServiceImpl implements OpenApiTokenService {

    /**
     * Token -> appId 的 Redis key 前缀
     */
    private static final String TOKEN_PREFIX = "open:token:";

    /**
     * appId -> Token 的 Redis key 前缀
     */
    private static final String APP_TOKEN_PREFIX = "open:app:";

    /**
     * appId -> Token 的 Redis key 后缀
     */
    private static final String APP_TOKEN_SUFFIX = ":token";

    /**
     * Token 提前刷新阈值（秒），剩余 TTL 低于此值时重新签发
     */
    private static final long REFRESH_THRESHOLD = 300L;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OpenApiConfig openApiConfig;

    @Override
    public String createToken(String appId, String appSecret) {
        // 查找应用配置
        OpenApiConfig.AppInfo appInfo = openApiConfig.getAppByAppId(appId);
        if (appInfo == null) {
            throw new BusinessException("应用不存在");
        }

        // 使用时间安全比较防止时序攻击
        byte[] providedBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        byte[] expectedBytes = appInfo.getAppSecret().getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(providedBytes, expectedBytes)) {
            throw new BusinessException("应用密钥错误");
        }

        // 检查是否已有有效 Token
        String appTokenKey = APP_TOKEN_PREFIX + appId + APP_TOKEN_SUFFIX;
        String existingToken = redisUtil.get(appTokenKey, String.class);
        if (existingToken != null) {
            Long ttl = redisUtil.getExpire(appTokenKey);
            if (ttl != null && ttl > REFRESH_THRESHOLD) {
                log.info("应用 {} 已有有效Token，直接返回", appId);
                return existingToken;
            }
        }

        // 生成新 Token
        String newToken = UUID.randomUUID().toString().replace("-", "");
        long tokenExpire = openApiConfig.getTokenExpire();

        // 双向写入 Redis
        String tokenKey = TOKEN_PREFIX + newToken;
        redisUtil.set(tokenKey, appId, tokenExpire);
        redisUtil.set(appTokenKey, newToken, tokenExpire);

        // 删除旧 Token
        if (existingToken != null) {
            redisUtil.delete(TOKEN_PREFIX + existingToken);
        }

        log.info("为应用 {} 签发新Token，有效期 {} 秒", appId, tokenExpire);
        return newToken;
    }

    @Override
    public String validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String tokenKey = TOKEN_PREFIX + token;
        return redisUtil.get(tokenKey, String.class);
    }

    @Override
    public void revokeToken(String appId) {
        String appTokenKey = APP_TOKEN_PREFIX + appId + APP_TOKEN_SUFFIX;
        String existingToken = redisUtil.get(appTokenKey, String.class);
        if (existingToken != null) {
            redisUtil.delete(TOKEN_PREFIX + existingToken);
            redisUtil.delete(appTokenKey);
            log.info("已吊销应用 {} 的Token", appId);
        }
    }
}
