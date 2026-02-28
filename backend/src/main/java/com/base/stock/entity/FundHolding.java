package com.base.stock.fund.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 基金持仓实体
 *
 * @author base
 */
@Data
@TableName("stk_fund_holding")
public class FundHolding implements Serializable {

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
     * 股票代码
     */
    private String stockCode;

    /**
     * 权重占比(%)
     */
    private BigDecimal weight;

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

    /**
     * 股票名称（非数据库字段，关联查询用）
     */
    @TableField(exist = false)
    private String stockName;

    /**
     * 市场（非数据库字段，关联查询用）
     */
    @TableField(exist = false)
    private String market;
}
