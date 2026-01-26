-- ============================
-- 基础管理系统初始化数据脚本
-- ============================

USE `base_system`;

-- ============================
-- 初始化部门数据
-- ============================
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `leader`, `phone`, `email`, `sort`, `status`, `create_by`) VALUES
(1, 0, '总公司', 'ROOT', '管理员', '13800138000', 'admin@example.com', 0, 1, 'system'),
(2, 1, '技术部', 'TECH', '技术总监', '13800138001', 'tech@example.com', 1, 1, 'system'),
(3, 1, '市场部', 'MARKET', '市场总监', '13800138002', 'market@example.com', 2, 1, 'system'),
(4, 1, '财务部', 'FINANCE', '财务总监', '13800138003', 'finance@example.com', 3, 1, 'system'),
(5, 2, '研发组', 'DEV', '研发组长', '13800138004', 'dev@example.com', 1, 1, 'system'),
(6, 2, '测试组', 'TEST', '测试组长', '13800138005', 'test@example.com', 2, 1, 'system');

-- ============================
-- 初始化角色数据
-- ============================
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `data_scope`, `status`, `sort`, `create_by`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1, 1, 0, 'system'),
(2, '系统管理员', 'ADMIN', '拥有系统管理权限', 2, 1, 1, 'system'),
(3, '普通用户', 'USER', '普通用户权限', 4, 1, 2, 'system');

-- ============================
-- 初始化权限/菜单数据
-- ============================
-- 一级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(1, 0, '系统管理', NULL, 1, '/system', NULL, 'Setting', 1, 1, 1, 'system'),
(2, 0, '系统监控', NULL, 1, '/monitor', NULL, 'Monitor', 2, 1, 1, 'system'),
(3, 0, '日志管理', NULL, 1, '/log', NULL, 'Document', 3, 1, 1, 'system');

-- 系统管理子菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(101, 1, '用户管理', 'system:user:list', 2, '/system/user', 'system/User', 'User', 1, 1, 1, 'system'),
(102, 1, '角色管理', 'system:role:list', 2, '/system/role', 'system/Role', 'UserFilled', 2, 1, 1, 'system'),
(103, 1, '菜单管理', 'system:permission:list', 2, '/system/permission', 'system/Permission', 'Menu', 3, 1, 1, 'system'),
(104, 1, '部门管理', 'system:dept:list', 2, '/system/dept', 'system/Department', 'OfficeBuilding', 4, 1, 1, 'system'),
(105, 1, '枚举管理', 'system:enum:list', 2, '/system/enum', 'system/Enum', 'Collection', 5, 1, 1, 'system'),
(106, 1, '参数配置', 'system:config:list', 2, '/system/config', 'system/Config', 'Tools', 6, 1, 1, 'system'),
(107, 1, '通知公告', 'system:notice:list', 2, '/system/notice', 'system/Notice', 'Bell', 7, 1, 1, 'system');

-- 用户管理按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10101, 101, '用户查询', 'system:user:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10102, 101, '用户新增', 'system:user:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10103, 101, '用户编辑', 'system:user:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10104, 101, '用户删除', 'system:user:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10105, 101, '重置密码', 'system:user:resetPwd', 3, NULL, NULL, NULL, 5, 1, 1, 'system'),
(10106, 101, '分配角色', 'system:user:assignRole', 3, NULL, NULL, NULL, 6, 1, 1, 'system');

-- 角色管理按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10201, 102, '角色查询', 'system:role:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10202, 102, '角色新增', 'system:role:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10203, 102, '角色编辑', 'system:role:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10204, 102, '角色删除', 'system:role:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10205, 102, '分配权限', 'system:role:assignPermission', 3, NULL, NULL, NULL, 5, 1, 1, 'system');

-- 菜单管理按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10301, 103, '菜单查询', 'system:permission:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10302, 103, '菜单新增', 'system:permission:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10303, 103, '菜单编辑', 'system:permission:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10304, 103, '菜单删除', 'system:permission:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system');

-- 部门管理按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10401, 104, '部门查询', 'system:dept:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10402, 104, '部门新增', 'system:dept:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10403, 104, '部门编辑', 'system:dept:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10404, 104, '部门删除', 'system:dept:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system');

-- 枚举管理按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10501, 105, '枚举查询', 'system:enum:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10502, 105, '枚举新增', 'system:enum:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10503, 105, '枚举编辑', 'system:enum:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10504, 105, '枚举删除', 'system:enum:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system');

-- 参数配置按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10601, 106, '参数查询', 'system:config:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10602, 106, '参数新增', 'system:config:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10603, 106, '参数编辑', 'system:config:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10604, 106, '参数删除', 'system:config:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10605, 106, '刷新缓存', 'system:config:refresh', 3, NULL, NULL, NULL, 5, 1, 1, 'system');

-- 通知公告按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10701, 107, '通知查询', 'system:notice:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10702, 107, '通知新增', 'system:notice:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10703, 107, '通知编辑', 'system:notice:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10704, 107, '通知删除', 'system:notice:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10705, 107, '通知发布', 'system:notice:publish', 3, NULL, NULL, NULL, 5, 1, 1, 'system');

-- 系统监控子菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(201, 2, '服务器监控', 'monitor:server:list', 2, '/monitor/server', 'monitor/Server', 'Cpu', 1, 1, 1, 'system'),
(202, 2, '缓存监控', 'monitor:cache:list', 2, '/monitor/cache', 'monitor/Cache', 'Coin', 2, 1, 1, 'system');

