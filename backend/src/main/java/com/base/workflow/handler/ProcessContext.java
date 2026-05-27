package com.base.workflow.handler;

import lombok.Data;

import java.util.Map;

/**
 * 流程上下文（适配 Flowable 引擎）
 */
@Data
public class ProcessContext {

    /** 流程实例 ID */
    private String processInstanceId;

    /** 流程定义 Key */
    private String processDefinitionKey;

    /** 流程名称 */
    private String processName;

    /** 业务标识 */
    private String businessKey;

    /** 当前活动（节点）ID */
    private String activityId;

    /** 当前活动名称 */
    private String activityName;

    /** 任务 ID */
    private String taskId;

    /** 发起人 */
    private String initiator;

    /** 操作人 */
    private String operator;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作类型 */
    private String action;

    /** 审批意见 */
    private String comment;

    /** 流程变量 */
    private Map<String, Object> variables;
}
