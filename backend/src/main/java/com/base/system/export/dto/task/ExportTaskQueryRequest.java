package com.base.system.export.dto.task;

import lombok.Data;

/**
 * 导出任务查询请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportTaskQueryRequest {

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 开始时间（起）
     */
    private String startTimeBegin;

    /**
     * 开始时间（止）
     */
    private String startTimeEnd;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
