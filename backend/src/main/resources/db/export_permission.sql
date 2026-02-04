-- =============================================
-- 通用导出功能模块 - 菜单和权限初始化
-- 执行前请确保已执行 export_schema.sql
-- =============================================

-- ----------------------------
-- 1. 导出管理二级菜单（放在系统管理下）
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(109, 1, '导出配置', 'system:export:config:list', 2, '/system/export/config', 'system/ExportConfig', 'Document', 9, 1, 1, '导出配置管理', NOW(), 'system', NOW(), NULL, 0),
(110, 1, '导出任务', 'system:export:task:list', 2, '/system/export/task', 'system/ExportTask', 'Download', 10, 1, 1, '导出任务管理', NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 2. 导出配置按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(10901, 109, '配置查询', 'system:export:config:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(10902, 109, '配置新增', 'system:export:config:add', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(10903, 109, '配置编辑', 'system:export:config:edit', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(10904, 109, '配置删除', 'system:export:config:delete', 3, NULL, NULL, NULL, 4, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 3. 导出任务按钮权限
-- ----------------------------
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(11001, 110, '任务查询', 'system:export:task:query', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(11002, 110, '任务新增', 'system:export:task:add', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(11003, 110, '任务编辑', 'system:export:task:edit', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(11004, 110, '任务删除', 'system:export:task:delete', 3, NULL, NULL, NULL, 4, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- ----------------------------
-- 4. 为超级管理员分配导出模块所有权限
-- ----------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 109), (1, 110),
(1, 10901), (1, 10902), (1, 10903), (1, 10904),
(1, 11001), (1, 11002), (1, 11003), (1, 11004);

-- ----------------------------
-- 验证数据
-- ----------------------------
SELECT '导出模块权限初始化完成' AS message;
SELECT COUNT(*) AS '导出模块权限数量' FROM sys_permission WHERE id IN (109, 110, 10901, 10902, 10903, 10904, 11001, 11002, 11003, 11004);
