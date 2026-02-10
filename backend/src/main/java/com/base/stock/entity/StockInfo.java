package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 股票基础信息实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_stock_info")
@ApiModel(value = "StockInfo", description = "股票基础信息实体")
public class StockInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    @ApiModelProperty("股票代码")
    private String stockCode;

    /**
     * 股票名称
     */
    @ApiModelProperty("股票名称")
    private String stockName;

    /**
     * 市场（SH-沪市, SZ-深市, HK-港股）
     */
    @ApiModelProperty("市场（SH-沪市, SZ-深市, HK-港股）")
    private String market;

    /**
     * 交易所
     */
    @ApiModelProperty("交易所")
    private String exchange;

    /**
     * 交易货币（CNY/HKD）
     */
    @ApiModelProperty("交易货币")
    private String currency;

    /**
     * 状态（0-退市, 1-正常）
     */
    @ApiModelProperty("状态（0-退市, 1-正常）")
    private Integer status;

    /**
     * 股票类型（stock-股票）
     */
    @ApiModelProperty("股票类型")
    private String stockType;

    /**
     * 所属板块
     */
    @ApiModelProperty("所属板块")
    private String sector;

    /**
     * 所属行业
     */
    @ApiModelProperty("所属行业")
    private String industry;

    /**
     * 公司简介
     */
    @ApiModelProperty("公司简介")
    private String businessDesc;

    /**
     * 公司网站URL
     */
    @ApiModelProperty("公司网站URL")
    private String websiteUrl;

    /**
     * 总市值
     */
    @ApiModelProperty("总市值")
    private BigDecimal marketCap;

    /**
     * 总股本
     */
    @ApiModelProperty("总股本")
    private BigDecimal totalShares;

    /**
     * 市盈率
     */
    @ApiModelProperty("市盈率")
    private BigDecimal peRatio;

    /**
     * 52周最高价
     */
    @ApiModelProperty("52周最高价")
    @TableField("high_52_week")
    private BigDecimal high52Week;

    /**
     * 52周最低价
     */
    @ApiModelProperty("52周最低价")
    @TableField("low_52_week")
    private BigDecimal low52Week;
}
