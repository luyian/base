package com.base.stock.http;

import com.base.stock.config.StockSyncConfig;
import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 并发HTTP执行器工厂
 * 管理不同服务商的执行器实例
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConcurrentHttpExecutorFactory {

    private final TokenManagerService tokenManagerService;
    private final StockSyncConfig stockSyncConfig;

    /**
     * 执行器缓存（按服务商）
     */
    private final Map<String, ConcurrentHttpExecutor> executorCache = new ConcurrentHashMap<>();

    /**
     * 获取指定服务商的执行器
     * 如果不存在则创建新实例
     *
     * @param provider 服务商标识
     * @return 执行器实例
     */
    public ConcurrentHttpExecutor getExecutor(String provider) {
        return executorCache.computeIfAbsent(provider, this::createExecutor);
    }

    /**
     * 创建执行器实例
     *
     * @param provider 服务商标识
     * @return 执行器实例
     */
    private ConcurrentHttpExecutor createExecutor(String provider) {
        log.info("创建并发HTTP执行器，provider: {}", provider);
        return new ConcurrentHttpExecutor(
                provider,
                tokenManagerService,
                stockSyncConfig.getTokensPerThread(),
                stockSyncConfig.getMaxThreads(),
                stockSyncConfig.getTokenAcquireTimeout()
        );
    }

    /**
     * 刷新指定服务商的Token池
     *
     * @param provider 服务商标识
     */
    public void refreshTokenPool(String provider) {
        ConcurrentHttpExecutor executor = executorCache.get(provider);
        if (executor != null) {
            executor.refreshTokenPool();
        }
    }

    /**
     * 刷新所有执行器的Token池
     */
    public void refreshAllTokenPools() {
        executorCache.values().forEach(ConcurrentHttpExecutor::refreshTokenPool);
    }

    /**
     * 关闭所有执行器
     */
    @PreDestroy
    public void shutdown() {
        log.info("关闭所有并发HTTP执行器");
        executorCache.values().forEach(ConcurrentHttpExecutor::shutdown);
        executorCache.clear();
    }
}
