package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 回退任务请求
 */
@Data
public class RollbackTaskRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "目标节点不能为空")
    private String targetNodeKey;

    private String comment;
}
