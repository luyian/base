package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 审批任务请求
 */
@Data
public class ApproveTaskRequest {

    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    @NotBlank(message = "审批结果不能为空")
    private String approveResult;

    private String comment;
}
