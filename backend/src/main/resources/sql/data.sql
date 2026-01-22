-- =============================================
-- 数据库初始化数据脚本
-- 数据库：base
-- 版本：1.0
-- 创建时间：2026-01-14
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 部门数据（sys_dept）
-- =============================================
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `leader`, `phone`, `email`, `status`, `sort`, `remark`, `create_time`) VALUES
(1, 0, '总公司', 'HEAD_OFFICE', '张三', '13800138000', 'zhangsan@example.com', 1, 1, '顶级部门', NOW()),
(2, 1, '技术部', 'TECH_DEPT', '李四', '13800138001', 'lisi@example.com', 1, 1, '技术研发部门', NOW()),
(3, 1, '市场部', 'MARKET_DEPT', '王五', '13800138002', 'wangwu@example.com', 1, 2, '市场营销部门', NOW()),
(4, 1, '财务部', 'FINANCE_DEPT', '赵六', '13800138003', 'zhaoliu@example.com', 1, 3, '财务管理部门', NOW()),
(5, 1, '人力资源部', 'HR_DEPT', '孙七', '13800138004', 'sunqi@example.com', 1, 4, '人力资源管理部门', NOW()),
(6, 2, '研发一组', 'DEV_GROUP_1', '周八', '13800138005', 'zhouba@example.com', 1, 1, '研发一组', NOW()),
(7, 2, '研发二组', 'DEV_GROUP_2', '吴九', '13800138006', 'wujiu@example.com', 1, 2, '研发二组', NOW());

-- =============================================
-- 2. 角色数据（sys_role）
-- =============================================
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `data_scope`, `status`, `sort`, `remark`, `create_time`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', 1, 1, 1, '拥有系统所有权限，全部数据权限', NOW()),
(2, '系统管理员', 'SYSTEM_ADMIN', 4, 1, 2, '拥有系统管理权限，本部门及以下数据权限', NOW()),
(3, '普通用户', 'USER', 5, 1, 3, '普通用户角色，仅本人数据权限', NOW());

-- =============================================
-- 3. 权限/菜单数据（sys_permission）
-- =============================================
-- 一级菜单：系统管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(1, 0, '系统管理', 'system', 1, '/system', 'Layout', 'Setting', 1, 1, 1, '系统管理菜单', NOW());

-- 二级菜单：用户管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(2, 1, '用户管理', 'system:user', 1, '/system/user', 'system/user/index', 'User', 1, 1, 1, '用户管理菜单', NOW());

-- 用户管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(3, 2, '用户查询', 'system:user:query', 2, NULL, NULL, NULL, 1, 1, 1, '用户查询按钮', NOW()),
(4, 2, '用户新增', 'system:user:add', 2, NULL, NULL, NULL, 1, 1, 2, '用户新增按钮', NOW()),
(5, 2, '用户修改', 'system:user:edit', 2, NULL, NULL, NULL, 1, 1, 3, '用户修改按钮', NOW()),
(6, 2, '用户删除', 'system:user:delete', 2, NULL, NULL, NULL, 1, 1, 4, '用户删除按钮', NOW());

-- 二级菜单：角色管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(7, 1, '角色管理', 'system:role', 1, '/system/role', 'system/role/index', 'Avatar', 1, 1, 2, '角色管理菜单', NOW());

