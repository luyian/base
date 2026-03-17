package com.base.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.ai.dto.SysAiConfigResponse;
import com.base.ai.dto.SysAiConfigSaveRequest;

import java.util.List;

/**
 * 大模型配置服务（多条配置，选一条生效）
 *
 * @author base
 * @since 2026-03-17
 */
public interface SysAiConfigService {

    Page<SysAiConfigResponse> page(int current, int size, String configName, Integer status);

    List<SysAiConfigResponse> listAll();

    SysAiConfigResponse getById(Long id);

    /** 获取详情（含 API Key 原文，仅用于编辑表单） */
    SysAiConfigResponse getByIdForEdit(Long id);

    Long save(SysAiConfigSaveRequest request);

    void update(SysAiConfigSaveRequest request);

    void deleteById(Long id);

    /** 设为当前生效配置（同时将其他配置的 is_active 置为 0） */
    void setActive(Long id);

    /** 获取当前生效的配置（供 AiConfigProvider 使用） */
    com.base.ai.entity.SysAiConfig getActiveConfig();
}
