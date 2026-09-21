package com.base.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品关联条码响应 DTO
 *
 * @author base
 */
@Data
@ApiModel(value = "ProductBarcodeResponse", description = "商品关联条码响应")
public class ProductBarcodeResponse {

    /**
     * 条码记录ID
     */
    @ApiModelProperty("条码记录ID")
    private Long id;

    /**
     * 条码内容
     */
    @ApiModelProperty("条码内容")
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
     * 条码/二维码图片访问 URL（COS 预签名地址，可空）
     */
    @ApiModelProperty("条码/二维码图片访问URL")
    private String fileUrl;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}