package com.base.system.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.system.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出配置实体类
 *
 * @author base
 * @since 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_export_config")
public class ExportConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 配置编码（唯一标识）
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
     * 数据源类型（SERVICE-服务方法, SQL-自定义SQL）
     */
    private String dataSourceType;

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
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
