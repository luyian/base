package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * API Token 实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_api_token")
@ApiModel(value = "ApiToken", description = "API Token实体")
public class ApiToken extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Token值
     */
    @ApiModelProperty("Token值")
    private String tokenValue;

    /**
     * Token名称/备注
     */
    @ApiModelProperty("Token名称/备注")
    private String tokenName;

    /**
     * 服务商
     */
    @ApiModelProperty("服务商")
    private String provider;

    /**
     * 状态（0-作废, 1-正常）
     */
    @ApiModelProperty("状态（0-作废, 1-正常）")
    private Integer status;

    /**
     * 最后使用时间
     */
    @ApiModelProperty("最后使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastUsedTime;

    /**
     * 使用次数
     */
    @ApiModelProperty("使用次数")
    private Integer useCount;

    /**
     * 每日限额（0表示无限制）
     */
    @ApiModelProperty("每日限额（0表示无限制）")
    private Integer dailyLimit;

    /**
     * 当日已用次数
     */
    @ApiModelProperty("当日已用次数")
    private Integer dailyUsed;

    /**
     * 连续失败次数
     */
    @ApiModelProperty("连续失败次数")
    private Integer failCount;

    /**
     * 过期时间（null表示永不过期）
     */
    @ApiModelProperty("过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;
}
