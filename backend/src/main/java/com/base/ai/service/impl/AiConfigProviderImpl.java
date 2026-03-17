package com.base.ai.service.impl;

import com.base.ai.entity.SysAiConfig;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.SysAiConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 大模型配置提供者：仅从 DB 读取当前生效的一条配置（sys_ai_config.is_active=1）
 *
 * @author base
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class AiConfigProviderImpl implements AiConfigProvider {

    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_RETRY = 2;
    private static final int DEFAULT_MAX_MESSAGE_LENGTH = 2000;
    private static final int DEFAULT_MAX_CONTEXT_LENGTH = 5000;

    private final SysAiConfigService sysAiConfigService;

    @Override
    public Boolean getEnabled() {
        SysAiConfig c = getActive();
        return c != null && Integer.valueOf(1).equals(c.getStatus());
    }

    @Override
    public String getBaseUrl() {
        SysAiConfig c = getActive();
        return c != null ? c.getBaseUrl() : null;
    }

    @Override
    public String getApiKey() {
        SysAiConfig c = getActive();
        return c != null ? c.getApiKey() : null;
    }

    @Override
    public String getModel() {
        SysAiConfig c = getActive();
        return c != null && StringUtils.hasText(c.getModel()) ? c.getModel() : "qwen-plus";
    }

    @Override
    public Integer getTimeout() {
        SysAiConfig c = getActive();
        return c != null && c.getTimeout() != null ? c.getTimeout() : DEFAULT_TIMEOUT;
    }

    @Override
    public Integer getRetry() {
        SysAiConfig c = getActive();
        return c != null && c.getRetry() != null ? c.getRetry() : DEFAULT_RETRY;
    }

    @Override
    public Integer getMaxMessageLength() {
        SysAiConfig c = getActive();
        return c != null && c.getMaxMessageLength() != null ? c.getMaxMessageLength() : DEFAULT_MAX_MESSAGE_LENGTH;
    }

    @Override
    public Integer getMaxContextLength() {
        SysAiConfig c = getActive();
        return c != null && c.getMaxContextLength() != null ? c.getMaxContextLength() : DEFAULT_MAX_CONTEXT_LENGTH;
    }

    @Override
    public boolean isConfigured() {
        SysAiConfig c = getActive();
        return c != null
                && Integer.valueOf(1).equals(c.getStatus())
                && StringUtils.hasText(c.getBaseUrl())
                && StringUtils.hasText(c.getApiKey());
    }

    private SysAiConfig getActive() {
        return sysAiConfigService.getActiveConfig();
    }
}
