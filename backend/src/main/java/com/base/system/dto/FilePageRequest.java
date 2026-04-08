package com.base.system.dto;

import lombok.Data;

/**
 * 文件分页查询请求
 *
 * @author base
 */
@Data
public class FilePageRequest {

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
     * 文件分组
     */
    private String fileGroup;
}
