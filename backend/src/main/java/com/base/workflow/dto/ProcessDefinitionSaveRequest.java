package com.base.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    private List<NodeConfig> nodes;

    private List<NodeRelationConfig> nodeRelations;

    @Data
    public static class NodeConfig {
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
    public static class NodeRelationConfig {
        private String sourceNodeKey;
        private String targetNodeKey;
        private String conditionExpr;
        private String relationName;
    }
}
