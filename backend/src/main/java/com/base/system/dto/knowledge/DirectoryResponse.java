package com.base.system.dto.knowledge;

import lombok.Data;

import java.util.List;

/**
 * 目录响应（树结构）
 */
@Data
public class DirectoryResponse {

    /**
     * 目录ID
     */
    private Long id;

    /**
     * 父目录ID
     */
    private Long parentId;

    /**
     * 目录名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子目录
     */
    private List<DirectoryResponse> children;
}
