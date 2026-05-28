package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 流程定义保存请求
 */
@Data
public class ProcessDefinitionSaveRequest {

    private Long id;

    @NotBlank(message = "流程标识不能为空")
    private String processKey;

    @NotBlank(message = "流程名称不能为空")
    private String processName;

    private String category;

    private String description;

    @NotBlank(message = "BPMN XML不能为空")
    private String bpmnXml;
}
