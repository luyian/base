package com.base.common.thirdparty.approval;

import lombok.Data;

/**
 * 通用审批发起请求
 *
 * @author base
 */
@Data
public class ApprovalCreateRequest {

    /**
     * 平台审批定义编码
     */
    private String approvalCode;

    /**
     * 发起人平台用户ID（如飞书 open_id）
     */
    private String applicantOpenId;

    /**
     * 表单数据 JSON（已按平台格式构建）
     */
    private String formData;

    /**
     * 幂等键
     */
    private String idempotentKey;
}
