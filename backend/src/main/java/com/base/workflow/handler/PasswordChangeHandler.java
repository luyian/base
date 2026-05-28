package com.base.workflow.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 密码变更流程事件处理器
 */
@Component
public class PasswordChangeHandler implements NodeEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PasswordChangeHandler.class);

    @Override
    public String getHandlerKey() {
        return "password_change";
    }

    @Override
    public void afterApprove(ProcessContext context) {
        Map<String, Object> variables = context.getVariables();
        String approveResult = (String) variables.get("approveResult");
        String initiator = context.getInitiator();
        String reason = (String) variables.get("reason");

        if ("APPROVE".equals(approveResult)) {
            log.info("密码变更申请已通过 - 申请人: {}, 原因: {}", initiator, reason);
            // TODO 审批通过后的业务逻辑，例如：
            // 1. 发送通知告知申请人审批已通过
            // 2. 自动执行密码重置
            // 3. 更新业务状态表
        } else if ("REJECT".equals(approveResult)) {
            log.info("密码变更申请已拒绝 - 申请人: {}, 原因: {}", initiator, reason);
            // TODO 审批拒绝后的业务逻辑，例如：
            // 1. 发送通知告知申请人审批被拒绝
        }
    }

    @Override
    public void onTerminate(ProcessContext context) {
        log.info("密码变更流程被终止 - 流程实例: {}", context.getProcessInstanceId());
    }
}
