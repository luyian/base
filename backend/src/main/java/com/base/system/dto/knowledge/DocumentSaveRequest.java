package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文档保存请求
 */
@Data
public class DocumentSaveRequest {

    /**
     * 文档ID（编辑时传入）
     */
    private Long id;

    /**
     * 所属知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    /**
     * 文档标题
     */
    @NotBlank(message = "文档标题不能为空")
    private String title;

    /**
     * MD文档内容
     */
    private String content;

    /**
     * 所属目录ID
     */
    private Long directoryId;

    /**
     * 标签列表
     */
    private List<String> tags;
}
