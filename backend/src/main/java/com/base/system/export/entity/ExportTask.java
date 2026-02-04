package com.base.system.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.system.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 导出任务实体类
 *
 * @author base
 * @since 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_export_task")
public class ExportTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 查询参数（JSON格式）
     */
    private String queryParams;

    /**
     * 状态（0-待处理，1-处理中，2-已完成，3-失败，4-已取消）
     */
    private Integer status;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 已处理记录数
     */
    private Integer processedCount;

    /**
     * 进度百分比（0-100）
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
     * 文件大小（字节）
     */
    private Long fileSize;

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
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 文件过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 下载次数
     */
    private Integer downloadCount;
}
