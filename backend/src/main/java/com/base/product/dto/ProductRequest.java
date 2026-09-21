package com.base.product.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增/编辑商品请求 DTO
 *
 * @author base
 */
@Data
@ApiModel(value = "ProductRequest", description = "商品请求")
public class ProductRequest {

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称过长")
    @ApiModelProperty("商品名称")
    private String name;

    /**
     * 分类
     */
    @Size(max = 50, message = "分类过长")
    @ApiModelProperty("分类")
    private String category;

    /**
     * 规格
     */
    @Size(max = 50, message = "规格过长")
    @ApiModelProperty("规格")
    private String spec;

    /**
     * 单位
     */
    @Size(max = 20, message = "单位过长")
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
     * 库存（默认 0）
     */
    @ApiModelProperty("库存")
    private Integer stock;

    /**
     * 供应商
     */
    @Size(max = 100, message = "供应商过长")
    @ApiModelProperty("供应商")
    private String supplier;

    /**
     * 生产日期
     */
    @ApiModelProperty("生产日期")
    private LocalDate productionDate;

    /**
     * 商品图片地址（预留）
     */
    @Size(max = 255, message = "图片地址过长")
    @ApiModelProperty("商品图片地址")
    private String imageUrl;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注过长")
    @ApiModelProperty("备注")
    private String remark;
}