package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 发起流程请求
 */
@Data
public class StartProcessRequest {

    @NotBlank(message = "流程标识不能为空")
    private String processKey;

    @NotBlank(message = "业务主键不能为空")
    private String businessKey;

    private String businessType;

    @NotBlank(message = "流程标题不能为空")
    private String title;

    private Map<String, Object> variables;
}
