package com.base.workflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务详情响应
 */
@Data
public class TaskResponse {

    /** Flowable 任务ID */
    private String id;

    /** Flowable 流程实例ID */
    private String processInstanceId;

    private String processDefinitionId;

    private String processKey;

    private String processName;

    /** 当前活动ID */
    private String activityId;

    /** 当前活动名称 */
    private String activityName;

    private String assignee;

    private String title;

    private String businessKey;

    private String initiator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
