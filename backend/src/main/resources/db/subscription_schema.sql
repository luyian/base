-- 消息订阅表
CREATE TABLE `msg_subscription` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `sub_type` VARCHAR(50) NOT NULL COMMENT '订阅类型：fund_valuation-基金估值',
    `channel` VARCHAR(50) NOT NULL DEFAULT 'feishu' COMMENT '推送渠道：feishu/dingtalk/email',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `last_push_time` DATETIME NULL COMMENT '最后推送时间',
    `last_push_status` TINYINT NULL COMMENT '最后推送状态：0-失败，1-成功',
    `last_push_msg` VARCHAR(500) NULL COMMENT '最后推送结果描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_type_channel` (`user_id`, `sub_type`, `channel`),
    KEY `idx_sub_type_status` (`sub_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息订阅表';
