-- ----------------------------
-- 文件转换菜单初始化 SQL
-- 放在系统管理（ID=1）下
-- ----------------------------

-- 文件转换菜单
INSERT INTO `sys_permission` VALUES (114, 1, '文件转换', 'system:fileConvert:use', 2, '/file-convert', 'system/FileConvert', 'RefreshRight', 14, 1, 1, '文件格式转换工具（PDF转Word等）', NOW(), 'system', NOW(), NULL, 0);

-- 给超级管理员角色(role_id=1)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 114);

-- 给系统管理员角色(role_id=2)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (2, 114);
