package com.base.system.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 文件批量下载请求
 *
 * @author base
 */
@Data
public class FileBatchDownloadRequest {

    /**
     * 文件ID列表
     */
    @NotEmpty(message = "文件ID列表不能为空")
    private List<Long> ids;
}
