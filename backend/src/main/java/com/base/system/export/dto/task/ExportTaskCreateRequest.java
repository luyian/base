package com.base.system.export.dto.task;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 导出任务创建请求
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportTaskCreateRequest {

    /**
     * 导出配置编码
     */
    @NotBlank(message = "配置编码不能为空")
    private String configCode;

    /**
     * 查询参数（JSON格式）
     */
    private String queryParams;
}
