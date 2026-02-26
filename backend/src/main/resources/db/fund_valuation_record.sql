-- 基金估值记录表：每天15:30后获取的估值持久化存储
CREATE TABLE `stk_fund_valuation_record` (
    `id`                        bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fund_id`                   bigint NOT NULL COMMENT '基金ID',
    `trade_date`                date NOT NULL COMMENT '交易日期',
    `estimated_change_percent`  decimal(10, 4) NULL DEFAULT NULL COMMENT '估算涨跌幅(%)',
    `holding_count`             int NULL DEFAULT NULL COMMENT '持仓数量',
    `success_count`             int NULL DEFAULT NULL COMMENT '成功获取报价数量',
    `fail_count`                int NULL DEFAULT NULL COMMENT '失败获取报价数量',
    `total_weight`              decimal(10, 4) NULL DEFAULT NULL COMMENT '总权重(%)',
    `quotes_json`               text NULL COMMENT '股票报价明细（JSON格式）',
    `create_time`               datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fund_date` (`fund_id`, `trade_date`),
    KEY `idx_trade_date` (`trade_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '基金估值记录表';
