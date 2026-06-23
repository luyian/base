-- ----------------------------
-- 基金自选表新增排序字段（支持首页「我的自选」置顶）
-- 执行前请先 select 确认表结构，再执行变更
-- ----------------------------
ALTER TABLE `stk_fund_watchlist`
  ADD COLUMN `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号（值越小越靠前，置顶时取当前用户最小值减1）' AFTER `user_id`;
