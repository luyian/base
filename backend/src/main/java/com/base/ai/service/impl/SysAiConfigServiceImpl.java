package com.base.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.ai.config.AiChatModelHolder;
import com.base.ai.dto.SysAiConfigResponse;
import com.base.ai.dto.SysAiConfigSaveRequest;
import com.base.ai.entity.SysAiConfig;
import com.base.ai.mapper.SysAiConfigMapper;
import com.base.ai.service.SysAiConfigService;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 大模型配置服务实现
 *
 * @author base
 * @since 2026-03-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysAiConfigServiceImpl implements SysAiConfigService {

    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_RETRY = 2;
    private static final int DEFAULT_MAX_MESSAGE_LENGTH = 2000;
    private static final int DEFAULT_MAX_CONTEXT_LENGTH = 5000;

    /** Redis 中“当前生效配置”的 key，配置变更时删除以保持一致性 */
    private static final String REDIS_KEY_AI_CONFIG_ACTIVE = "ai:config:active";

    private final SysAiConfigMapper sysAiConfigMapper;
    private final AiChatModelHolder chatModelHolder;
    private final RedisUtil redisUtil;

    @Override
    public Page<SysAiConfigResponse> page(int current, int size, String configName, Integer status) {
        Page<SysAiConfig> page = new Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysAiConfig> q = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        q.like(configName != null && !configName.trim().isEmpty(), SysAiConfig::getConfigName, configName)
                .eq(status != null, SysAiConfig::getStatus, status)
                .orderByAsc(SysAiConfig::getSortOrder)
                .orderByDesc(SysAiConfig::getId);
        sysAiConfigMapper.selectPage(page, q);
        Page<SysAiConfigResponse> resp = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resp.setRecords(page.getRecords().stream().map(this::toResponse).collect(Collectors.toList()));
        return resp;
    }

    @Override
    public List<SysAiConfigResponse> listAll() {
        List<SysAiConfig> list = sysAiConfigMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysAiConfig>()
                        .eq(SysAiConfig::getStatus, 1)
                        .orderByAsc(SysAiConfig::getSortOrder));
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public SysAiConfigResponse getById(Long id) {
        SysAiConfig e = sysAiConfigMapper.selectById(id);
        if (e == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toResponse(e);
    }

    @Override
    public SysAiConfigResponse getByIdForEdit(Long id) {
        SysAiConfig e = sysAiConfigMapper.selectById(id);
        if (e == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toResponse(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysAiConfigSaveRequest request) {
        if (request.getApiKey() == null || request.getApiKey().isEmpty()) {
            throw new BusinessException("新增配置时 API Key 不能为空");
        }
        SysAiConfig e = new SysAiConfig();
        e.setConfigName(request.getConfigName());
        e.setBaseUrl(request.getBaseUrl());
        e.setApiKey(request.getApiKey());
        e.setModel(request.getModel() != null ? request.getModel() : "qwen-plus");
        e.setTimeout(request.getTimeout() != null ? request.getTimeout() : DEFAULT_TIMEOUT);
        e.setRetry(request.getRetry() != null ? request.getRetry() : DEFAULT_RETRY);
        e.setMaxMessageLength(request.getMaxMessageLength() != null ? request.getMaxMessageLength() : DEFAULT_MAX_MESSAGE_LENGTH);
        e.setMaxContextLength(request.getMaxContextLength() != null ? request.getMaxContextLength() : DEFAULT_MAX_CONTEXT_LENGTH);
        e.setIsActive(0);
        e.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        e.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        e.setRemark(request.getRemark());
        sysAiConfigMapper.insert(e);
        return e.getId();
    }

    /** 大模型配置变更后：删除 Redis 缓存 key，并清除内存中的对话模型缓存 */
    private void refreshAiConfigCache() {
        redisUtil.del(REDIS_KEY_AI_CONFIG_ACTIVE);
        chatModelHolder.clearCache();
        log.debug("大模型配置相关缓存已刷新（Redis + 对话模型）");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysAiConfigSaveRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "id 不能为空");
        }
        SysAiConfig e = sysAiConfigMapper.selectById(request.getId());
        if (e == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        e.setConfigName(request.getConfigName());
        e.setBaseUrl(request.getBaseUrl());
        if (request.getApiKey() != null && !request.getApiKey().isEmpty()) {
            e.setApiKey(request.getApiKey());
        }
        e.setModel(request.getModel());
        e.setTimeout(request.getTimeout() != null ? request.getTimeout() : DEFAULT_TIMEOUT);
        e.setRetry(request.getRetry() != null ? request.getRetry() : DEFAULT_RETRY);
        e.setMaxMessageLength(request.getMaxMessageLength() != null ? request.getMaxMessageLength() : DEFAULT_MAX_MESSAGE_LENGTH);
        e.setMaxContextLength(request.getMaxContextLength() != null ? request.getMaxContextLength() : DEFAULT_MAX_CONTEXT_LENGTH);
        e.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        e.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        e.setRemark(request.getRemark());
        sysAiConfigMapper.updateById(e);
        refreshAiConfigCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        SysAiConfig e = sysAiConfigMapper.selectById(id);
        if (e == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        sysAiConfigMapper.deleteById(id);
        refreshAiConfigCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setActive(Long id) {
        SysAiConfig target = sysAiConfigMapper.selectById(id);
        if (target == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (Integer.valueOf(0).equals(target.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "禁用的配置不能设为生效");
        }
        sysAiConfigMapper.update(null, new LambdaUpdateWrapper<SysAiConfig>().set(SysAiConfig::getIsActive, 0));
        target.setIsActive(1);
        sysAiConfigMapper.updateById(target);
        refreshAiConfigCache();
        log.info("大模型生效配置已切换为: id={}, name={}", id, target.getConfigName());
    }

    private SysAiConfigResponse toResponse(SysAiConfig e) {
        SysAiConfigResponse r = BeanUtil.copyProperties(e, SysAiConfigResponse.class);
        if (r != null && e.getApiKey() != null && !e.getApiKey().isEmpty()) {
            r.setApiKey(maskApiKey(e.getApiKey()));
        }
        return r;
    }

    @Override
    public SysAiConfig getActiveConfig() {
        // 直接查 DB：Redis 使用 Jackson 反序列化会得到 LinkedHashMap，无法还原为 SysAiConfig，故不做缓存
        return sysAiConfigMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysAiConfig>()
                        .eq(SysAiConfig::getIsActive, 1)
                        .eq(SysAiConfig::getStatus, 1)
                        .last("LIMIT 1"));
    }

    /** 列表脱敏；getById 用于编辑时需返回原文，在 Controller 层单独查 entity 填 apiKey */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
