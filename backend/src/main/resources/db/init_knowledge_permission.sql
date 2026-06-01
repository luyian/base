-- ----------------------------
-- 知识库菜单初始化 SQL
-- ----------------------------

-- 1. 添加顶级目录：知识库 (ID=8)
INSERT INTO `sys_permission` VALUES (8, 0, '知识库', NULL, 1, '/knowledge-base', NULL, 'Collection', 8, 1, 1, '知识库管理', NOW(), 'system', NOW(), NULL, 0);

-- 2. 添加二级菜单：知识库列表
INSERT INTO `sys_permission` VALUES (801, 8, '知识库列表', 'knowledge:base:list', 2, '/knowledge-base', 'KnowledgeBaseList', 'Folder', 1, 1, 1, '知识库列表', NOW(), 'system', NOW(), NULL, 0);

-- 2.1 添加二级菜单：文档广场（全站文档浏览）
INSERT INTO `sys_permission` VALUES (802, 8, '文档广场', 'knowledge:document:square', 2, '/document-square', 'DocumentSquare', 'Document', 2, 1, 1, '文档广场（全站文档浏览）', NOW(), 'system', NOW(), NULL, 0);

-- 3. 知识库按钮权限
INSERT INTO `sys_permission` VALUES (80101, 801, '知识库查询', 'knowledge:base:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询知识库', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80102, 801, '知识库新增', 'knowledge:base:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增知识库', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80103, 801, '知识库编辑', 'knowledge:base:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑知识库', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80104, 801, '知识库删除', 'knowledge:base:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除知识库', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80105, 801, '文档管理', 'knowledge:document:manage', 3, NULL, NULL, NULL, 5, 1, 1, '管理文档（增删改查）', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80106, 801, '目录管理', 'knowledge:directory:manage', 3, NULL, NULL, NULL, 6, 1, 1, '管理目录（增删改）', NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (80107, 801, '标签管理', 'knowledge:tag:manage', 3, NULL, NULL, NULL, 7, 1, 1, '管理标签（增删改）', NOW(), 'system', NOW(), NULL, 0);

-- 4. 给超级管理员角色(role_id=1)分配知识库权限
INSERT INTO `sys_role_permission` (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE id >= 8 AND id < 900;

-- 5. 给系统管理员角色(role_id=2)分配知识库权限
INSERT INTO `sys_role_permission` (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE id >= 8 AND id < 900;
