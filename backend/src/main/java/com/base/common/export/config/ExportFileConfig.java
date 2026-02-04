package com.base.common.export.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 导出文件配置
 *
 * @author base
 * @since 2026-02-04
 */
@Component
@ConfigurationProperties(prefix = "export.file")
public class ExportFileConfig {

    /**
     * 导出文件存储路径
     */
    private String path = "D:/upload/export/";

    /**
     * 文件过期天数
     */
    private int expireDays = 7;

    /**
     * 文件访问前缀
     */
    private String urlPrefix = "/export/download/";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}
