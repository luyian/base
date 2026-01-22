package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log_operation")
public class OperationLog extends BaseEntity {

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
    private Integer operationType;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 执行时长（毫秒）
     */
    private Integer executeTime;

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
}
