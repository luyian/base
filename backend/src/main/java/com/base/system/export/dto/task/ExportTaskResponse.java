package com.base.system.export.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导出任务响应
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportTaskResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 导出配置ID
     */
    private Long configId;

    /**
     * 导出配置编码
     */
    private String configCode;

    /**
     * 导出配置名称
     */
    private String configName;

    /**
     * 查询参数
     */
    private String queryParams;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 已处理记录数
     */
    private Integer processedCount;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件大小描述
     */
    private String fileSizeDesc;

    /**
     * 文件下载URL
     */
    private String fileUrl;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 文件过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;
}
