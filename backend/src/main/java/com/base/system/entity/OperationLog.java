package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.base.system.enums.OperationTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("sys_log_operation")
public class OperationLog {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作描述
     */
    private String operation;

    /**
     * 操作类型（1-新增 2-修改 3-删除 4-查询 5-导出 6-导入 7-其他）
     */
    private OperationTypeEnum operationType;

    /**
     * 请求方法（GET/POST/PUT/DELETE等）
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求参数（别名，用于DTO映射）
     */
    @TableField(exist = false)
    private String requestParam;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 响应结果（别名，用于DTO映射）
     */
    @TableField(exist = false)
    private String responseResult;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作IP（别名，用于DTO映射）
     */
    @TableField(exist = false)
    private String operationIp;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 操作地点（别名，用于DTO映射）
     */
    @TableField(exist = false)
    private String operationLocation;

    /**
     * 执行时长（毫秒）
     */
    private Integer executeTime;

    /**
     * 操作时间（别名，用于DTO映射）
     */
    @TableField(exist = false)
    private Long operationTime;

    /**
     * 操作状态（0-失败 1-成功）
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
