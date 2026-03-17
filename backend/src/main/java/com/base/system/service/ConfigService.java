package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.config.ConfigQueryRequest;
import com.base.system.dto.config.ConfigResponse;
import com.base.system.dto.config.ConfigSaveRequest;
import com.base.system.entity.Config;

import java.util.List;

/**
 * 全局变量服务接口
 */
public interface ConfigService {

    /**
     * 分页查询全局变量列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<ConfigResponse> pageConfigs(ConfigQueryRequest request);

    /**
     * 根据ID获取全局变量详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    ConfigResponse getConfigById(Long id);

    /**
     * 新增全局变量
     *
     * @param request 保存请求参数
     */
    void addConfig(ConfigSaveRequest request);

    /**
     * 编辑全局变量
     *
     * @param request 保存请求参数
     */
    void updateConfig(ConfigSaveRequest request);

    /**
     * 删除全局变量
     *
     * @param id 配置ID
     */
    void deleteConfig(Long id);

    /**
     * 批量删除全局变量
     *
     * @param ids 配置ID列表
     */
    void batchDeleteConfigs(List<Long> ids);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValueByKey(String configKey);

    /**
     * 刷新全局变量缓存
     */
    void refreshCache();

    /**
     * 根据配置键获取配置实体
     *
     * @param configKey 配置键
     * @return 配置实体，不存在返回 null
     */
    Config getConfigByKey(String configKey);

    /**
     * 按配置键保存或更新配置值（用于大模型等按 key 覆盖）
     *
     * @param configKey   配置键
     * @param configValue 配置值（可为长文本）
     * @param configName  配置名称
     * @param type        参数类型 string/number/boolean
     */
    void saveOrUpdateByKey(String configKey, String configValue, String configName, String type);
}
