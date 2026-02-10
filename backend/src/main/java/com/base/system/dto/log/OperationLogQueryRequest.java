package com.base.system.dto.log;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogQueryRequest extends BasePageRequest {

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
}
