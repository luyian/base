package com.base.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 报价数据源配置
 *
 * @author base
 */
@Data
@Component
@ConfigurationProperties(prefix = "stock.quote")
public class QuoteSourceConfig {

    /**
     * 主数据源：eastmoney / itick
     */
    private String source = "eastmoney";

    /**
     * 是否启用降级（主数据源失败时切换到备用）
     */
    private boolean fallbackEnabled = true;

    /**
     * 备用数据源
     */
    private String fallbackSource = "itick";
}