-- 角色管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(8, 7, '角色查询', 'system:role:query', 2, NULL, NULL, NULL, 1, 1, 1, '角色查询按钮', NOW()),
(9, 7, '角色新增', 'system:role:add', 2, NULL, NULL, NULL, 1, 1, 2, '角色新增按钮', NOW()),
(10, 7, '角色修改', 'system:role:edit', 2, NULL, NULL, NULL, 1, 1, 3, '角色修改按钮', NOW()),
(11, 7, '角色删除', 'system:role:delete', 2, NULL, NULL, NULL, 1, 1, 4, '角色删除按钮', NOW()),
(12, 7, '分配权限', 'system:role:assign', 2, NULL, NULL, NULL, 1, 1, 5, 'OW());

-- 二级菜单：权限管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(13, 1, '权限管理', 'system:permission', 1, '/system/permission', 'system/permission/index', 'Lock', 1, 1, 3, '权限管理菜单', NOW());

-- 权限管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(14, 13, '权限查询', 'system:permission:query', 2, NULL, NULL, NULL, 1, 1, 1, '权限查询按钮', NOW()),
(15, 13, '权限新, 'system:permission:add', 2, NULL, NULL, NULL, 1, 1, 2, '权限新增按钮', NOW()),
(16, 13, '权限修改', 'system:permission:edit', 2, NULL, NULL, NULL, 1, 1, 3, '权限修改按钮', NOW()),
(17, 13, '权限删除', 'system:permission:delete', 2, NULL, NULL, NULL, 1, 1, 4, '权限删除按钮', NOW());

-- 二级菜单：部门管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(18, 1, '部门管理', 'system:dept', 1, '/system/dept', 'system/dept/index', 'OfficeBuilding', 1, 1, 4'部门管理菜单', NOW());

-- 部门管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(19, 18, '部门查询', 'system:dept:query', 2, NULL, NULL, NULL, 1, 1, 1, '部门查询按钮', NOW()),
(20, 18, '部门新增', 'system:dept:add', 2, NULL, NULL, NULL, 1, 1, 2, '部门新增按钮', NOW()),
(21, 18, '部门修改', 'system:dept:edit', 2, NULL, NULL, NULL, 1, 1, 3, '部门修改按钮', NOW()),
(22, 18, '部门删除', 'system:dept:delete', 2, NULL, NULL, NULL, 1, 1, 4, '部门删除按钮', NOW());

-- 二级菜单：枚举管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(23, 1, '枚举管理', 'system:enum', 1, '/system/enum', 'system/enum/index', 'List', 1, 1, 5, '枚举管理菜单', NOW());

-- 枚举管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(24, 23, '枚举查询', 'system:enum:query', 2, NULL, NULL, NULL, 1, 1, 1, '枚举查询按钮', NOW()),
(25, 23举新增', 'system:enum:add', 2, NULL, NUL, 1, 2, '枚举新增按钮', NOW()),
(26, 23, '枚举修改', 'system:enum:edit', 2, NULL, NULL, NULL, 1, 1, 3, '枚举修改按钮', NOW()),
(27, 23, '枚举删除', 'system:enum:delete', 2, NULL, NULL, NULL, 1, 1, 4, '枚举删除按钮', NOW());

-- 二级菜单：全局变量管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(28, 1, '全局变量管理', 'system:config', 1, '/system/config', 'system/config/index', 'Tools', 1, 1, 6, '全局变量管理菜单', NOW());

-- 全局变量管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(29, 28, '变量查询', 'system:config:query', 2, NULL, NULL, NULL, 1, 1, 1, '变量查询按钮', NOW()),
(30, 28, '变量新增', 'system:config:add', 2, NULL, NULL, NULL, 1, 1, 2, '变量新增按钮', NOW()),
(31, 28, '变量修改', 'system:config:edit', 2, NULL, NULL, NULL, 1, 1, 3, '变量修改按钮', NOW()),
(32, 28, '变量删除', 'system:config:delete', 2, NULL, NULL, NULL, 1, 1, 4, '变量删除按钮', NOW());

-- 二级菜单：通知公告管理
INSERT INTO `sys_permissio `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(33, 1, '通知公告管理', 'system:notice', 1, '/system/notice', 'system/notice/index', 'Bell', 1, 1, 7, '通知公告管理菜单', NOW());

-- 通知公告管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(34, 33, '通知查询', 'system:notice:query', 2, NULL, NULL, NULL, 1, 1, 1, '通知查询按钮', NOW()),
(35, 33, '通知新增', 'system:notice:add', 2, NULL, NULL, NULL, 1, 1, 2, '通知新增按钮', NOW()),
(36, 33, '通知修改', 'system:notice:edit', 2, NULL, NULL, NULL, 1, 1, 3, '通知修改按钮', NOW()),
(37, 33, '通知删除', 'system:notice:delete', 2, NULL, NULL, NULL, 1, 1, 4, '通知删除按钮', NOW()),
(38, 33, '通知发布', 'system:notice:publish', 2, NULL, NULL, NULL, 1, 1, 5, '通知发布按钮', NOW());

-- 一级菜单：日志管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(39, 0, '日志管理', 'log', 1, '/log', 'Layout', 'Document', 1, 1, 2, '日志管理菜单', NOW());

-- 二级菜单：操作日志
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(40, 39, '操作日志', 'log:operation', 1, '/log/operation', 'log/operation/index', 'EditPen', 1, 1, 1, '操作日志菜单', NOW());

-- 操作日志按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(41, 40, '日志查询', 'log:operation:query', 2, NULL, NULL, NULL, 1, 1, 1, '日志查询按钮', NOW()),
(42, 40, '日志删除', 'log:operation:delete', 2, NULL, NULL, NULL, 1, 1, 2, '日志删除按钮', NOW()),
(43, 40, '日志清空', 'log:operation:clear', 2, NULL, NULL, NULL, 1, 1, 3, '日志清空按钮', NOW());

-- 二级菜单：登录日志
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(44, 39, '登录日志', 'log:login', 1, '/log/login', 'log/login/index', 'Key', 1, 1, 2, '登录日志菜单', NOW());

-- 登录日志按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(45, 44, '日志查询', 'log:login:query', 2, NULL, NULL, NULL, 1, 1, 1, '日志查询按钮', NOW()),
(46, 44, '日志删除', 'log:login:delete', 2, NULL, NULL, NULL, 1, 1, 2, '日志删除按钮', NOW()),
(47, 44, '日志清空', 'log:login:clear', 2, NULL, NULL, NULL, 1, 1, 3, '日志清空按钮', NOW());

-- 一级菜单：系统监控
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(48, 0, '系统监控', 'monitor', 1, '/monitor', 'Layout', 'Monitor', 1, 1, 3, '系统监控菜单', NOW());

-- 二级菜单：服务器监控
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(49, 48, '服务器监控', 'monitor:server', 1, '/monitor/server', 'monitor/server/index', 'Cpu', 1, 1, 1, '服务器监控菜单', NOW());

-- 二级菜单：缓存监控
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(50, 48, '缓存监控', 'monitor:cache', 1, '/monitor/cache', 'monitor/cache/index', 'Coin', 1, 1, 2, '缓存监控菜单', NOW());

-- 一级菜单：我的通知
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `route_path`, `component_path`, `icon`, `visible`, `status`, `sort`, `remark`, `create_time`) VALUES
(51, 0, '我的通知', 'my:notice', 1, '/my/notice', 'my/notice/index', 'Message', 1, 1, 4, '我的通知菜单', NOW());

-- =============================================
-- 4. 用户数据（sys_user）
-- =============================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `gender`, `avatar`, `dept_id`, `status`, `remark`, `create_time`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin@example.com', '13800138000', 1, NULL, 1, 1, '系统超级管理员账号', NOW()),
(2, 'test', '$2a$10$VWdkF5gKjY8JqVkYd8b8/.QqVJHJQqKqY5qF5qF5qF5qF5qF5qF5q', '测试用户', 'test@example.com', '13800138888', 2, NULL, 6, 1, '测试用户账号', NOW());

-- =============================================
-- 5. 用户角色关联（sys_user_role）
-- =============================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_time`) VALUES
(1, 1, NOW()),
(2, 3, NOW());

-- =============================================
-- 6. 角色权限关联（sys_role_permission）
-- =============================================
-- 超级管理员拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
(1, 1, NOW()), (1, 2, NOW()), (1, 3, NOW()), (1, 4, NOW()), (1, 5, NOW()), (1, 6, NOW()),
(1, 7, NOW()), (1, 8, NOW()), (1, 9, NOW()), (1, 10, NOW()), (1, 11, NOW()), (1, 12, NOW()),
(1, 13, NOW()), (1, 14, NOW()), (1, 15, NOW()), (1, 16, NOW()), (1, 17, NOW()),
(1, 18, NOW()), (1, 19, NOW()), (1, 20, NOW()), (1, 21, NOW()), (1, 22, NOW()),
(1, 23, NOW()), (1, 24, NOW()), (1, 25, 1, 26, NOW()), (1, 27, NOW()),
(1, 28, NOW()), (1, 29, NOW()), (1, 30, NOW()), (1, 31, NOW()), (1, 32, NOW()),
(1, 33, NOW()), (1, 34, NOW()), (1, 35, NOW()), (1, 36, NOW()), (1, 37, NOW()), (1, 38, NOW()),
(1, 39, NOW()), (1, 40, NOW()), (1, 41, NOW()), (1, 42, NOW()), (1, 43, NOW()),
(1, 44, NOW()), (1, 45, NOW()), (1, 46, NOW()), (1, 47, NOW()),
(1, 48, NOW()), (1, 49, NOW()), (1, 50, NOW()),
(1, 51, NOW());

-- 系统管理员拥有部分权限（不包括敏感操作）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
(2, 1, NOW()), (2, 2, NOW()), (2, 3, NOW()), (2, 4, NOW()), (2, 5, NOW()),
(2, 7, NOW()), (2, 8, NOW()), (2, 9, NOW()), (2, 10, NOW()),
(2, 13, NOW()), (2, 14, NOW()),
(2, 18, NOW()), (2, 19, NOW()), (2, 20, NOW()), (2, 21, NOW()),
(2, 23, NOW()), (2, 24, NOW()),
(2, 28, NOW()), (2, 29, NOW()),
(2, 33, NOW()), (2, 34, NOW()), (2, 35, NOW()), (2, 36, NOW()), (2, 38, NOW()),
(2, 39, NOW()), (2, 40, NOW()), (2, 41, NOW()),
(2, 44, NOW()), (2, 45, NOW()),
(2, 48, NOW()), (2, 49, NOW()), (2, 50, NOW()),
(2, 51, NOW());

-- 普通用户只有查看权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
(3, 1, NOW()), (3, 2, NOW()), (3, 3, NOW()),
)), (3, 19, NOW()),
(3, 33, NOW()), (3, 34, NOW()),
(3, 39, NOW()), (3, 40, NOW()), (3, 41, NOW()),
(3, 44, NOW()), (3, 45, NOW()),
(3, 48, NOW()), (3, 49, NOW()), (3, 50, NOW()),
(3, 51, NOW());

-- =============================================
-- 7. 枚举数据（sys_enum）
-- =============================================
-- 性别枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_value`, `enum_label`, `sort`, `status`, `remark`, `create_time`) VALUES
('gender', '0', '未知', 1, 1, '性别未知', NOW()),
('gender', '1', '男', 2, 1, '男性', NOW()),
('gender', '2', '女', 3, 1, '女性', NOW());

-- 状态枚举
INSERNTO `sys_enum` (`enum_type`, `enum_value`, `enum_label`, `sort`, `status`, `remark`, `create_time`) VALUES
('status', '0', '禁用', 1, 1, '禁用状态', NOW()),
('status', '1', '正常', 2, 1, '正常状态', NOW());

-- 通知类型枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_value`, `enum_label`, `sort`, `status`, `remark`, `create_time`) VALUES
('notice_type', '1', '通知', 1, 1, '通知类型', NOW()),
('notice_type', '2', '公告', 2, 1, '公告类型', NOW());

-- 通知级别枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_value`, `enum_label`, `sort`, `status`, `remark`, `create_time`) VALUES
('notice_level', '1', '普通', 1, 1, '普通级别', NOW()),
('notice_level', '2', '重要', 2, 1, '重要级别', ()),
('notice_level', '3', '紧急', 3, 1, '紧急级别', NOW());

