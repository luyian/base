package com.base.system.export.dto.task;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出任务查询请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExportTaskQueryRequest extends BasePageRequest {

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
}
