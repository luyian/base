package com.base.checkin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 打卡计划响应（含今日打卡状态）
 *
 * @author base
 */
@Data
@ApiModel(value = "CheckinPlanResponse", description = "打卡计划响应")
public class CheckinPlanResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty("计划ID")
    private Long id;

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
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer sortOrder;

    /**
     * 状态（0停用 1启用）
     */
    @ApiModelProperty("状态（0停用 1启用）")
    private Integer status;

    /**
     * 今日是否已打卡
     */
    @ApiModelProperty("今日是否已打卡")
    private Boolean todayChecked;

    /**
     * 是否已删除（历史日期列表可能包含已删除计划，前端据此置灰且不可操作）
     */
    @ApiModelProperty("是否已删除")
    private Boolean deleted;
}
