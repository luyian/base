package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.config.ConfigQueryRequest;
import com.base.system.dto.config.ConfigResponse;
import com.base.system.dto.config.ConfigSaveRequest;

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
}
