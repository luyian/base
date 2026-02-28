package com.base.stock.recommend.entity;

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
 * 打分记录实体类
 *
 * @author base
 */
@Data
@TableName("stk_score_record")
@ApiModel(value = "ScoreRecord", description = "打分记录实体")
public class ScoreRecord implements Serializable {

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
     * 规则编码
     */
    @ApiModelProperty("规则编码")
    private String ruleCode;

    /**
     * 打分日期
     */
    @ApiModelProperty("打分日期")
    private LocalDate scoreDate;

    /**
     * 得分
     */
    @ApiModelProperty("得分")
    private BigDecimal score;

    /**
     * 加权得分（得分 * 权重）
     */
    @ApiModelProperty("加权得分")
    private BigDecimal weightedScore;

    /**
     * 命中标志（0-未命中, 1-命中）
     */
    @ApiModelProperty("命中标志")
    private Integer hitFlag;

    /**
     * 打分详情（JSON格式，记录计算过程）
     */
    @ApiModelProperty("打分详情")
    private String detailJson;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
