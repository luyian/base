package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 回退任务请求
 */
@Data
public class RollbackTaskRequest {

    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    @NotBlank(message = "目标活动ID不能为空")
    private String targetActivityId;

    private String comment;
}
