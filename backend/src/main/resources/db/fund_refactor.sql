-- 基金权限重构：去掉 user_id，基金变为公共资源
-- 1. stk_fund_config 去掉 user_id 字段
ALTER TABLE `stk_fund_config` DROP COLUMN `user_id`;

-- 2. fund_code 改为唯一标识
ALTER TABLE `stk_fund_config` ADD UNIQUE KEY `uk_fund_code` (`fund_code`);

-- 3. 新增基金自选表
CREATE TABLE `stk_fund_watchlist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fund_id` BIGINT NOT NULL COMMENT '基金ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fund_user` (`fund_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基金自选表';
