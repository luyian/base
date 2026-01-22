-- ============================
-- 添加系统监控模块菜单和权限
-- ============================

-- 系统监控子菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(201, 2, '服务器监控', 'monitor:server:list', 2, '/monitor/server', 'monitor/server/index', 'el-icon-monitor', 1, 1, 1, 'system'),
(202, 2, '缓存监控', 'monitor:cache:list', 2, '/monitor/cache', 'monitor/cache/index', 'el-icon-coin', 2, 1, 1, 'system');

-- 服务器监控按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20101, 201, '服务器查询', 'monitor:server:view', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 缓存监控按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20201, 202, '缓存查询', 'monitor:cache:view', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 为超级管理员角色添加新权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 201),
(1, 202),
(1, 20101),
(1, 20201);
