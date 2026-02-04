package com.base.system.export.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.export.dto.config.ExportConfigQueryRequest;
import com.base.system.export.dto.config.ExportConfigResponse;
import com.base.system.export.dto.config.ExportConfigSaveRequest;
import com.base.system.export.dto.field.ExportFieldResponse;
import com.base.system.export.dto.field.ExportFieldSaveRequest;
import com.base.system.export.entity.ExportConfig;

import java.util.List;

/**
 * 导出配置服务接口
 *
 * @author base
 * @since 2026-02-04
 */
public interface ExportConfigService {

    /**
     * 分页查询导出配置
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Page<ExportConfigResponse> pageConfigs(ExportConfigQueryRequest request);

    /**
     * 根据ID获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    ExportConfigResponse getConfigById(Long id);

    /**
     * 根据编码获取配置
     *
     * @param configCode 配置编码
     * @return 配置实体
     */
    ExportConfig getConfigByCode(String configCode);

    /**
     * 新增配置
     *
     * @param request 保存请求
     * @return 配置ID
     */
    Long addConfig(ExportConfigSaveRequest request);

    /**
     * 更新配置
     *
     * @param request 保存请求
     */
    void updateConfig(ExportConfigSaveRequest request);

    /**
     * 删除配置
     *
     * @param id 配置ID
     */
    void deleteConfig(Long id);

    /**
     * 获取字段配置列表
     *
     * @param configId 配置ID
     * @return 字段配置列表
     */
    List<ExportFieldResponse> getFieldsByConfigId(Long configId);

    /**
     * 保存字段配置（批量）
     *
     * @param configId 配置ID
     * @param fields   字段配置列表
     */
    void saveFields(Long configId, List<ExportFieldSaveRequest> fields);

    /**
     * 获取所有启用的配置列表
     *
     * @return 配置列表
     */
    List<ExportConfigResponse> listEnabledConfigs();
}
