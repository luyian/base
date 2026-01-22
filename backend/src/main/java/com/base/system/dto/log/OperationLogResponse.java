package com.base.system.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志响应结果
 */
@Data
public class OperationLogResponse {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private Integer operationType;

    /**
     * 操作类型名称
     */
    private String operationTypeName;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 操作状态
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作地点
     */
    private String operationLocation;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人用户名
     */
    private String operatorName;

    /**
     * 操作时间（毫秒）
     */
    private Long operationTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
