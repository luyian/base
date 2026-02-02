-- =============================================
-- 添加 Token 失败计数字段
-- 用于记录 Token 连续失败次数，超过3次自动作废
-- =============================================

ALTER TABLE `stk_api_token` ADD COLUMN `fail_count` int NOT NULL DEFAULT 0 COMMENT '连续失败次数' AFTER `daily_used`;
