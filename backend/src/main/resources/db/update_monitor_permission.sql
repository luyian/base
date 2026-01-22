-- ============================
-- 补充监控模块权限数据
-- 执行此脚本前请确保已连接到 base_system 数据库
-- ============================

USE `base_system`;

-- 添加系统监控子菜单（如果不存在）
INSERT IGNORE INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(201, 2, '服务器监控', 'monitor:server:list', 2, '/monitor/server', 'monitor/Server', 'Cpu', 1, 1, 1, 'system'),
(202, 2, '缓存监控', 'monitor:cache:list', 2, '/monitor/cache', 'monitor/Cache', 'Coin', 2, 1, 1, 'system');

-- 添加服务器监控按钮权限
INSERT IGNORE INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20101, 201, '服务器查询', 'system:monitor:server', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 添加缓存监控按钮权限
INSERT IGNORE INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20201, 202, '缓存查询', 'system:monitor:cache', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 为超级管理员角色添加新权限（如果不存在）
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 201),
(1, 202),
(1, 20101),
(1, 20201);

-- 验证权限数据
SELECT '监控模块权限数据:' AS '说明';
SELECT id, permission_name, permission_code, path, component FROM sys_permission WHERE parent_id = 2;

SELECT '超级管理员角色权限数量:' AS '说明';
SELECT COUNT(*) AS '权限数量' FROM sys_role_permission WHERE role_id = 1;
