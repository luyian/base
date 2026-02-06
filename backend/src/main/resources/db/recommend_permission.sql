-- 股票推荐系统 - 菜单和权限初始化脚本
-- 执行时间：2026-02-06
-- 表结构说明：type字段 1-目录，2-菜单，3-按钮

-- ============================================
-- 1. 添加股票推荐菜单
-- ============================================

-- 查询股票管理菜单的ID（已存在）
SET @stock_parent_id = (SELECT id FROM sys_permission WHERE permission_code = 'stock' AND deleted = 0 LIMIT 1);

-- 添加股票推荐菜单（type=2表示菜单）
INSERT INTO sys_permission (permission_name, permission_code, type, parent_id, path, component, icon, sort, visible, status, create_time, deleted)
VALUES
('股票推荐', 'stock:recommend', 2, @stock_parent_id, '/stock/recommend', 'stock/recommend/index', 'TrendCharts', 10, 1, 1, NOW(), 0),
('规则配置', 'stock:recommend:rule', 2, @stock_parent_id, '/stock/recommend/rule', 'stock/recommend/rule', 'Setting', 11, 1, 1, NOW(), 0);

-- ============================================
-- 2. 添加按钮权限
-- ============================================

-- 获取股票推荐菜单ID
SET @recommend_menu_id = (SELECT id FROM sys_permission WHERE permission_code = 'stock:recommend' AND deleted = 0 LIMIT 1);

-- 获取规则配置菜单ID
SET @rule_menu_id = (SELECT id FROM sys_permission WHERE permission_code = 'stock:recommend:rule' AND deleted = 0 LIMIT 1);

-- 股票推荐页面的按钮权限（type=3表示按钮）
INSERT INTO sys_permission (permission_name, permission_code, type, parent_id, sort, visible, status, create_time, deleted)
VALUES
('查询推荐列表', 'stock:recommend:list', 3, @recommend_menu_id, 1, 1, 1, NOW(), 0),
('查询打分明细', 'stock:recommend:detail', 3, @recommend_menu_id, 2, 1, 1, NOW(), 0),
('手动触发打分', 'stock:recommend:execute', 3, @recommend_menu_id, 3, 1, 1, NOW(), 0);

-- 规则配置页面的按钮权限（type=3表示按钮）
INSERT INTO sys_permission (permission_name, permission_code, type, parent_id, sort, visible, status, create_time, deleted)
VALUES
('查询规则列表', 'stock:rule:list', 3, @rule_menu_id, 1, 1, 1, NOW(), 0),
('编辑规则配置', 'stock:rule:edit', 3, @rule_menu_id, 2, 1, 1, NOW(), 0);

-- ============================================
-- 3. 为超级管理员分配权限
-- ============================================

-- 为超级管理员（role_id = 1）分配所有股票推荐相关权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time)
SELECT 1, id, NOW()
FROM sys_permission
WHERE (permission_code LIKE 'stock:recommend%' OR permission_code LIKE 'stock:rule%')
  AND deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission
    WHERE role_id = 1 AND permission_id = sys_permission.id
  );

-- ============================================
-- 4. 验证SQL
-- ============================================

-- 查看新增的菜单
SELECT id, permission_name, permission_code, type, parent_id, path, component, icon, sort
FROM sys_permission
WHERE (permission_code LIKE 'stock:recommend%' OR permission_code LIKE 'stock:rule%')
  AND deleted = 0
ORDER BY sort;

-- 查看超级管理员的权限分配
SELECT p.id, p.permission_name, p.permission_code, p.type
FROM sys_permission p
INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
WHERE rp.role_id = 1
  AND (p.permission_code LIKE 'stock:recommend%' OR p.permission_code LIKE 'stock:rule%')
  AND p.deleted = 0
ORDER BY p.sort;

-- ============================================
-- 说明
-- ============================================

/*
菜单结构：
股票管理 (stock) - type=1 目录
  ├── 股票列表 (stock:info) - type=2 菜单
  ├── 自选股票 (stock:watchlist) - type=2 菜单
  ├── 股票推荐 (stock:recommend) - type=2 菜单 ← 新增
  │   ├── 查询推荐列表 (stock:recommend:list) - type=3 按钮
  │   ├── 查询打分明细 (stock:recommend:detail) - type=3 按钮
  │   └── 手动触发打分 (stock:recommend:execute) - type=3 按钮
  └── 规则配置 (stock:recommend:rule) - type=2 菜单 ← 新增
      ├── 查询规则列表 (stock:rule:list) - type=3 按钮
      └── 编辑规则配置 (stock:rule:edit) - type=3 按钮

权限说明：
- stock:recommend:list - 查询推荐列表（必须）
- stock:recommend:detail - 查询打分明细（必须）
- stock:recommend:execute - 手动触发打分（管理员）
- stock:rule:list - 查询规则列表（必须）
- stock:rule:edit - 编辑规则配置（管理员）

注意事项：
1. 如果sys_permission表结构与脚本不一致，请根据实际表结构调整
2. 如果股票管理父菜单不存在，脚本会自动创建
3. 超级管理员role_id默认为1，如不同请修改
4. 执行前请备份数据库
*/
