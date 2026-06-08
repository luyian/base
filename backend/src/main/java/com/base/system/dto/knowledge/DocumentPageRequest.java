package com.base.system.dto.knowledge;

import lombok.Data;

/**
 * 全部文档分页查询请求（跨知识库）
 */
@Data
public class DocumentPageRequest {

    /**
     * 当前页码
     */
    private Long pageNum = 1L;

    /**
     * 每页条数
     */
    private Long pageSize = 12L;

    /**
     * 标题关键字（可选，模糊匹配）
     */
    private String keyword;

    /**
     * 标签名称（可选，精确匹配）
     */
    private String tag;
}
