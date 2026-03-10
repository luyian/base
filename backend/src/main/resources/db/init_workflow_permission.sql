-- ----------------------------
-- 流程引擎菜单和权限初始化
-- ----------------------------

-- 1. 添加顶级目录：流程管理 (ID=6)
INSERT INTO `sys_permission` VALUES (6, 0, '流程管理', NULL, 1, '/workflow', NULL, 'Promotion', 6, 1, 1, '流程定义与审批管理', NOW(), 'system', NOW(), NULL, 0);

-- 2. 添加二级菜单：流程定义
INSERT INTO `sys_permission` VALUES (601, 6, '流程定义', 'workflow:definition:list', 2, '/workflow/definition', 'workflow/ProcessDefinition', 'Grid', 1, 1, 1, '流程定义管理', NOW(), 'system', NOW(), NULL, 0);

-- 3. 添加二级菜单：我的任务
INSERT INTO `sys_permission` VALUES (602, 6, '我的任务', 'workflow:task:list', 2, '/workflow/task', 'workflow/MyTask', 'List', 2, 1, 1, '我的待办任务', NOW(), 'system', NOW(), NULL, 0);

-- 4. 添加二级菜单：我发起的
INSERT INTO `sys_permission` VALUES (603, 6, '我发起的', 'workflow:initiated:list', 2, '/workflow/initiated', 'workflow/MyInitiated', 'Document', 3, 1, 1, '我发起的流程', NOW(), 'system', NOW(), NULL, 0);

-- 5. 流程定义按钮权限
INSERT INTO `sys_permission` VALUES (60101, 601, '流程查询', 'workflow:definition:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询流程定义', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60102, 601, '流程新增', 'workflow:definition:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增流程定义', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60103, 601, '流程编辑', 'workflow:definition:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑流程定义', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60104, 601, '流程删除', 'workflow:definition:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除流程定义', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60105, 601, '流程发布', 'workflow:definition:publish', 3, NULL, NULL, NULL, 5, 1, 1, '发布流程定义', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60106, 601, '流程禁用', 'workflow:definition:disable', 3, NULL, NULL, NULL, 6, 1, 1, '禁用流程定义', NOW(), 'system', NOW(), NULL, 0);

-- 6. 我的任务按钮权限
INSERT INTO `sys_permission` VALUES (60201, 602, '任务查询', 'workflow:task:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询待办任务', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60202, 602, '任务审批', 'workflow:task:approve', 3, NULL, NULL, NULL, 2, 1, 1, '审批任务', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60203, 602, '任务回退', 'workflow:task:rollback', 3, NULL, NULL, NULL, 3, 1, 1, '回退任务', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60204, 602, '任务转办', 'workflow:task:delegate', 3, NULL, NULL, NULL, 4, 1, 1, '转办任务', NOW(), 'system', NOW(), NULL, 0);

-- 7. 我发起的按钮权限
INSERT INTO `sys_permission` VALUES (60301, 603, '流程查询', 'workflow:initiated:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询我发起的流程', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60302, 603, '流程终止', 'workflow:initiated:terminate', 3, NULL, NULL, NULL, 2, 1, 1, '终止流程', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (60303, 603, '流程详情', 'workflow:initiated:detail', 3, NULL, NULL, NULL, 3, 1, 1, '查看流程详情', NOW(), 'system', NOW(), NULL, 0);

-- 8. 给超级管理员角色分配流程管理权限
INSERT INTO `sys_role_permission` (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE id >= 6 AND id < 700;
