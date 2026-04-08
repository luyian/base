package com.base.system.dto;

import lombok.Data;

/**
 * 文件操作日志分页查询请求
 *
 * @author base
 */
@Data
public class FileLogPageRequest {

    /**
     * 页码
     */
    private Long pageNum = 1L;

    /**
     * 每页条数
     */
    private Long pageSize = 10L;

    /**
     * 文件名（模糊查询）
     */
    private String fileName;

    /**
     * 操作类型（1-上传 2-下载 3-删除 4-预览）
     */
    private Integer operationType;

    /**
     * 操作人（模糊查询）
     */
    private String operatorName;

    /**
     * 状态（0-失败 1-成功）
     */
    private Integer status;
}
