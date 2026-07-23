-- 小程序打卡功能菜单与权限初始化
-- 隐藏菜单，仅作为后端 @PreAuthorize 权限码载体
-- 授权角色：1 超级管理员、2 系统管理员、4 小程序用户

INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(11, 0, '打卡管理', NULL, 1, '/checkin', NULL, 'Calendar', 11, 0, 1, '小程序打卡功能模块', NOW(), 'system', NOW(), NULL, 0),
(110000, 11, '打卡计划', 'checkin:plan:list', 2, NULL, NULL, 'List', 1, 0, 1, '打卡计划查询', NOW(), 'system', NOW(), NULL, 0),
(110001, 110000, '新增计划', 'checkin:plan:add', 3, NULL, NULL, NULL, 1, 0, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(110002, 110000, '编辑计划', 'checkin:plan:edit', 3, NULL, NULL, NULL, 2, 0, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(110003, 110000, '删除计划', 'checkin:plan:delete', 3, NULL, NULL, NULL, 3, 0, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(110004, 110000, '打卡/取消', 'checkin:record:toggle', 3, NULL, NULL, NULL, 4, 0, 1, NULL, NOW(), 'system', NOW(), NULL, 0),
(110005, 110000, '日历查询', 'checkin:calendar:view', 3, NULL, NULL, NULL, 5, 0, 1, NULL, NOW(), 'system', NOW(), NULL, 0)
ON DUPLICATE KEY UPDATE
  `parent_id` = VALUES(`parent_id`),
  `permission_name` = VALUES(`permission_name`),
  `permission_code` = VALUES(`permission_code`),
  `type` = VALUES(`type`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `icon` = VALUES(`icon`),
  `sort` = VALUES(`sort`),
  `visible` = VALUES(`visible`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_time` = NOW(),
  `deleted` = 0;

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`, `create_by`, `deleted`) VALUES
(1, 11, NOW(), 'system', 0),
(1, 110000, NOW(), 'system', 0),
(1, 110001, NOW(), 'system', 0),
(1, 110002, NOW(), 'system', 0),
(1, 110003, NOW(), 'system', 0),
(1, 110004, NOW(), 'system', 0),
(1, 110005, NOW(), 'system', 0),
(2, 11, NOW(), 'system', 0),
(2, 110000, NOW(), 'system', 0),
(2, 110001, NOW(), 'system', 0),
(2, 110002, NOW(), 'system', 0),
(2, 110003, NOW(), 'system', 0),
(2, 110004, NOW(), 'system', 0),
(2, 110005, NOW(), 'system', 0),
(4, 11, NOW(), 'system', 0),
(4, 110000, NOW(), 'system', 0),
(4, 110001, NOW(), 'system', 0),
(4, 110002, NOW(), 'system', 0),
(4, 110003, NOW(), 'system', 0),
(4, 110004, NOW(), 'system', 0),
(4, 110005, NOW(), 'system', 0)
ON DUPLICATE KEY UPDATE
  `deleted` = 0;
