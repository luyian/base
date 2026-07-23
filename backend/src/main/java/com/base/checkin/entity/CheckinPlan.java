package com.base.checkin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡计划实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_plan")
@ApiModel(value = "CheckinPlan", description = "打卡计划实体")
public class CheckinPlan extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 计划名称
     */
    @ApiModelProperty("计划名称")
    private String title;

    /**
     * 图标（emoji）
     */
    @ApiModelProperty("图标（emoji）")
    private String icon;

    /**
     * 卡片颜色（hex）
     */
    @ApiModelProperty("卡片颜色（hex）")
    private String color;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 计划类型（0长期计划 1单日事件）
     */
    @ApiModelProperty("计划类型（0长期计划 1单日事件）")
    private Integer planType;

    /**
     * 目标日期（仅单日事件）
     */
    @ApiModelProperty("目标日期（仅单日事件）")
    private LocalDate targetDate;

    /**
     * 排序号（越小越靠前）
     */
    @ApiModelProperty("排序号")
    private Integer sortOrder;

    /**
     * 状态（0停用 1启用）
     */
    @ApiModelProperty("状态（0停用 1启用）")
    private Integer status;

    /**
     * 删除时间，用于保留历史日历统计口径
     */
    @ApiModelProperty("删除时间")
    private LocalDateTime deletedTime;
}
