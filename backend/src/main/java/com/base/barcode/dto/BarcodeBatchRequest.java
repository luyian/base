package com.base.barcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 批量生成条码/二维码请求 DTO（数量 + 码制，内容由系统自动生成唯一编号）
 *
 * @author base
 */
@Data
@ApiModel(value = "BarcodeBatchRequest", description = "批量生成条码请求")
public class BarcodeBatchRequest {

    /**
     * 码制（CODE128 条形码 / QR 二维码），编号前缀对应 CODE / QR
     */
    @NotBlank(message = "条码类型不能为空")
    @Pattern(regexp = "^(CODE128|QR)$", message = "条码类型仅支持 CODE128 或 QR")
    @ApiModelProperty("类型（CODE128 / QR）")
    private String type;

    /**
     * 生成数量
     */
    @NotNull(message = "生成数量不能为空")
    @Min(value = 1, message = "生成数量至少为 1")
    @Max(value = 200, message = "单次最多生成 200 个")
    @ApiModelProperty("生成数量（1~200）")
    private Integer count;
}