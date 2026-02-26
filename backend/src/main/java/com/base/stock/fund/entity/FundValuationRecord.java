package com.base.stock.fund.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金估值记录实体
 *
 * @author base
 */
@Data
@TableName("stk_fund_valuation_record")
public class FundValuationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 基金ID
     */
    private Long fundId;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 估算涨跌幅(%)
     */
    private BigDecimal estimatedChangePercent;

    /**
     * 持仓数量
     */
    private Integer holdingCount;

    /**
     * 成功获取报价数量
     */
    private Integer successCount;

    /**
     * 失败获取报价数量
     */
    private Integer failCount;

    /**
     * 总权重(%)
     */
    private BigDecimal totalWeight;

    /**
     * 股票报价明细（JSON格式）
     */
    private String quotesJson;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
