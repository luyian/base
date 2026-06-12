-- ----------------------------
-- 代码分支管理表
-- ----------------------------

CREATE TABLE `dev_branch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(64) NOT NULL COMMENT '编号',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `prd_link` varchar(512) DEFAULT NULL COMMENT 'PRD链接',
  `prod_branch` varchar(128) NOT NULL COMMENT '生产分支',
  `dev_branch` varchar(128) NOT NULL COMMENT '开发分支',
  `online_time` date NOT NULL COMMENT '上线时间',
  `priority` tinyint DEFAULT 0 COMMENT '紧急程度（0-普通 1-紧急 2-特急）',
  `status` tinyint DEFAULT 0 COMMENT '状态（0-进行中 1-已完成）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除（0未删除 1已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_online_time` (`online_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码分支管理表';

-- ----------------------------
-- sys_config 初始化当前生产分支配置
-- ----------------------------
INSERT INTO `sys_config` (config_key, config_value, config_name, type, status, remark, create_time, create_by)
VALUES ('branch.current_prod', 'master', '当前生产分支', 'string', 1, '代码分支管理模块使用的当前生产分支名称', NOW(), 'system');

-- ----------------------------
-- 分支管理菜单权限
-- ----------------------------
INSERT INTO `sys_permission` VALUES (115, 1, '分支管理', 'dev:branch:list', 2, '/dev/branch', 'DevBranch', 'Connection', 15, 1, 1, '代码分支管理', NOW(), 'system', NOW(), NULL, 0);

-- 按钮权限
INSERT INTO `sys_permission` VALUES (1151, 115, '新增分支', 'dev:branch:add', 3, NULL, NULL, NULL, 1, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (1152, 115, '编辑分支', 'dev:branch:edit', 3, NULL, NULL, NULL, 2, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);
INSERT INTO `sys_permission` VALUES (1153, 115, '删除分支', 'dev:branch:delete', 3, NULL, NULL, NULL, 3, 1, 1, NULL, NOW(), 'system', NOW(), NULL, 0);

-- 给超级管理员角色(role_id=1)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 115);
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 1151);
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 1152);
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 1153);
