package com.base.approval.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 审批发起请求
 *
 * @author base
 */
@Data
@ApiModel("审批发起请求")
public class ApprovalSubmitRequest {

    @NotBlank(message = "模板编码不能为空")
    @ApiModelProperty(value = "模板编码", required = true)
    private String templateCode;

    @NotBlank(message = "业务主键不能为空")
    @ApiModelProperty(value = "业务主键", required = true)
    private String businessKey;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("审批标题")
    private String title;

    @ApiModelProperty("表单业务数据")
    private Map<String, Object> formData;
}
