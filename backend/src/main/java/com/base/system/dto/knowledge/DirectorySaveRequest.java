package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 目录保存请求
 */
@Data
public class DirectorySaveRequest {

    /**
     * 目录ID（编辑时传入）
     */
    private Long id;

    /**
     * 所属知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    /**
     * 父目录ID（0或null表示根目录）
     */
    private Long parentId;

    /**
     * 目录名称
     */
    @NotBlank(message = "目录名称不能为空")
    private String name;
}
