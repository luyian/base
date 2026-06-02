-- ----------------------------
-- 飞书审批管理菜单和权限初始化
-- ----------------------------

-- 1. 添加顶级目录：审批管理 (ID=9)
INSERT INTO `sys_permission` VALUES (9, 0, '审批管理', NULL, 1, '/approval', NULL, 'Stamp', 9, 1, 1, '飞书审批管理', NOW(), 'system', NOW(), NULL, 0);

-- 2. 添加二级菜单：审批列表
INSERT INTO `sys_permission` VALUES (901, 9, '审批列表', 'approval:instance:list', 2, '/approval', 'approval/index', 'List', 1, 1, 1, '审批实例列表', NOW(), 'system', NOW(), NULL, 0);

-- 3. 添加二级菜单：审批模板
INSERT INTO `sys_permission` VALUES (902, 9, '审批模板', 'approval:template:list', 2, '/approval/template', 'approval/template/index', 'Document', 2, 1, 1, '审批模板管理', NOW(), 'system', NOW(), NULL, 0);

-- 4. 添加二级菜单：事件日志
INSERT INTO `sys_permission` VALUES (903, 9, '事件日志', 'approval:event:list', 2, '/approval/event-log', 'approval/eventLog/index', 'Notebook', 3, 1, 1, '事件回调日志', NOW(), 'system', NOW(), NULL, 0);

-- 5. 审批列表按钮权限
INSERT INTO `sys_permission` VALUES (90101, 901, '审批查询', 'approval:instance:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询审批实例', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90102, 901, '审批发起', 'approval:instance:submit', 3, NULL, NULL, NULL, 2, 1, 1, '发起审批', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90103, 901, '审批撤销', 'approval:instance:cancel', 3, NULL, NULL, NULL, 3, 1, 1, '撤销审批', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90104, 901, '审批同步', 'approval:instance:sync', 3, NULL, NULL, NULL, 4, 1, 1, '手动同步审批状态', NOW(), 'system', NOW(), NULL, 0);

-- 6. 审批模板按钮权限
INSERT INTO `sys_permission` VALUES (90201, 902, '模板查询', 'approval:template:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询审批模板', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90202, 902, '模板新增', 'approval:template:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增审批模板', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90203, 902, '模板编辑', 'approval:template:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑审批模板', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90204, 902, '模板删除', 'approval:template:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除审批模板', NOW(), 'system', NOW(), NULL, 0);

-- 7. 事件日志按钮权限
INSERT INTO `sys_permission` VALUES (90301, 903, '日志查询', 'approval:event:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询事件日志', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (90302, 903, '日志重试', 'approval:event:retry', 3, NULL, NULL, NULL, 2, 1, 1, '手动重试事件', NOW(), 'system', NOW(), NULL, 0);

-- 8. 给超级管理员角色(role_id=1)分配审批管理权限
INSERT INTO `sys_role_permission` (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE id >= 9 AND id < 1000;

-- 9. 给系统管理员角色(role_id=2)分配审批管理权限
INSERT INTO `sys_role_permission` (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE id >= 9 AND id < 1000;
