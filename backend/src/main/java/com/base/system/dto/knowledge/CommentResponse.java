package com.base.system.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应
 */
@Data
public class CommentResponse {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 父评论ID（0表示顶级评论）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人ID
     */
    private Long commenterId;

    /**
     * 评论人昵称
     */
    private String commenterName;

    /**
     * 评论人头像
     */
    private String commenterAvatar;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 一级回复列表
     */
    private List<CommentResponse> children;
}
