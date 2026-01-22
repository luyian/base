-- =============================================
-- 数据库表结构脚本
-- 数据库：base
-- 版本：1.0
-- 创建时间：2026-01-14
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `gender` TINYINT(1) DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 角色表
-- =============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `data_scope` TINYINT(1) DEFAULT 5 COMMENT '数据权限范围：1-全部数据 2-自定义数据 3-本部门数据 4-本部门及以下数据 5-仅本人数据',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMEN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 3. 权限/菜单表
-- =============================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID，0表示顶级',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `permission_type` TINYINT(1) NOT NULL COMMENT '权限类型：1-菜单 2-按钮',
  `route_path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `component_path` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
  `visible` TINYINT(1) DEFAULT 1 COMMENT '是否可见：0-隐藏 1-显示',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限/菜单表';

-- =============================================
-- 4. 部门表
-- =============================================
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID，0表示顶级',
  `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
  `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- =============================================
-- 5. 枚举表
-- =============================================
DROP TABLE IF EXISTS `sys_enum`;
CREATE TABLE `sys_enum` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '枚举ID',
  `enum_type0) NOT NULL COMMENT '枚举类型',
  `enum_value` VARCHAR(50) NOT NULL COMMENT '枚举值',
  `enum_label` VARCHAR(50) NOT NULL COMMENT '枚举标签',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_enum_type_value` (`enum_type`, `enum_value`),
  KEY `idx_enum_type` (`enum_type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='枚举表';

-- =============================================
-- 6. 全局变量表
-- =============================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
  `config_nARCHAR(50) NOT NULL COMMENT '配置名称',
  `config_type` TINYINT(1) DEFAULT 2 COMMENT '配置类型：1-系统内置 2-用户自定义',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY _type` (`config_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局变量表';

-- =============================================
-- 7. 操作日志表
-- =============================================
DROP TABLE IF EXISTS `sys_log_operation`;
CREATE TABLE `sys_log_operation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
  `response_result` TEXT DEFAULT NULL COMMENT '响应结果',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
  `execute_time` BIGINT DEFAULT NULL COMMENT '执行时长（毫秒）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-失败 1-成功',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `create_by` BIGINT DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 8. 登录日志表
-- =============================================
DROP TABLE IF EXISTS `sys_log_login`;
CREATE TABLE `sys_log_login` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  `login_location` VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
  `browser` VARCHAR(50) DEFAULT NULL COMMENT '浏览器',
  `os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-失败 1-成功',
  `message` VARCHAR(255) DEFAULT NULL COMMENT '提示信息',
  `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_ip` (`login_ip`),
  KEY `idx_status` (`status`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- =============================================
-- 9. 通知公告表
-- =============================================
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notice_title` VARCHAR(100) NOT NULL COMMENT '通知标题',
  `notice_content` TEXT NOT NULL COMMENT '通知内容',
  `notice_type` TINYINT(1) DEFAULT 1 COMMENT '通知类型：1-通知 2-公告',
  `notice_level` TINYINT(1) DEFAULT 1 COMMENT '通知级别：1-普通 2-重要 3-紧急',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态：0-草稿 1-已发布',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_notice_type` (`notice_type`),
  KEY `idx_notice_level` (`notice_level`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- =============================================
-- 10. 用户角色关联表
-- =============================================
DROP LE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 11. 角色权限关联表
-- =============================================
DROP TABLE IF EXISTS `sys_role_permissi`;
CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =============================================
-- 12. 角色部门关联表（用于自定义数据权限）
-- ==================================
DROP TABLE IF EXISTS `sys_role_department`;
CREATE TABLE `sys_role_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `department_id` BIGINT NOT NULL COMMENT '部门ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_department` (`role_id`, `department_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色部门关联表';

-- =============================================
-- 13. 通知已读记录表
-- =============================================
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `notice_id` BIGINT NOT NULL COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
  KEY `idx_notice_id` (`notice_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_read_time` (`read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知已读记录表';

SET FOREIGN_KEY_CHECKS = 1;
