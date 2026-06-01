package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 知识库保存请求
 */
@Data
public class KnowledgeBaseSaveRequest {

    /**
     * 知识库ID（编辑时传入）
     */
    private Long id;

    /**
     * 知识库名称
     */
    @NotBlank(message = "知识库名称不能为空")
    private String name;

    /**
     * 知识库描述
     */
    private String description;
}
