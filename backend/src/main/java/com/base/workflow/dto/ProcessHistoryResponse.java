package com.base.workflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程历史记录响应
 */
@Data
public class ProcessHistoryResponse {

    /** 活动ID */
    private String activityId;

    /** 活动名称 */
    private String activityName;

    /** 活动类型(userTask/startEvent/endEvent等) */
    private String activityType;

    /** 办理人 */
    private String assignee;

    /** 办理人姓名 */
    private String assigneeName;

    /** 操作类型 */
    private String action;

    /** 审批意见 */
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}
