package com.base.system.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 附件响应
 */
@Data
public class AttachmentResponse {

    /**
     * 关联记录ID（file_link_obj.id，用于删除）
     */
    private Long id;

    /**
     * 文件ID（sys_file.id）
     */
    private Long fileId;

    /**
     * 文件名（原始文件名）
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 预签名访问URL
     */
    private String fileUrl;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
