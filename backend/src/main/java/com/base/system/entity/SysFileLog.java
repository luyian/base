package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件操作日志实体类
 */
@Data
@TableName("sys_file_log")
public class SysFileLog {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 操作类型（1-上传 2-下载 3-删除 4-预览）
     */
    private Integer operationType;

    /**
     * 操作类型名称
     */
    @TableField(exist = false)
    private String operationTypeName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 请求UA
     */
    private String userAgent;

    /**
     * 操作状态（0-失败 1-成功）
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时长（毫秒）
     */
    private Integer executeTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}