package com.base.common.feishu.service;

/**
 * 飞书 Token 管理服务接口
 *
 * @author base
 * @since 2026-02-11
 */
public interface FeishuTokenService {

    /**
     * 获取 tenant_access_token
     * 优先从 Redis 缓存获取，缓存不存在或即将过期时自动刷新
     *
     * @return tenant_access_token
     */
    String getTenantAccessToken();

    /**
     * 强制刷新 tenant_access_token
     *
     * @return 新的 tenant_access_token
     */
    String refreshTenantAccessToken();
}
