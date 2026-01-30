package com.base.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * iTick API 配置
 *
 * @author base
 */
@Data
@Component
@ConfigurationProperties(prefix = "stock.itick")
public class ITickConfig {

    /**
     * API 基础地址
     */
    private String baseUrl = "https://api.itick.org";

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 30000;

    /**
     * 重试次数
     */
    private Integer retry = 3;
}
