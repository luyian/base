package com.base.common.thirdparty.approval;

import lombok.Data;

/**
 * 通用审批发起响应
 *
 * @author base
 */
@Data
public class ApprovalCreateResponse {

    /**
     * 平台审批实例ID
     */
    private String platformInstanceId;
}
