package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * K线数据实体类（日K）
 *
 * @author base
 */
@Data
@TableName("stk_kline_daily")
@ApiModel(value = "StockKline", description = "K线数据实体")
public class StockKline implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 股票代码
     */
    @ApiModelProperty("股票代码")
    private String stockCode;

    /**
     * 交易日期
     */
    @ApiModelProperty("交易日期")
    private LocalDate tradeDate;

    /**
     * 开盘价
     */
    @ApiModelProperty("开盘价")
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    @ApiModelProperty("最高价")
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    @ApiModelProperty("最低价")
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    @ApiModelProperty("收盘价")
    private BigDecimal closePrice;

    /**
     * 成交量
     */
    @ApiModelProperty("成交量")
    private Long volume;

    /**
     * 成交额
     */
    @ApiModelProperty("成交额")
    private BigDecimal amount;

    /**
     * 涨跌幅(%)
     */
    @ApiModelProperty("涨跌幅(%)")
    private BigDecimal changeRate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
