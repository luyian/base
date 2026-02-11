-- =============================================
-- 消息中心 + 基金权限重构 - 菜单和权限初始化
-- =============================================

-- =============================================
-- 一、基金模块新增自选按钮权限
-- 在现有基金菜单(ID=407)下新增自选相关按钮
-- =============================================

-- 基金自选（加自选/取消自选）
INSERT INTO `sys_permission` VALUES (40708, 407, '基金自选', 'stock:fund:watchlist', 3, NULL, NULL, NULL, 6, 1, 1, '加自选/取消自选', NOW(), 'system', NOW(), NULL, 0);

-- =============================================
-- 二、消息中心模块菜单和权限
-- 顶级目录 ID=5，二级菜单 ID=501，按钮 ID=50101+
-- =============================================

-- 1. 消息中心目录（顶级）
INSERT INTO `sys_permission` VALUES (5, 0, '消息中心', NULL, 1, '/message', NULL, 'Bell', 5, 1, 1, '消息推送与订阅管理', NOW(), 'system', NOW(), NULL, 0);

-- 2. 消息订阅菜单
INSERT INTO `sys_permission` VALUES (501, 5, '消息订阅', 'message:subscription:list', 2, '/message', 'message/index', 'Message', 1, 1, 1, '消息订阅管理', NOW(), 'system', NOW(), NULL, 0);

-- 3. 消息订阅按钮权限
INSERT INTO `sys_permission` VALUES (50101, 501, '订阅查询', 'message:subscription:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询订阅列表', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (50102, 501, '订阅开关', 'message:subscription:toggle', 3, NULL, NULL, NULL, 2, 1, 1, '开启/关闭订阅', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (50103, 501, '手动推送', 'message:push:execute', 3, NULL, NULL, NULL, 3, 1, 1, '手动触发推送（管理员）', NOW(), 'system', NOW(), NULL, 0);

-- =============================================
-- 三、为超级管理员(role_id=1)分配所有新增权限
-- sys_role_permission: (id, role_id, permission_id, create_time, ...)
-- =============================================

-- 基金自选按钮
INSERT INTO `sys_role_permission` VALUES (487, 1, 40708, NOW(), NULL, NULL, NULL, 0);

-- 消息中心目录
INSERT INTO `sys_role_permission` VALUES (488, 1, 5, NOW(), NULL, NULL, NULL, 0);

-- 消息订阅菜单
INSERT INTO `sys_role_permission` VALUES (489, 1, 501, NOW(), NULL, NULL, NULL, 0);

-- 消息订阅按钮
INSERT INTO `sys_role_permission` VALUES (490, 1, 50101, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (491, 1, 50102, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (492, 1, 50103, NOW(), NULL, NULL, NULL, 0);
