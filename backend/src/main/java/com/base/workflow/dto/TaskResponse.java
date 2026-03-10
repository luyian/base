package com.base.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务详情响应
 */
@Data
public class TaskResponse {

    private Long id;
    private Long instanceId;
    private String processKey;
    private String processName;
    private String nodeKey;
    private String nodeName;
    private String taskType;
    private String assignee;
    private String status;
    private String approveType;
    private String title;
    private String businessKey;
    private String initiator;
    private LocalDateTime createTime;

    private Integer canRollback;
    private String rollbackNodes;
}
