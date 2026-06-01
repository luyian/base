package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 标签保存请求
 */
@Data
public class TagSaveRequest {

    /**
     * 标签ID（编辑时传入）
     */
    private Long id;

    /**
     * 所属知识库ID
     */
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签颜色
     */
    private String color;
}
