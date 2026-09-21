package com.base.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品响应 DTO
 *
 * @author base
 */
@Data
@ApiModel(value = "ProductResponse", description = "商品响应")
public class ProductResponse {

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 商品唯一编码（新增商品时系统自动生成，如 PROD202609210001）
     */
    @ApiModelProperty("商品唯一编码")
    private String code;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String name;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String category;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String spec;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 售价
     */
    @ApiModelProperty("售价")
    private BigDecimal salePrice;

    /**
     * 成本价
     */
    @ApiModelProperty("成本价")
    private BigDecimal costPrice;

    /**
     * 库存
     */
    @ApiModelProperty("库存")
    private Integer stock;

    /**
     * 供应商
     */
    @ApiModelProperty("供应商")
    private String supplier;

    /**
     * 生产日期
     */
    @ApiModelProperty("生产日期")
    private LocalDate productionDate;

    /**
     * 商品图片地址
     */
    @ApiModelProperty("商品图片地址")
    private String imageUrl;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}