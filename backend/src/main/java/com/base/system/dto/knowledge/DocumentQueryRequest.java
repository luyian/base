package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 文档查询请求
 */
@Data
public class DocumentQueryRequest {

    /**
     * 所属知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    /**
     * 所属目录ID（可选）
     */
    private Long directoryId;

    /**
     * 标签ID（可选，用于筛选）
     */
    private Long tagId;
}
