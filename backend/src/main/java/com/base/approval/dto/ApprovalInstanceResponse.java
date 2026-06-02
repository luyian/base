package com.base.approval.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批实例响应
 *
 * @author base
 */
@Data
@ApiModel("审批实例响应")
public class ApprovalInstanceResponse {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("模板编码")
    private String templateCode;

    @ApiModelProperty("模板名称")
    private String templateName;

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

    @ApiModelProperty("发起人用户ID")
    private Long applicantUserId;

    @ApiModelProperty("发起人姓名")
    private String applicantName;

    @ApiModelProperty("表单数据（JSON）")
    private String formData;

    @ApiModelProperty("审批状态")
    private String status;

    @ApiModelProperty("审批结果说明")
    private String resultComment;

    @ApiModelProperty("提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submittedAt;

    @ApiModelProperty("完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime completedAt;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
