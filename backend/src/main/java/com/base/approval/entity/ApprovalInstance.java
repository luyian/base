package com.base.approval.entity;

import com.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 第三方审批实例
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tp_approval_instance")
@ApiModel("审批实例")
public class ApprovalInstance extends BaseEntity {

    @ApiModelProperty("关联模板ID")
    private Long templateId;

    @ApiModelProperty("模板编码（冗余）")
    private String templateCode;

    @ApiModelProperty("第三方平台")
    private String platform;

    @ApiModelProperty("平台审批实例ID")
    private String platformInstanceId;

    @ApiModelProperty("业务主键")
    private String businessKey;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("审批标题")
    private String title;

    @ApiModelProperty("发起人系统用户ID")
    private Long applicantUserId;

    @ApiModelProperty("发起人平台用户ID")
    private String applicantOpenId;

    @ApiModelProperty("提交的表单数据（JSON）")
    private String formData;

    @ApiModelProperty("审批状态（PENDING/APPROVED/REJECTED/CANCELED）")
    private String status;

    @ApiModelProperty("平台原始状态")
    private String platformStatus;

    @ApiModelProperty("审批结果说明")
    private String resultComment;

    @ApiModelProperty("幂等键")
    private String idempotentKey;

    @ApiModelProperty("提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submittedAt;

    @ApiModelProperty("完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime completedAt;
}
