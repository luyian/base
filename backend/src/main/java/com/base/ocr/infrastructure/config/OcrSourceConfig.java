package com.base.ocr.infrastructure.config;

import com.base.system.service.ConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OCR 数据源配置
 * <p>
 * source/fallback 等结构化配置从 yml 读取，
 * 各平台 API Key 从 sys_config 表读取（支持运行时动态修改）。
 * </p>
 *
 * @author base
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "ocr")
public class OcrSourceConfig {

    /** 主数据源：tencent / baidu / aliyun */
    private String source = "tencent";

    /** 是否启用降级（主数据源失败时切换到备用） */
    private boolean fallbackEnabled = false;

    /** 降级数据源 */
    private String fallbackSource = "baidu";

    private ConfigService configService;

    @javax.annotation.Resource
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    // ========== 腾讯云 OCR 配置 ==========

    public String getTencentSecretId() {
        return getKeyFromConfig("ocr.tencent.secret-id");
    }

    public String getTencentSecretKey() {
        return getKeyFromConfig("ocr.tencent.secret-key");
    }

    // ========== 百度云 OCR 配置 ==========

    public String getBaiduApiKey() {
        return getKeyFromConfig("ocr.baidu.api-key");
    }

    public String getBaiduSecretKey() {
        return getKeyFromConfig("ocr.baidu.secret-key");
    }

    // ========== 阿里云 OCR 配置 ==========

    public String getAliyunAccessKeyId() {
        return getKeyFromConfig("ocr.aliyun.access-key-id");
    }

    public String getAliyunAccessKeySecret() {
        return getKeyFromConfig("ocr.aliyun.access-key-secret");
    }

    private String getKeyFromConfig(String configKey) {
        if (configService == null) {
            return "";
        }
        String value = configService.getConfigValueByKey(configKey);
        return value != null ? value : "";
    }
}
