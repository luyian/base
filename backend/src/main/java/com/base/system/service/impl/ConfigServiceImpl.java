package com.base.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.ResultCode;
import com.base.system.dto.config.ConfigQueryRequest;
import com.base.system.dto.config.ConfigResponse;
import com.base.system.dto.config.ConfigSaveRequest;
import com.base.system.entity.Config;
import com.base.system.exception.BusinessException;
import com.base.system.mapper.ConfigMapper;
import com.base.system.service.ConfigService;
import com.base.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 全局变量服务实现类
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String CONFIG_CACHE_PREFIX = "config:key:";

    @Override
    public Page<ConfigResponse> pageConfigs(ConfigQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getConfigKey()), Config::getConfigKey, request.getConfigKey())
                .like(StringUtils.hasText(request.getConfigName()), Config::getConfigName, request.getConfigName())
                .eq(StringUtils.hasText(request.getType()), Config::getType, request.getType())
                .eq(request.getStatus() != null, Config::getStatus, request.getStatus())
                .orderByAsc(Config::getType)
                .orderByDesc(Config::getCreateTime);

        // 分页查询
        Page<Config> page = request.buildPage();
        configMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<ConfigResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ConfigResponse> responseList = BeanUtil.copyToList(page.getRecords(), ConfigResponse.class);
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public ConfigResponse getConfigById(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return BeanUtil.copyProperties(config, ConfigResponse.class);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(ConfigSaveRequest request) {
        // 检查配置键是否已存在
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, request.getConfigKey());
        Long count = configMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "配置键已存在");
        }

        // 保存配置
        Config config = BeanUtil.copyProperties(request, Config.class);
        configMapper.insert(config);

        // 删除缓存
        redisUtil.del(CONFIG_CACHE_PREFIX + request.getConfigKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigSaveRequest request) {
        // 检查配置是否存在
        Config config = configMapper.selectById(request.getId());
        if (config == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查配置键是否已被其他配置使用
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, request.getConfigKey())
                .ne(Config::getId, request.getId());
        Long count = configMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "配置键已存在");
        }

        // 更新配置
        BeanUtil.copyProperties(request, config, "id");
        configMapper.updateById(config);

        // 删除缓存
        redisUtil.del(CONFIG_CACHE_PREFIX + request.getConfigKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        // 检查配置是否存在
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 系统内置配置不允许删除（通过创建人判断）
        if ("system".equals(config.getCreateBy())) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "系统内置配置不允许删除");
        }

        // 删除配置
        configMapper.deleteById(id);

        // 删除缓存
        redisUtil.del(CONFIG_CACHE_PREFIX + config.getConfigKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteConfigs(List<Long> ids) {
        // 检查是否包含系统内置配置（通过创建人判断）
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Config::getId, ids)
                .eq(Config::getCreateBy, "system");
        Long count = configMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "系统内置配置不允许删除");
        }

        // 查询要删除的配置键
        List<Config> configs = configMapper.selectBatchIds(ids);

        // 批量删除
        configMapper.deleteBatchIds(ids);

        // 删除缓存
        for (Config config : configs) {
            redisUtil.del(CONFIG_CACHE_PREFIX + config.getConfigKey());
        }
    }

    @Override
    public String getConfigValueByKey(String configKey) {
        // 先从缓存获取
        String cacheKey = CONFIG_CACHE_PREFIX + configKey;
        Object cacheValue = redisUtil.get(cacheKey);
        if (cacheValue != null) {
            return cacheValue.toString();
        }

        // 从数据库查询
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey)
                .eq(Config::getStatus, 1);
        Config config = configMapper.selectOne(wrapper);

        if (config == null) {
            return null;
        }

        // 存入缓存（24小时）
        redisUtil.set(cacheKey, config.getConfigValue(), 86400);

        return config.getConfigValue();
    }

    @Override
    public void refreshCache() {
        // 清空配置缓存
        Set<String> keys = redisUtil.keys(CONFIG_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisUtil.del(keys.toArray(new String[0]));
        }
        log.info("全局变量缓存已刷新");
    }
}