-- 服务器监控按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20101, 201, '服务器查询', 'system:monitor:server', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 缓存监控按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(20201, 202, '缓存查询', 'system:monitor:cache', 3, NULL, NULL, NULL, 1, 1, 1, 'system');

-- 日志管理子菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(301, 3, '操作日志', 'log:operation:list', 2, '/log/operation', 'system/OperationLog', 'EditPen', 1, 1, 1, 'system'),
(302, 3, '登录日志', 'log:login:list', 2, '/log/login', 'system/LoginLog', 'Unlock', 2, 1, 1, 'system');

-- 操作日志按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(30101, 301, '日志查询', 'log:operation:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(30102, 301, '日志删除', 'log:operation:delete', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(30103, 301, '日志清空', 'log:operation:clear', 3, NULL, NULL, NULL, 3, 1, 1, 'system');

-- 登录日志按钮
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(30201, 302, '日志查询', 'log:login:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(30202, 302, '日志删除', 'log:login:delete', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(30203, 302, '日志清空', 'log:login:clear', 3, NULL, NULL, NULL, 3, 1, 1, 'system');

-- ============================
-- 初始化用户数据（密码：admin123）
-- ============================
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `gender`, `dept_id`, `status`, `create_by`) VALUES
(1, 'admin', '$2a$10$dptAyYkp3nrMNff2X7fT1uZSVS6I7KxdCyEI0MhD/9yWfe57togM.', '超级管理员', 'admin@example.com', '13800138000', 1, 1, 1, 'system'),
(2, 'test', '$2a$10$dptAyYkp3nrMNff2X7fT1uZSVS6I7KxdCyEI0MhD/9yWfe57togM.', '测试用户', 'test@example.com', '13800138001', 1, 5, 1, 'system');

-- ============================
-- 初始化用户角色关联数据
-- ============================
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 3);

-- ============================
-- 初始化角色权限关联数据（超级管理员拥有所有权限）
-- ============================
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- ============================
-- 初始化枚举数据
-- ============================
-- 性别枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_code`, `enum_value`, `description`, `sort`, `status`, `create_by`) VALUES
('gender', '0', '未知', '性别未知', 0, 1, 'system'),
('gender', '1', '男', '男性', 1, 1, 'system'),
('gender', '2', '女', '女性', 2, 1, 'system');

-- 状态枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_code`, `enum_value`, `description`, `sort`, `status`, `create_by`) VALUES
('status', '0', '禁用', '禁用状态', 0, 1, 'system'),
('status', '1', '启用', '启用状态', 1, 1, 'system');

-- 通知类型枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_code`, `enum_value`, `description`, `sort`, `status`, `create_by`) VALUES
('notice_type', '1', '通知', '系统通知', 1, 1, 'system'),
('notice_type', '2', '公告', '系统公告', 2, 1, 'system');

-- 通知级别枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_code`, `enum_value`, `description`, `sort`, `status`, `create_by`) VALUES
('notice_level', '1', '普通', '普通级别', 1, 1, 'system'),
('notice_level', '2', '重要', '重要级别', 2, 1, 'system'),
('notice_level', '3', '紧急', '紧急级别', 3, 1, 'system');

-- 数据权限范围枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_code`, `enum_value`, `description`, `sort`, `status`, `create_by`) VALUES
('data_scope', '1', '全部数据', '可以查看所有数据', 1, 1, 'system'),
('data_scope', '2', '本部门及以下', '可以查看本部门及下级部门数据', 2, 1, 'system'),
('data_scope', '3', '本部门', '只能查看本部门数据', 3, 1, 'system'),
('data_scope', '4', '仅本人', '只能查看自己的数据', 4, 1, 'system'),
('data_scope', '5', '自定义', '自定义数据权限', 5, 1, 'system');

-- ============================
-- 初始化全局变量数据
-- ============================
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_name`, `description`, `type`, `status`, `create_by`) VALUES
('system.name', '基础管理系统', '系统名称', '系统的名称', 'string', 1, 'system'),
('system.version', '1.0.0', '系统版本', '系统的版本号', 'string', 1, 'system'),
('user.password.maxRetryCount', '5', '密码最大重试次数', '用户登录密码最大重试次数', 'number', 1, 'system'),
('user.password.lockTime', '10', '账号锁定时间', '密码输错后账号锁定时间（分钟）', 'number', 1, 'system'),
('captcha.enabled', 'true', '验证码开关', '是否开启验证码功能', 'boolean', 1, 'system'),
('captcha.type', 'math', '验证码类型', '验证码类型（math：数字运算，char：字符验证）', 'string', 1, 'system');

-- 行政区划管理菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(108, 1, '行政区划', 'system:region:list', 2, '/system/region', 'system/Region', 'Location', 8, 1, 1, 'system');

-- 行政区划管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10801, 108, '区划查询', 'system:region:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10802, 108, '区划新增', 'system:region:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10803, 108, '区划编辑', 'system:region:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10804, 108, '区划删除', 'system:region:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10805, 108, '区划导入', 'system:region:import', 3, NULL, NULL, NULL, 5, 1, 1, 'system');

-- ============================
-- 初始化通知公告数据
-- ============================
INSERT INTO `sys_notice` (`title`, `content`, `type`, `level`, `status`, `publish_time`, `create_by`) VALUES
('欢迎使用基础管理系统', '欢迎使用基础管理系统，这是一个功能完善的后台管理系统。', 1, 1, 1, NOW(), 'system'),
('系统维护通知', '系统将于本周六凌晨2:00-4:00进行维护升级，期间可能无法访问，请提前做好准备。', 2, 2, 1, NOW(), 'system');
