package com.base.system.export.dto.config;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出配置查询请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExportConfigQueryRequest extends BasePageRequest {

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 状态
     */
    private Integer status;
}
