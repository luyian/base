package com.base.common.thirdparty.approval;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用审批实例信息（从平台查询返回）
 *
 * @author base
 */
@Data
public class ApprovalInstanceInfo {

    /**
     * 平台审批实例ID
     */
    private String platformInstanceId;

    /**
     * 审批状态
     */
    private ApprovalStatusEnum status;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
