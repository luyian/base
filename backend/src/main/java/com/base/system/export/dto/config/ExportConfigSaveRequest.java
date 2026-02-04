package com.base.system.export.dto.config;

import com.base.system.export.dto.field.ExportFieldSaveRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 导出配置保存请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportConfigSaveRequest {

    /**
     * 主键ID（编辑时必填）
     */
    private Long id;

    /**
     * 配置编码（唯一标识）
     */
    @NotBlank(message = "配置编码不能为空")
    @Size(max = 50, message = "配置编码长度不能超过50个字符")
    private String configCode;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100个字符")
    private String configName;

    /**
     * 配置描述
     */
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    private String description;

    /**
     * 数据源类型（SERVICE-服务方法, SQL-自定义SQL）
     */
    @NotBlank(message = "数据源类型不能为空")
    private String dataSourceType;

    /**
     * 数据源Bean名称
     */
    @Size(max = 100, message = "数据源Bean名称长度不能超过100个字符")
    private String dataSourceBean;

    /**
     * 数据源方法名
     */
    @Size(max = 100, message = "数据源方法名长度不能超过100个字符")
    private String dataSourceMethod;

    /**
     * 自定义SQL
     */
    private String dataSourceSql;

    /**
     * 查询参数类全路径
     */
    @Size(max = 200, message = "查询参数类全路径长度不能超过200个字符")
    private String queryParamClass;

    /**
     * 分批查询大小
     */
    private Integer batchSize = 5000;

    /**
     * 最大导出数量
     */
    private Integer maxExportCount = 1000000;

    /**
     * 文件名模式
     */
    @Size(max = 100, message = "文件名模式长度不能超过100个字符")
    private String fileNamePattern;

    /**
     * 是否启用多Sheet
     */
    private Boolean enableMultiSheet = false;

    /**
     * 单Sheet最大行数
     */
    private Integer sheetMaxRows = 100000;

    /**
     * 权限编码
     */
    @Size(max = 100, message = "权限编码长度不能超过100个字符")
    private String permissionCode;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status = 1;

    /**
     * 排序
     */
    private Integer sort = 0;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 字段配置列表
     */
    private List<ExportFieldSaveRequest> fields;
}
