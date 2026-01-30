package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
