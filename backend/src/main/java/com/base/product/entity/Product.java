package com.base.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
@ApiModel(value = "Product", description = "商品实体")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 商品图片地址（预留）
     */
    @ApiModelProperty("商品图片地址")
    private String imageUrl;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 所属用户ID
     */
    @ApiModelProperty("所属用户ID")
    private Long userId;
}