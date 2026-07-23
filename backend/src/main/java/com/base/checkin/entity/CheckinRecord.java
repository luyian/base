package com.base.checkin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 每日打卡记录实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_record")
@ApiModel(value = "CheckinRecord", description = "每日打卡记录实体")
public class CheckinRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty("计划ID")
    private Long planId;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 打卡日期
     */
    @ApiModelProperty("打卡日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkinDate;
}
