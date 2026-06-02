package com.base.approval.service;

import com.base.approval.dto.ApprovalInstanceResponse;
import com.base.approval.dto.ApprovalSubmitRequest;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 审批实例服务接口
 *
 * @author base
 */
public interface ApprovalInstanceService {

    /**
     * 发起审批
     */
    ApprovalInstanceResponse submitApproval(ApprovalSubmitRequest request);

    /**
     * 撤销审批
     */
    void cancelApproval(Long instanceId);

    /**
     * 事件回调更新审批状态
     */
    void updateStatusFromCallback(ThirdPartyPlatform platform, String platformInstanceId,
                                  String platformStatus, String comment);

    /**
     * 手动同步审批状态
     */
    void syncApprovalStatus(Long instanceId);

    /**
     * 分页查询审批实例
     */
    IPage<ApprovalInstanceResponse> pageList(int pageNum, int pageSize, String status,
                                              String businessType, Long applicantUserId);

    /**
     * 查询审批详情
     */
    ApprovalInstanceResponse getDetail(Long instanceId);

    /**
     * 按业务键查询
     */
    ApprovalInstanceResponse getByBusinessKey(String businessKey);
}
