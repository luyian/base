package com.base.approval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方事件回调日志
 *
 * @author base
 */
@Data
@TableName("tp_event_callback_log")
@ApiModel("事件回调日志")
public class EventCallbackLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("来源平台")
    private String platform;

    @ApiModelProperty("事件唯一ID")
    private String eventId;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事件原始内容（完整JSON）")
    private String eventPayload;

    @ApiModelProperty("处理状态（PENDING/PROCESSING/SUCCESS/FAILED/SKIPPED）")
    private String status;

    @ApiModelProperty("已重试次数")
    private Integer retryCount;

    @ApiModelProperty("最大重试次数")
    private Integer maxRetry;

    @ApiModelProperty("处理失败原因")
    private String errorMessage;

    @ApiModelProperty("处理完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime processedAt;

    @ApiModelProperty("下次重试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime nextRetryAt;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
