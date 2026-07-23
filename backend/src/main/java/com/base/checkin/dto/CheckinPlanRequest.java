package com.base.checkin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 打卡计划新增/编辑请求
 *
 * @author base
 */
@Data
@ApiModel(value = "CheckinPlanRequest", description = "打卡计划新增/编辑请求")
public class CheckinPlanRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划名称
     */
    @NotBlank(message = "计划名称不能为空")
    @Size(max = 50, message = "计划名称不能超过50个字符")
    @ApiModelProperty(value = "计划名称", required = true)
    private String title;

    /**
     * 图标（emoji）
     */
    @Size(max = 20, message = "图标长度不能超过20个字符")
    @ApiModelProperty("图标（emoji）")
    private String icon;

    /**
     * 卡片颜色（hex）
     */
    @Size(max = 20, message = "颜色长度不能超过20个字符")
    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "颜色格式错误，应为#RRGGBB")
    @ApiModelProperty("卡片颜色（hex）")
    private String color;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注不能超过200个字符")
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 计划类型（0长期计划 1单日事件）
     */
    @ApiModelProperty("计划类型（0长期计划 1单日事件）")
    private Integer planType;

    /**
     * 目标日期（仅单日事件，格式 yyyy-MM-dd）
     */
    @ApiModelProperty("目标日期（仅单日事件，格式 yyyy-MM-dd）")
    private LocalDate targetDate;
}
