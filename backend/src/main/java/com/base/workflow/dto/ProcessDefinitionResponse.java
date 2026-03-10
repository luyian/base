package com.base.workflow.dto;

import com.base.workflow.entity.ProcessDefinition;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程定义详情响应
 */
@Data
public class ProcessDefinitionResponse {

    private Long id;
    private String processKey;
    private String processName;
    private String category;
    private Integer version;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private String createBy;

    private List<NodeResponse> nodes;
    private List<NodeRelationResponse> nodeRelations;

    @Data
    public static class NodeResponse {
        private Long id;
        private String nodeKey;
        private String nodeName;
        private String nodeType;
        private String eventHandler;
        private String candidateType;
        private String candidateConfig;
        private String approveType;
        private Double approveRatio;
        private Integer canRollback;
        private String rollbackNodes;
        private Integer positionX;
        private Integer positionY;
    }

    @Data
    public static class NodeRelationResponse {
        private Long id;
        private String sourceNodeKey;
        private String targetNodeKey;
        private String conditionExpr;
        private String relationName;
    }

    public static ProcessDefinitionResponse fromEntity(ProcessDefinition definition) {
        ProcessDefinitionResponse response = new ProcessDefinitionResponse();
        response.setId(definition.getId());
        response.setProcessKey(definition.getProcessKey());
        response.setProcessName(definition.getProcessName());
        response.setCategory(definition.getCategory());
        response.setVersion(definition.getVersion());
        response.setStatus(definition.getStatus());
        response.setDescription(definition.getDescription());
        response.setCreateTime(definition.getCreateTime());
        response.setCreateBy(definition.getCreateBy());
        return response;
    }
}
