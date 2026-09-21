package com.base.barcode.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 条码/二维码列表项响应 DTO（卡片列表：图 + 编号 + 绑定态）
 *
 * @author base
 */
@Data
@ApiModel(value = "BarcodeListResponse", description = "条码列表项响应")
public class BarcodeListResponse {

    /**
     * 条码记录ID
     */
    @ApiModelProperty("条码记录ID")
    private Long id;

    /**
     * 条码内容（系统唯一编号）
     */
    @ApiModelProperty("条码内容")
    private String code;

    /**
     * 类型（1条码 2二维码）
     */
    @ApiModelProperty("类型（1条码 2二维码）")
    private Integer type;

    /**
     * 是否已绑定
     */
    @ApiModelProperty("是否已绑定")
    private Boolean bound;

    /**
     * 图片访问URL（COS 预签名地址）
     */
    @ApiModelProperty("图片访问URL")
    private String fileUrl;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}