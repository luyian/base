package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论保存请求
 */
@Data
public class CommentSaveRequest {

    /**
     * 所属知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    /**
     * 所属文档ID
     */
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    /**
     * 父评论ID（顶级评论传 0 或不传）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
