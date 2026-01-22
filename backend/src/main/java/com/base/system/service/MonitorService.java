package com.base.system.service;

import com.base.system.dto.monitor.CacheInfoResponse;
import com.base.system.dto.monitor.CacheKeyResponse;
import com.base.system.dto.monitor.ServerInfoResponse;

import java.util.List;

/**
 * 系统监控服务接口
 *
 * @author base
 * @date 2026-01-14
 */
public interface MonitorService {

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    ServerInfoResponse getServerInfo();

    /**
     * 获取缓存信息
     *
     * @return 缓存信息
     */
    CacheInfoResponse getCacheInfo();

    /**
     * 获取缓存键列表
     *
     * @param pattern 匹配模式
     * @return 缓存键列表
     */
    List<String> getCacheKeys(String pattern);

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存键信息
     */
    CacheKeyResponse getCacheValue(String key);

    /**
     * 删除缓存键
     *
     * @param key 缓存键
     * @return 是否成功
     */
    Boolean deleteCacheKey(String key);

    /**
     * 清空缓存
     *
     * @return 是否成功
     */
    Boolean clearCache();
}
