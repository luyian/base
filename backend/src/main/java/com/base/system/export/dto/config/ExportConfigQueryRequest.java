package com.base.system.export.dto.config;

import lombok.Data;

/**
 * 导出配置查询请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportConfigQueryRequest {

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

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
