-- =============================================
-- 股票数据分析模块 - 菜单和权限初始化
-- 执行前请确保已执行 stock_schema.sql
-- =============================================

-- ----------------------------
-- 1. 股票管理一级菜单
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(4, 0, '股票管理', NULL, 1, '/stock', NULL, 'TrendCharts', 4, 1, 1, '股票数据分析模块', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 2. 股票管理二级菜单
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(401, 4, '股票列表', 'stock:info:list', 2, '/stock', 'stock/index', 'List', 1, 1, 1, '股票列表页面', NOW(), 'system', NOW(), NULL, 0),
(402, 4, '自选股票', 'stock:watchlist:list', 2, '/stock/watchlist', 'stock/watchlist/index', 'Star', 2, 1, 1, '自选股票管理', NOW(), 'system', NOW(), NULL, 0),
(403, 4, 'Token管理', 'stock:token:list', 2, '/stock/token', 'stock/token/index', 'Key', 3, 1, 1, 'API Token管理', NOW(), 'system', NOW(), NULL, 0),
(404, 4, '数据映射', 'stock:mapping:list', 2, '/stock/mapping', 'stock/mapping/index', 'Connection', 4, 1, 1, '数据映射配置', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 3. 股票列表按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40101, 401, '股票查询', 'stock:info:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40102, 401, 'K线查询', 'stock:kline:query', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40103, 401, '数据同步', 'stock:sync:execute', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 4. 自选股票按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40201, 402, '自选查询', 'stock:watchlist:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40202, 402, '添加自选', 'stock:watchlist:add', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40203, 402, '编辑自选', 'stock:watchlist:edit', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40204, 402, '移除自选', 'stock:watchlist:delete', 3, NULL, NULL, NULL, 4, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 5. Token管理按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40301, 403, 'Token查询', 'stock:token:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40302, 403, 'Token新增', 'stock:token:add', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40303, 403, 'Token编辑', 'stock:token:edit', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40304, 403, 'Token删除', 'stock:token:delete', 3, NULL, NULL, NULL, 4, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 6. 数据映射按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40401, 404, '映射查询', 'stock:mapping:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40402, 404, '映射新增', 'stock:mapping:add', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40403, 404, '映射编辑', 'stock:mapping:edit', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(40404, 404, '映射删除', 'stock:mapping:delete', 3, NULL, NULL, NULL, 4, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 7. 为超级管理员分配股票模块所有权限
-- ----------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 4),
(1, 401), (1, 402), (1, 403), (1, 404),
(1, 40101), (1, 40102), (1, 40103),
(1, 40201), (1, 40202), (1, 40203), (1, 40204),
(1, 40301), (1, 40302), (1, 40303), (1, 40304),
(1, 40401), (1, 40402), (1, 40403), (1, 40404);

-- ----------------------------
-- 验证数据
-- ----------------------------
SELECT '股票模块权限初始化完成' AS message;
SELECT COUNT(*) AS '股票模块权限数量' FROM sys_permission WHERE id >= 4 AND id < 500;
