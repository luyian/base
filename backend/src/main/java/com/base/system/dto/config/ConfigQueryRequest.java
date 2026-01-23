package com.base.system.dto.config;

import lombok.Data;

/**
 * 全局变量查询请求参数
 */
@Data
public class ConfigQueryRequest {

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 参数类型（string、number、boolean、json）
     */
    private String type;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示数量
     */
    private Long size = 10L;
}
