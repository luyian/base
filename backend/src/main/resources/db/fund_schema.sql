-- =============================================
-- 基金估值功能数据库表结构
-- =============================================

-- 基金配置主表
CREATE TABLE IF NOT EXISTS `stk_fund_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `fund_name` VARCHAR(100) NOT NULL COMMENT '基金名称',
    `fund_code` VARCHAR(50) DEFAULT NULL COMMENT '基金代码（可选）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基金配置主表';

-- 基金持仓明细表
CREATE TABLE IF NOT EXISTS `stk_fund_holding` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fund_id` BIGINT NOT NULL COMMENT '基金ID',
    `stock_code` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `weight` DECIMAL(8,4) NOT NULL COMMENT '权重占比(%)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_fund_id` (`fund_id`),
    KEY `idx_stock_code` (`stock_code`),
    UNIQUE KEY `uk_fund_stock` (`fund_id`, `stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基金持仓明细表';
