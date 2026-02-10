package com.base.system.dto.config;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全局变量查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigQueryRequest extends BasePageRequest {

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
}
