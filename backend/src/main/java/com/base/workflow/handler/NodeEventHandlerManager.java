package com.base.workflow.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 事件处理器管理器
 */
@Component
public class NodeEventHandlerManager {

    private final Map<String, NodeEventHandler> handlerMap;

    @Autowired
    public NodeEventHandlerManager(ApplicationContext applicationContext) {
        Map<String, NodeEventHandler> beans = applicationContext.getBeansOfType(NodeEventHandler.class);
        this.handlerMap = beans.values().stream()
                .collect(Collectors.toMap(NodeEventHandler::getHandlerKey, Function.identity()));
    }

    /**
     * 获取处理器
     */
    public NodeEventHandler getHandler(String handlerKey) {
        return handlerMap.get(handlerKey);
    }

    /**
     * 触发节点进入事件
     */
    public void triggerOnEnter(ProcessContext context) {
        if (context.getCurrentNode() == null || context.getCurrentNode().getEventHandler() == null) {
            return;
        }
        NodeEventHandler handler = getHandler(context.getCurrentNode().getEventHandler());
        if (handler != null) {
            handler.onEnter(context);
        }
    }

    /**
     * 触发审批前事件
     */
    public void triggerBeforeApprove(ProcessContext context) {
        if (context.getCurrentNode() == null || context.getCurrentNode().getEventHandler() == null) {
            return;
        }
        NodeEventHandler handler = getHandler(context.getCurrentNode().getEventHandler());
        if (handler != null) {
            handler.beforeApprove(context);
        }
    }

    /**
     * 触发审批后事件
     */
    public void triggerAfterApprove(ProcessContext context) {
        if (context.getCurrentNode() == null || context.getCurrentNode().getEventHandler() == null) {
            return;
        }
        NodeEventHandler handler = getHandler(context.getCurrentNode().getEventHandler());
        if (handler != null) {
            handler.afterApprove(context);
        }
    }

    /**
     * 触发流程完成事件
     */
    public void triggerOnComplete(ProcessContext context) {
        if (context.getCurrentNode() == null || context.getCurrentNode().getEventHandler() == null) {
            return;
        }
        NodeEventHandler handler = getHandler(context.getCurrentNode().getEventHandler());
        if (handler != null) {
            handler.onComplete(context);
        }
    }
}
