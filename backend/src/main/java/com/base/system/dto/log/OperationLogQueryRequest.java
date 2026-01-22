package com.base.system.dto.log;

import lombok.Data;

/**
 * 操作日志查询请求参数
 */
@Data
public class OperationLogQueryRequest {

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private Integer operationType;

    /**
     * 操作人用户名
     */
    private String operatorName;

    /**
     * 操作状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示数量
     */
    private Long size = 10L;
}
