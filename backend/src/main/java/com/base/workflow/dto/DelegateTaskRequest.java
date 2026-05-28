package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 转办任务请求
 */
@Data
public class DelegateTaskRequest {

    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    @NotBlank(message = "被转办人不能为空")
    private String delegateTo;

    private String comment;
}
