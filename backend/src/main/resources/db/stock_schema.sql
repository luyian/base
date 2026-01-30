-- =============================================
-- 股票数据分析模块 - 数据库表结构
-- 表名前缀：stk_
-- =============================================

-- ----------------------------
-- 1. Token 管理表
-- ----------------------------
DROP TABLE IF EXISTS `stk_api_token`;
CREATE TABLE `stk_api_token` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `token_value` varchar(200) NOT NULL COMMENT 'Token值',
    `token_name` varchar(50) DEFAULT NULL COMMENT 'Token名称/备注',
    `provider` varchar(20) NOT NULL DEFAULT 'itick' COMMENT '服务商',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-作废, 1-正常）',
    `last_used_time` datetime DEFAULT NULL COMMENT '最后使用时间',
    `use_count` int NOT NULL DEFAULT 0 COMMENT '使用次数',
    `daily_limit` int NOT NULL DEFAULT 0 COMMENT '每日限额（0表示无限制）',
    `daily_used` int NOT NULL DEFAULT 0 COMMENT '当日已用次数',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间（null表示永不过期）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_provider_status` (`provider`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='API Token管理表';

-- ----------------------------
-- 2. 数据映射配置表
-- ----------------------------
DROP TABLE IF EXISTS `stk_data_mapping`;
CREATE TABLE `stk_data_mapping` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mapping_code` varchar(50) NOT NULL COMMENT '映射编码（唯一标识）',
    `mapping_name` varchar(100) NOT NULL COMMENT '映射名称',
    `source_type` varchar(50) NOT NULL COMMENT '源数据类型',
    `target_table` varchar(50) NOT NULL COMMENT '目标表名',
    `field_mapping` text NOT NULL COMMENT '字段映射配置（JSON格式）',
    `transform_script` text DEFAULT NULL COMMENT '转换脚本（可选）',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用, 1-启用）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_mapping_code` (`mapping_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据映射配置表';

-- ----------------------------
-- 3. 股票基础信息表
-- ----------------------------
DROP TABLE IF EXISTS `stk_stock_info`;
CREATE TABLE `stk_stock_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `stock_name` varchar(100) NOT NULL COMMENT '股票名称',
    `market` varchar(10) NOT NULL COMMENT '市场（SH-沪市, SZ-深市, HK-港股）',
    `exchange` varchar(20) DEFAULT NULL COMMENT '交易所',
    `currency` varchar(10) DEFAULT NULL COMMENT '交易货币（CNY/HKD）',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-退市, 1-正常）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_stock_code` (`stock_code`),
    INDEX `idx_market` (`market`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='股票基础信息表';

-- ----------------------------
-- 4. 自选股票表
-- ----------------------------
DROP TABLE IF EXISTS `stk_watchlist`;
CREATE TABLE `stk_watchlist` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_user_stock` (`user_id`, `stock_code`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='自选股票表';

-- ----------------------------
-- 5. K线数据表（日K）
-- ----------------------------
DROP TABLE IF EXISTS `stk_kline_daily`;
CREATE TABLE `stk_kline_daily` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `trade_date` date NOT NULL COMMENT '交易日期',
    `open_price` decimal(12,4) DEFAULT NULL COMMENT '开盘价',
    `high_price` decimal(12,4) DEFAULT NULL COMMENT '最高价',
    `low_price` decimal(12,4) DEFAULT NULL COMMENT '最低价',
    `close_price` decimal(12,4) DEFAULT NULL COMMENT '收盘价',
    `volume` bigint DEFAULT NULL COMMENT '成交量',
    `amount` decimal(18,4) DEFAULT NULL COMMENT '成交额',
    `change_rate` decimal(10,4) DEFAULT NULL COMMENT '涨跌幅(%)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_stock_date` (`stock_code`, `trade_date`),
    INDEX `idx_trade_date` (`trade_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='K线数据表（日K）';

-- ----------------------------
-- 初始化数据映射配置
-- ----------------------------
INSERT INTO `stk_data_mapping` (`mapping_code`, `mapping_name`, `source_type`, `target_table`, `field_mapping`, `status`) VALUES
('itick_stock_list', 'iTick股票列表映射', 'itick_stock_list', 'stk_stock_info', '{"mappings":[{"source":"symbol","target":"stock_code","type":"string"},{"source":"name","target":"stock_name","type":"string"},{"source":"exchange","target":"exchange","type":"string"},{"source":"currency","target":"currency","type":"string"}]}', 1),
('itick_kline_daily', 'iTick日K线映射', 'itick_kline', 'stk_kline_daily', '{"mappings":[{"source":"symbol","target":"stock_code","type":"string"},{"source":"timestamp","target":"trade_date","type":"date"},{"source":"open","target":"open_price","type":"decimal","scale":4},{"source":"high","target":"high_price","type":"decimal","scale":4},{"source":"low","target":"low_price","type":"decimal","scale":4},{"source":"close","target":"close_price","type":"decimal","scale":4},{"source":"volume","target":"volume","type":"long"},{"source":"amount","target":"amount","type":"decimal","scale":4}]}', 1);