-- 数据权限范围枚举
INSERT INTO `sys_enum` (`enum_type`, `enum_value`, `enum_label`, `sort`, `status`, `remark`, `create_time`) VALUES
('data_scope', '1', '全部数据', 1, 1, '全部数据权限', NOW()),
('data_scope', '2', '自定义数据', 2, 1, '自定义数据权限', NOW()),
('data_scope', '3', '本部门数据', 3, 1, '本部门数据权限', NOW()),
('data_scope', '4', '本部门及以下数据', 4, 1, '本部门及以下数据权限', NOW()),
('data_scope', '5', '仅本人数据', 5, 1, '仅本人数据权限', NOW());

-- =============================================
-- 8. 全局变量数据（sys_config）
-- =============================================
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_name`, `config_type`, `status`, `remark`, `create_time`) VALUES
('system.name', '后台管理系统', '系统名称', 1, 1, '系统名称配置', NOW()),
('system.version', '1.0.0', '系统版本', 1, 1, '系统版本号', NOW()),
('login.retry.count', '5', '密码重试次数', 1, 1, '登录密码错误重试次数限制', NOW()),
('captcha.expire.time', '300', '验证码过期时间', 1, 1, '验证码过期时间（秒）', NOW()),
('login.lock.time', '1800', '登录锁定时间', 1, 1, '登录失败锁定时间（秒）', NOW());

-- =============================================
-- 9. 通知公告数据（sys_notice）
-- =============================================
INSERT INTO `sys_notice` (`notice_title`, `notice_content`, `notice_type`otice_level`, `status`, `remark`, `create_by`, `create_time`) VALUES
('欢迎使用后台管理系统', '欢迎使用后台管理系统！本系统提供完善的用户管理、角色管理、权限管理等功能。如有问题请联系系统管理员。', 1, 1, 1, '系统欢迎通知', 1, NOW()),
('系统维护通知', '系统将于本周六凌晨2:00-4:00进行例行维护，届时系统将暂停服务，请各位用户提前做好相关准备工作。给您带来的不便敬请谅解！', 2, 2, 1, '系统维护公告', 1, NOW());

SET FOREIGN_KEY_CHECKS = 1;
