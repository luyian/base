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
     * 配置类型（1-系统内置 2-用户自定义）
     */
    private Integer configType;

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
