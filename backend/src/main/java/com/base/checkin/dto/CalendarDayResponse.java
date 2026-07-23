package com.base.checkin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 日历单日完成度响应
 *
 * @author base
 */
@Data
@ApiModel(value = "CalendarDayResponse", description = "日历单日完成度响应")
public class CalendarDayResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期（yyyy-MM-dd）
     */
    @ApiModelProperty("日期（yyyy-MM-dd）")
    private String date;

    /**
     * 启用计划总数
     */
    @ApiModelProperty("启用计划总数")
    private Integer total;

    /**
     * 当天已完成数
     */
    @ApiModelProperty("当天已完成数")
    private Integer completed;
}
