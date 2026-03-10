package com.base.workflow.handler;

import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessTask;

/**
 * 节点事件处理器接口
 */
public interface NodeEventHandler {

    /**
     * 获取处理器标识
     */
    String getHandlerKey();

    /**
     * 节点进入事件
     */
    default void onEnter(ProcessContext context) {
    }

    /**
     * 节点审批前事件
     */
    default void beforeApprove(ProcessContext context) {
    }

    /**
     * 节点审批后事件
     */
    default void afterApprove(ProcessContext context) {
    }

    /**
     * 节点跳过事件
     */
    default void onSkip(ProcessContext context) {
    }

    /**
     * 流程结束事件
     */
    default void onComplete(ProcessContext context) {
    }

    /**
     * 流程终止事件
     */
    default void onTerminate(ProcessContext context) {
    }
}
