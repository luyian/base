package com.base.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息订阅实体
 *
 * @author base
 */
@Data
@TableName("msg_subscription")
public class Subscription implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 订阅类型 */
    private String subType;

    /** 推送渠道 */
    private String channel;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 最后推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastPushTime;

    /** 最后推送状态：0-失败，1-成功 */
    private Integer lastPushStatus;

    /** 最后推送结果描述 */
    private String lastPushMsg;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
