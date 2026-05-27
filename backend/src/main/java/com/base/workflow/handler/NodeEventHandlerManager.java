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
     * 触发事件
     *
     * @param handlerKey 处理器标识
     * @param eventType  事件类型：start / end / beforeApprove / afterApprove / complete / terminate
     * @param context    流程上下文
     */
    public void trigger(String handlerKey, String eventType, ProcessContext context) {
        if (handlerKey == null || handlerKey.isEmpty()) {
            return;
        }
        NodeEventHandler handler = getHandler(handlerKey);
        if (handler == null) {
            return;
        }
        switch (eventType) {
            case "start":
                handler.onEnter(context);
                break;
            case "end":
                handler.afterApprove(context);
                break;
            case "beforeApprove":
                handler.beforeApprove(context);
                break;
            case "complete":
                handler.onComplete(context);
                break;
            case "terminate":
                handler.onTerminate(context);
                break;
            default:
                break;
        }
    }
}
