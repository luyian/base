package com.base.common.thirdparty.approval;

import com.base.common.thirdparty.ThirdPartyPlatform;

/**
 * 第三方审批服务接口
 *
 * @author base
 */
public interface ThirdPartyApprovalService {

    /**
     * 获取平台标识
     *
     * @return 平台枚举
     */
    ThirdPartyPlatform getPlatform();

    /**
     * 发起审批
     *
     * @param request 审批发起请求
     * @return 审批发起响应
     */
    ApprovalCreateResponse createApproval(ApprovalCreateRequest request);

    /**
     * 撤销审批
     *
     * @param platformInstanceId 平台审批实例ID
     * @param approvalCode       审批定义编码
     * @param openId             操作人平台用户ID
     */
    void cancelApproval(String platformInstanceId, String approvalCode, String openId);

    /**
     * 查询审批实例状态
     *
     * @param platformInstanceId 平台审批实例ID
     * @return 审批实例信息
     */
    ApprovalInstanceInfo getApprovalStatus(String platformInstanceId);
}
