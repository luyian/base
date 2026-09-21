package com.base.barcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 生成条码请求 DTO
 *
 * @author base
 */
@Data
@ApiModel(value = "BarcodeGenerateRequest", description = "生成条码请求")
public class BarcodeGenerateRequest {

    /**
     * 条码/二维码内容（可编码任意文本，与业务解耦）
     */
    @NotBlank(message = "条码内容不能为空")
    @Size(max = 256, message = "条码内容过长")
    @ApiModelProperty("条码/二维码内容")
    private String content;

    /**
     * 类型（CODE128 条形码 / QR 二维码）
     */
    @NotBlank(message = "条码类型不能为空")
    @Pattern(regexp = "^(CODE128|QR)$", message = "条码类型仅支持 CODE128 或 QR")
    @ApiModelProperty("类型（CODE128 条形码 / QR 二维码）")
    private String type;
}