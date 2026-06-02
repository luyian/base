package com.base.common.feishu.event.handler;

import com.alibaba.fastjson2.JSONObject;
import com.base.approval.service.ApprovalInstanceService;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEvent;
import com.base.common.thirdparty.event.ThirdPartyEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 飞书审批状态变更事件处理器
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeishuApprovalEventHandler implements ThirdPartyEventHandler {

    private final ApprovalInstanceService approvalInstanceService;

    @Override
    public String getEventType() {
        return "approval_instance";
    }

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public void handle(ThirdPartyEvent event) {
        JSONObject payload = JSONObject.parseObject(event.getPayload());
        JSONObject eventBody = payload.getJSONObject("event");
        if (eventBody == null) {
            log.warn("审批事件缺少 event 字段: eventId={}", event.getEventId());
            return;
        }

        String instanceCode = eventBody.getString("instance_code");
        String status = eventBody.getString("status");
        String comment = eventBody.getString("comment");

        log.info("处理飞书审批事件: instanceCode={}, status={}", instanceCode, status);

        approvalInstanceService.updateStatusFromCallback(
                ThirdPartyPlatform.FEISHU, instanceCode, status, comment);
    }
}
