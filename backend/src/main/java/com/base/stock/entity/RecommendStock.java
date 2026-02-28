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
 * 推荐股票实体类
 *
 * @author base
 */
@Data
@TableName("stk_recommend")
@ApiModel(value = "RecommendStock", description = "推荐股票实体")
public class RecommendStock implements Serializable {

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
     * 推荐日期
     */
    @ApiModelProperty("推荐日期")
    private LocalDate recommendDate;

    /**
     * 总分
     */
    @ApiModelProperty("总分")
    private BigDecimal totalScore;

    /**
     * 命中规则数
     */
    @ApiModelProperty("命中规则数")
    private Integer hitRuleCount;

    /**
     * 总规则数
     */
    @ApiModelProperty("总规则数")
    private Integer totalRuleCount;

    /**
     * 命中率（%）
     */
    @ApiModelProperty("命中率")
    private BigDecimal hitRate;

    /**
     * 得分明细（JSON格式）
     */
    @ApiModelProperty("得分明细")
    private String scoreDetail;

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
