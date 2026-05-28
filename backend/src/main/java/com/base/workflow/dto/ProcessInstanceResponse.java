package com.base.workflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例响应
 */
@Data
public class ProcessInstanceResponse {

    /** Flowable 流程实例ID */
    private String processInstanceId;

    private String processDefinitionId;

    private String processKey;

    private String processName;

    private String businessKey;

    private String businessType;

    private String title;

    /** 发起人 */
    private String initiator;

    /** 当前活动名称 */
    private String currentActivityName;

    /** 流程状态: RUNNING / COMPLETED / TERMINATED */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}
