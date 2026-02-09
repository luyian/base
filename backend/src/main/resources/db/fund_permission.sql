-- =============================================
-- 基金估值模块 - 菜单和权限初始化
-- 执行前请确保已执行 fund_schema.sql
-- =============================================

-- ----------------------------
-- 1. 基金估值二级菜单（挂在股票管理下）
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(407, 4, '基金估值', 'stock:fund:list', 2, '/stock/fund', 'stock/fund/index', 'PieChart', 7, 1, 1, '基金估值管理', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 2. 基金估值按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40701, 407, '基金查询', 'stock:fund:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询基金列表', NOW(), 'system', NOW(), NULL, 0),
(40702, 407, '基金新增', 'stock:fund:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增基金配置', NOW(), 'system', NOW(), NULL, 0),
(40703, 407, '基金编辑', 'stock:fund:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑基金配置', NOW(), 'system', NOW(), NULL, 0),
(40704, 407, '基金删除', 'stock:fund:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除基金配置', NOW(), 'system', NOW(), NULL, 0),
(40705, 407, '估值查询', 'stock:fund:valuation', 3, NULL, NULL, NULL, 5, 1, 1, '查询基金实时估值', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 3. 为超级管理员分配基金模块所有权限
-- ----------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 407),
(1, 40701), (1, 40702), (1, 40703), (1, 40704), (1, 40705);

-- ----------------------------
-- 验证数据
-- ----------------------------
SELECT '基金估值模块权限初始化完成' AS message;
SELECT * FROM sys_permission WHERE id = 407;
SELECT * FROM sys_permission WHERE parent_id = 407;


-- =============================================
-- 同步失败记录模块 - 按钮权限（挂在股票列表下）
-- =============================================

-- ----------------------------
-- 1. 同步失败记录按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(40104, 401, '失败记录查询', 'stock:sync:failure:query', 3, NULL, NULL, NULL, 4, 1, 1, '查询同步失败记录列表', NOW(), 'system', NOW(), NULL, 0),
(40105, 401, '失败数据补拉', 'stock:sync:failure:retry', 3, NULL, NULL, NULL, 5, 1, 1, '补拉同步失败的数据', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 2. 为超级管理员分配失败记录权限
-- ----------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 40104), (1, 40105);

-- ----------------------------
-- 验证数据
-- ----------------------------
SELECT '同步失败记录权限初始化完成' AS message;
SELECT * FROM sys_permission WHERE id IN (40104, 40105);
