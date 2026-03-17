-- =============================================
-- 系统管理 - 大模型配置菜单与权限（多条配置，选一条生效）
-- 菜单 ID=111，按钮 11101 查询、11102 编辑、11103 新增、11104 删除
-- =============================================

-- 大模型配置菜单（系统管理 parent_id=1 下，sort=11）
INSERT INTO `sys_permission` VALUES (111, 1, '大模型配置', 'system:ai-config:list', 2, '/system/ai-config', 'system/AiConfig', 'ChatDotRound', 11, 1, 1, 'AI 大模型接口配置（支持多条，选一条生效）', NOW(), 'system', NOW(), NULL, 0);

-- 按钮权限
INSERT INTO `sys_permission` VALUES (11101, 111, '配置查询', 'system:ai-config:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询大模型配置', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (11102, 111, '配置编辑', 'system:ai-config:edit', 3, NULL, NULL, NULL, 2, 1, 1, '编辑/设为生效', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (11103, 111, '配置新增', 'system:ai-config:add', 3, NULL, NULL, NULL, 3, 1, 1, '新增大模型配置', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (11104, 111, '配置删除', 'system:ai-config:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除大模型配置', NOW(), 'system', NOW(), NULL, 0);

-- 为超级管理员(role_id=1)分配新菜单及按钮
INSERT INTO `sys_role_permission` VALUES (600, 1, 111, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (601, 1, 11101, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (602, 1, 11102, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (603, 1, 11103, NOW(), NULL, NULL, NULL, 0);
INSERT INTO `sys_role_permission` VALUES (604, 1, 11104, NOW(), NULL, NULL, NULL, 0);
