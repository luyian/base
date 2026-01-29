
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '参数键',
  `config_value` text NOT NULL COMMENT '参数值',
  `config_name` varchar(100) NOT NULL COMMENT '参数名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `type` varchar(20) DEFAULT 'string' COMMENT '参数类型（string、number、boolean、json）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='全局变量表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
  `dept_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_enum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_enum` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enum_type` varchar(50) NOT NULL COMMENT '枚举类型',
  `enum_code` varchar(50) NOT NULL COMMENT '枚举编码',
  `enum_value` varchar(100) NOT NULL COMMENT '枚举值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`enum_type`,`enum_code`),
  KEY `idx_enum_type` (`enum_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='枚举表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_log_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log_login` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：失败，1：成功）',
  `message` varchar(255) DEFAULT NULL COMMENT '提示信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_log_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
  `operation` varchar(50) DEFAULT NULL COMMENT '操作类型',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `result` text COMMENT '返回结果',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) DEFAULT NULL COMMENT '操作地点',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：失败，1：成功）',
  `error_msg` text COMMENT '错误信息',
  `execute_time` int DEFAULT '0' COMMENT '执行时长（毫秒）',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '操作人',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint(1) DEFAULT '1' COMMENT '类型（1：通知，2：公告）',
  `level` tinyint(1) DEFAULT '1' COMMENT '级别（1：普通，2：重要，3：紧急）',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：草稿，1：已发布）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_notice_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知阅读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) DEFAULT NULL COMMENT '权限编码',
  `type` tinyint(1) NOT NULL COMMENT '类型（1：目录，2：菜单，3：按钮）',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见（0：隐藏，1：显示）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限/菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `region_code` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区划代码',
  `region_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区划名称',
  `level` tinyint NOT NULL COMMENT '层级（1-省，2-市，3-区）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-禁用，1-正常）',
  `full_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整名称',
  `short_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简称',
  `pinyin` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拼音',
  `pinyin_prefix` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拼音首字母',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `create_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`region_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_region_name` (`region_name`),
  KEY `idx_pinyin_prefix` (`pinyin_prefix`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='行政区划表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `data_scope` tinyint(1) DEFAULT '1' COMMENT '数据权限范围（1：全部，2：本部门及以下，3：本部门，4：仅本人，5：自定义）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_role_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `department_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_dept` (`role_id`,`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `deleted` int DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别（0：未知，1：男，2：女）',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

