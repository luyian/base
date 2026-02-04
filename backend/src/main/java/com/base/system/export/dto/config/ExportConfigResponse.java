package com.base.system.export.dto.config;

import com.base.system.export.dto.field.ExportFieldResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导出配置响应
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportConfigResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 数据源类型描述
     */
    private String dataSourceTypeDesc;

    /**
     * 数据源Bean名称
     */
    private String dataSourceBean;

    /**
     * 数据源方法名
     */
    private String dataSourceMethod;

    /**
     * 自定义SQL
     */
    private String dataSourceSql;

    /**
     * 查询参数类全路径
     */
    private String queryParamClass;

    /**
     * 分批查询大小
     */
    private Integer batchSize;

    /**
     * 最大导出数量
     */
    private Integer maxExportCount;

    /**
     * 文件名模式
     */
    private String fileNamePattern;

    /**
     * 是否启用多Sheet
     */
    private Boolean enableMultiSheet;

    /**
     * 单Sheet最大行数
     */
    private Integer sheetMaxRows;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 字段配置列表
     */
    private List<ExportFieldResponse> fields;
}
