package com.base.common.feishu.approval;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.client.FeishuApiClient;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.approval.ApprovalCreateRequest;
import com.base.common.thirdparty.approval.ApprovalCreateResponse;
import com.base.common.thirdparty.approval.ApprovalInstanceInfo;
import com.base.common.thirdparty.approval.ApprovalStatusEnum;
import com.base.common.thirdparty.approval.ThirdPartyApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书审批服务实现
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuApprovalService implements ThirdPartyApprovalService {

    private final FeishuApiClient feishuApiClient;

    private static final String CREATE_INSTANCE_URL = "/approval/v4/instances";
    private static final String CANCEL_INSTANCE_URL = "/approval/v4/instances/cancel";
    private static final String GET_INSTANCE_URL = "/approval/v4/instances/";

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public ApprovalCreateResponse createApproval(ApprovalCreateRequest request) {
        Map<String, Object> body = new HashMap<>(8);
        body.put("approval_code", request.getApprovalCode());
        body.put("open_id", request.getApplicantOpenId());
        body.put("form", request.getFormData());
        if (request.getIdempotentKey() != null) {
            body.put("uuid", request.getIdempotentKey());
        }

        Map<String, Object> params = new HashMap<>(4);
        params.put("user_id_type", "open_id");

        JSONObject result = feishuApiClient.post(CREATE_INSTANCE_URL, body, params);
        String instanceCode = result.getJSONObject("data").getString("instance_code");
        log.info("飞书审批创建成功，instanceCode: {}，approvalCode: {}", instanceCode, request.getApprovalCode());

        ApprovalCreateResponse response = new ApprovalCreateResponse();
        response.setPlatformInstanceId(instanceCode);
        return response;
    }

    @Override
    public void cancelApproval(String platformInstanceId, String approvalCode, String openId) {
        Map<String, Object> body = new HashMap<>(8);
        body.put("approval_code", approvalCode);
        body.put("instance_code", platformInstanceId);
        body.put("user_id", openId);

        Map<String, Object> params = new HashMap<>(4);
        params.put("user_id_type", "open_id");

        feishuApiClient.post(CANCEL_INSTANCE_URL, body, params);
        log.info("飞书审批撤销成功，instanceCode: {}", platformInstanceId);
    }

    @Override
    public ApprovalInstanceInfo getApprovalStatus(String platformInstanceId) {
        JSONObject result = feishuApiClient.get(GET_INSTANCE_URL + platformInstanceId);
        JSONObject data = result.getJSONObject("data");

        ApprovalInstanceInfo info = new ApprovalInstanceInfo();
        info.setPlatformInstanceId(platformInstanceId);
        info.setStatus(mapFeishuStatus(data.getString("status")));

        Long endTime = data.getLong("end_time");
        if (endTime != null && endTime > 0) {
            info.setCompletedAt(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(endTime), ZoneId.systemDefault()));
        }
        return info;
    }

    /**
     * 飞书审批状态映射到通用状态
     */
    private ApprovalStatusEnum mapFeishuStatus(String feishuStatus) {
        if (feishuStatus == null) {
            return ApprovalStatusEnum.PENDING;
        }
        switch (feishuStatus) {
            case "PENDING":
                return ApprovalStatusEnum.PENDING;
            case "APPROVED":
                return ApprovalStatusEnum.APPROVED;
            case "REJECTED":
                return ApprovalStatusEnum.REJECTED;
            case "CANCELED":
                return ApprovalStatusEnum.CANCELED;
            case "DELETED":
                return ApprovalStatusEnum.DELETED;
            default:
                log.warn("未知的飞书审批状态: {}", feishuStatus);
                return ApprovalStatusEnum.PENDING;
        }
    }
}
