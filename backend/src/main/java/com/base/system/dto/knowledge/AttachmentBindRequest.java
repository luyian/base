package com.base.system.dto.knowledge;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 附件绑定请求
 *
 * <p>前端先调用 /system/file/upload 上传文件得到 fileId，再调用本接口将文件绑定到文档。</p>
 */
@Data
public class AttachmentBindRequest {

    /**
     * 所属文档ID
     */
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    /**
     * 文件ID（sys_file.id）
     */
    @NotNull(message = "文件ID不能为空")
    private Long fileId;
}
