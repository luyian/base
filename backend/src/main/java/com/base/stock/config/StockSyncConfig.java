package com.base.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 股票同步配置类
 *
 * @author base
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "stock.sync")
public class StockSyncConfig {

    /**
     * 每个线程需要的Token数量
     */
    private int tokensPerThread = 6;

    /**
     * 最大线程数
     */
    private int maxThreads = 10;

    /**
     * 获取Token的超时时间（毫秒）
     */
    private long tokenAcquireTimeout = 30000;

    /**
     * 失败最大重试次数
     */
    private int maxRetryCount = 3;

    /**
     * 批量同步阈值（超过此数量使用批量接口）
     */
    private int batchThreshold = 100;

    /**
     * 批量请求大小
     */
    private int batchSize = 100;
}
