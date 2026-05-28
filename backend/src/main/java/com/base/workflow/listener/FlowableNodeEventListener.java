package com.base.workflow.listener;

import com.base.workflow.handler.NodeEventHandlerManager;
import com.base.workflow.handler.ProcessContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Flowable 节点事件监听器
 * <p>
 * 桥接 Flowable ExecutionListener 与现有 NodeEventHandler 机制。
 * 在 BPMN 节点上配置流程变量 eventHandler_{activityId} 指定处理器标识。
 * </p>
 */
@Component("flowableNodeEventListener")
public class FlowableNodeEventListener implements ExecutionListener {

    @Autowired
    private NodeEventHandlerManager eventHandlerManager;

    @Override
    public void notify(DelegateExecution execution) {
        String activityId = execution.getCurrentActivityId();
        String handlerKey = (String) execution.getVariable("eventHandler_" + activityId);

        if (handlerKey == null || handlerKey.isEmpty()) {
            return;
        }

        ProcessContext context = new ProcessContext();
        context.setProcessInstanceId(execution.getProcessInstanceId());
        context.setProcessDefinitionKey(execution.getProcessDefinitionId());
        context.setActivityId(activityId);
        context.setActivityName((String) execution.getVariable("activityName_" + activityId));
        context.setBusinessKey(execution.getProcessInstanceBusinessKey());
        context.setInitiator((String) execution.getVariable("initiator"));
        context.setVariables(execution.getVariables());

        eventHandlerManager.trigger(handlerKey, execution.getEventName(), context);
    }
}
