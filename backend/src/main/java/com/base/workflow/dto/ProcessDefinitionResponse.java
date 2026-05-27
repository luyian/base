package com.base.workflow.dto;

import com.base.workflow.entity.FlowableDefinitionExt;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程定义详情响应
 */
@Data
public class ProcessDefinitionResponse {

    /** 扩展表ID */
    private Long id;

    /** Flowable 流程定义ID */
    private String processDefinitionId;

    /** Flowable 部署ID */
    private String deploymentId;

    private String processKey;

    private String processName;

    private String category;

    private Integer version;

    /** 状态(0草稿 1已发布 2禁用) */
    private Integer status;

    private String description;

    /** BPMN XML 内容 */
    private String bpmnXml;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String createBy;

    /**
     * 从扩展表实体转换
     */
    public static ProcessDefinitionResponse fromEntity(FlowableDefinitionExt ext) {
        ProcessDefinitionResponse response = new ProcessDefinitionResponse();
        response.setId(ext.getId());
        response.setProcessDefinitionId(ext.getProcessDefinitionId());
        response.setDeploymentId(ext.getDeploymentId());
        response.setProcessKey(ext.getProcessKey());
        response.setProcessName(ext.getProcessName());
        response.setCategory(ext.getCategory());
        response.setVersion(ext.getVersion());
        response.setStatus(ext.getStatus());
        response.setDescription(ext.getDescription());
        response.setBpmnXml(ext.getBpmnXml());
        response.setCreateTime(ext.getCreateTime());
        response.setCreateBy(ext.getCreateBy());
        return response;
    }
}
