-- ============================
-- 基础管理系统数据库初始化脚本
-- ============================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `base_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `base_system`;

-- ============================
-- 用户表
-- ============================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别（0：未知，1：男，2：女）',
    `dept_id` BIGINT(20) DEFAULT NULL COMMENT '部门ID',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================
-- 角色表
-- ============================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `data_scope` TINYINT(1) DEFAULT 1 COMMENT '数据权限范围（1：全部，2：本部门及以下，3：本部门，4：仅本人，5：自定义）',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================
-- 权限/菜单表
-- ============================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父级ID',
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限编码',
    `type` TINYINT(1) NOT NULL COMMENT '类型（1：目录，2：菜单，3：按钮）',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `visible` TINYINT(1) DEFAULT 1 COMMENT '是否可见（0：隐藏，1：显示）',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限/菜单表';

-- ============================
-- 部门表
-- ============================
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父级ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ============================
-- 枚举表
-- ============================
DROP TABLE IF EXISTS `sys_enum`;
CREATE TABLE `sys_enum` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `enum_type` VARCHAR(50) NOT NULL COMMENT '枚举类型',
    `enum_code` VARCHAR(50) NOT NULL COMMENT '枚举编码',
    `enum_value` VARCHAR(100) NOT NULL COMMENT '枚举值',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`enum_type`, `enum_code`),
    KEY `idx_enum_type` (`enum_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='枚举表';

-- ============================
-- 全局变量表
-- ============================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '参数键',
    `config_value` TEXT NOT NULL COMMENT '参数值',
    `config_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `type` VARCHAR(20) DEFAULT 'string' COMMENT '参数类型（string、number、boolean、json）',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局变量表';

-- ============================
-- 操作日志表
-- ============================
DROP TABLE IF EXISTS `sys_log_operation`;
CREATE TABLE `sys_log_operation` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `result` TEXT DEFAULT NULL COMMENT '返回结果',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '操作地点',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：失败，1：成功）',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `execute_time` INT(11) DEFAULT 0 COMMENT '执行时长（毫秒）',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================
-- 登录日志表
-- ============================
DROP TABLE IF EXISTS `sys_log_login`;
CREATE TABLE `sys_log_login` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
    `browser` VARCHAR(50) DEFAULT NULL COMMENT '浏览器',
    `os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0：失败，1：成功）',
    `message` VARCHAR(255) DEFAULT NULL COMMENT '提示信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================
-- 通知公告表
-- ============================
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` TINYINT(1) DEFAULT 1 COMMENT '类型（1：通知，2：公告）',
    `level` TINYINT(1) DEFAULT 1 COMMENT '级别（1：普通，2：重要，3：紧急）',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态（0：草稿，1：已发布）',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ============================
-- 通知阅读记录表
-- ============================
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `notice_id` BIGINT(20) NOT NULL COMMENT '通知ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知阅读记录表';

-- ============================
-- 用户角色关联表
-- ============================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================
-- 角色权限关联表
-- ============================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT(20) NOT NULL COMMENT '权限ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
