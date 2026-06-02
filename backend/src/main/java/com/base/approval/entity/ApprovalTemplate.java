package com.base.approval.entity;

import com.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方审批模板
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tp_approval_template")
@ApiModel("审批模板")
public class ApprovalTemplate extends BaseEntity {

    @ApiModelProperty("模板编码（业务类型标识）")
    private String templateCode;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("第三方平台（feishu/dingtalk）")
    private String platform;

    @ApiModelProperty("平台审批定义编码")
    private String platformApprovalCode;

    @ApiModelProperty("表单字段映射配置（JSON）")
    private String formMapping;

    @ApiModelProperty("模板说明")
    private String description;

    @ApiModelProperty("状态（0-禁用 1-启用）")
    private Integer status;
}
