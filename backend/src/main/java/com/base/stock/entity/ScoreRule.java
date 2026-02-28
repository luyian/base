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
import java.time.LocalDateTime;

/**
 * 打分规则配置实体类
 *
 * @author base
 */
@Data
@TableName("stk_score_rule")
@ApiModel(value = "ScoreRule", description = "打分规则配置实体")
public class ScoreRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 规则编码（唯一标识，对应策略Bean名称）
     */
    @ApiModelProperty("规则编码")
    private String ruleCode;

    /**
     * 规则名称
     */
    @ApiModelProperty("规则名称")
    private String ruleName;

    /**
     * 规则描述
     */
    @ApiModelProperty("规则描述")
    private String ruleDesc;

    /**
     * 规则分类（TECHNICAL-技术面, FUNDAMENTAL-基本面）
     */
    @ApiModelProperty("规则分类")
    private String category;

    /**
     * 打分类型（FIXED-固定分, DYNAMIC-动态分）
     */
    @ApiModelProperty("打分类型")
    private String scoreType;

    /**
     * 基础分数
     */
    @ApiModelProperty("基础分数")
    private Integer baseScore;

    /**
     * 最高分数（动态打分时使用）
     */
    @ApiModelProperty("最高分数")
    private Integer maxScore;

    /**
     * 权重系数（最终得分 = 规则得分 * 权重）
     */
    @ApiModelProperty("权重系数")
    private BigDecimal weight;

    /**
     * 规则参数配置（JSON格式）
     */
    @ApiModelProperty("规则参数配置")
    private String configJson;

    /**
     * 状态（0-禁用, 1-启用）
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 排序号（执行顺序）
     */
    @ApiModelProperty("排序号")
    private Integer sortOrder;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新人")
    private String updateBy;

    /**
     * 删除标志（0-未删除, 1-已删除）
     */
    @ApiModelProperty("删除标志")
    private Integer deleted;
}
