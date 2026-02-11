package com.base.common.feishu.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.config.FeishuConfig;
import com.base.common.feishu.service.FeishuTokenService;
import com.base.common.util.HttpClientUtil;
import com.base.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书 Token 管理服务实现
 * 使用 Redis 缓存 tenant_access_token，提前 5 分钟刷新
 *
 * @author base
 * @since 2026-02-11
 */
@Slf4j
@Service
public class FeishuTokenServiceImpl implements FeishuTokenService {

    /**
     * Redis 缓存 Key
     */
    private static final String TOKEN_CACHE_KEY = "feishu:tenant_access_token";

    /**
     * Token 有效期（秒），飞书默认 2 小时
     */
    private static final long TOKEN_EXPIRE = 7200;

    /**
     * 提前刷新时间（秒），提前 5 分钟
     */
    private static final long REFRESH_AHEAD = 300;

    @Autowired
    private FeishuConfig feishuConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getTenantAccessToken() {
        // 从 Redis 获取缓存的 Token
        String token = redisUtil.get(TOKEN_CACHE_KEY, String.class);
        if (token != null) {
            // 检查是否需要提前刷新
            Long ttl = redisUtil.getExpire(TOKEN_CACHE_KEY);
            if (ttl != null && ttl > REFRESH_AHEAD) {
                return token;
            }
        }
        // 缓存不存在或即将过期，刷新 Token
        return refreshTenantAccessToken();
    }

    @Override
    public synchronized String refreshTenantAccessToken() {
        // 双重检查，防止并发重复刷新
        String token = redisUtil.get(TOKEN_CACHE_KEY, String.class);
        if (token != null) {
            Long ttl = redisUtil.getExpire(TOKEN_CACHE_KEY);
            if (ttl != null && ttl > REFRESH_AHEAD) {
                return token;
            }
        }

        log.info("开始刷新飞书 tenant_access_token");
        String url = feishuConfig.getBaseUrl() + "/auth/v3/tenant_access_token/internal";
        Map<String, Object> body = new HashMap<>(4);
        body.put("app_id", feishuConfig.getAppId());
        body.put("app_secret", feishuConfig.getAppSecret());

        try {
            String response = HttpClientUtil.postWithRetry(url, body, null, feishuConfig.getRetry());
            JSONObject jsonObject = JSONObject.parseObject(response);
            int code = jsonObject.getIntValue("code");
            if (code != 0) {
                String msg = jsonObject.getString("msg");
                log.error("获取飞书 tenant_access_token 失败，code: {}，msg: {}", code, msg);
                throw new BusinessException("获取飞书 Token 失败: " + msg);
            }

            String newToken = jsonObject.getString("tenant_access_token");
            int expire = jsonObject.getIntValue("expire");
            if (expire <= 0) {
                expire = (int) TOKEN_EXPIRE;
            }

            // 缓存到 Redis
            redisUtil.set(TOKEN_CACHE_KEY, newToken, expire);
            log.info("飞书 tenant_access_token 刷新成功，有效期: {} 秒", expire);
            return newToken;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新飞书 tenant_access_token 异常", e);
            throw new BusinessException("刷新飞书 Token 异常: " + e.getMessage());
        }
    }
}
