package com.base.workflow.handler;

import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessTask;
import lombok.Data;

import java.util.Map;

/**
 * 流程上下文
 */
@Data
public class ProcessContext {

    private ProcessInstance processInstance;

    private ProcessNode currentNode;

    private ProcessTask currentTask;

    private String operator;

    private String operatorName;

    private String action;

    private String comment;

    private Map<String, Object> variables;

    public ProcessContext() {
    }

    public ProcessContext(ProcessInstance processInstance, ProcessNode currentNode) {
        this.processInstance = processInstance;
        this.currentNode = currentNode;
    }
}
