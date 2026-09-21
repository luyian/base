package com.base.barcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 登记通用条码记录请求 DTO
 *
 * @author base
 */
@Data
@ApiModel(value = "BarcodeRecordRequest", description = "登记通用条码记录请求")
public class BarcodeRecordRequest {

    /**
     * 条码/二维码内容（全局唯一）
     */
    @NotBlank(message = "条码内容不能为空")
    @Size(max = 64, message = "条码内容过长")
    @ApiModelProperty("条码/二维码内容")
    private String code;

    /**
     * 类型（1条码 2二维码）
     */
    @ApiModelProperty("类型（1条码 2二维码）")
    private Integer type;

    /**
     * 来源（1自生成 2图片识别录入）
     */
    @ApiModelProperty("来源（1自生成 2图片识别录入）")
    private Integer source;

    /**
     * 业务类型（如 PRODUCT）
     */
    @ApiModelProperty("业务类型")
    private String bizType;

    /**
     * 业务对象ID（如商品ID）
     */
    @ApiModelProperty("业务对象ID")
    private Long bizId;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注过长")
    @ApiModelProperty("备注")
    private String remark;
}