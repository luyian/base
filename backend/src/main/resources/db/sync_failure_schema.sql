-- 股票同步失败记录表
CREATE TABLE IF NOT EXISTS `stk_sync_failure` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `start_date` date NOT NULL COMMENT '开始日期',
    `end_date` date NOT NULL COMMENT '结束日期',
    `failure_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
    `retry_count` int DEFAULT 0 COMMENT '重试次数',
    `status` tinyint DEFAULT 0 COMMENT '状态：0-待重试，1-重试成功，2-放弃重试',
    `last_retry_time` datetime DEFAULT NULL COMMENT '最后重试时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_stock_code` (`stock_code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票同步失败记录表';
