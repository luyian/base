package com.base.system.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档响应
 */
@Data
public class DocumentResponse {

    /**
     * 文档ID
     */
    private Long id;

    /**
     * 所属知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 所属知识库名称
     */
    private String knowledgeBaseName;

    /**
     * 文档标题
     */
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
     * 目录路径（如"前端/Vue"）
     */
    private String directoryPath;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
