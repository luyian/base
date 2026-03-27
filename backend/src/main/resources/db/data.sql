-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: base_system
-- ------------------------------------------------------
-- Server version	8.0.45-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_ai_config`
--

DROP TABLE IF EXISTS `sys_ai_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_ai_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置名称（用于区分多条配置）',
  `base_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API 基础地址',
  `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API Key',
  `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'qwen-plus' COMMENT '模型名称',
  `timeout` int NOT NULL DEFAULT '30000' COMMENT '超时时间(ms)',
  `retry` int NOT NULL DEFAULT '2' COMMENT '重试次数',
  `max_message_length` int NOT NULL DEFAULT '2000' COMMENT '消息最大长度',
  `max_context_length` int NOT NULL DEFAULT '5000' COMMENT '上下文最大长度',
  `is_active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否当前生效（0否 1是，仅一条可为1）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0禁用 1启用）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_is_active` (`is_active`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='大模型配置表（支持多条，选一条生效）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_ai_config`
--

LOCK TABLES `sys_ai_config` WRITE;
/*!40000 ALTER TABLE `sys_ai_config` DISABLE KEYS */;
INSERT INTO `sys_ai_config` VALUES (1,'默认（通义千问）','https://dashscope.aliyuncs.com/compatible-mode/v1','sk-9a6a6292001847ca8eac5f8353f3d7a7','qwen-plus',30000,2,2000,5000,0,1,0,'请将 api_key 改为实际 Key 后保存，并点击「设为生效」','2026-03-18 14:42:33',NULL,'2026-03-18 15:41:58','system',0),(2,'移动云','https://zhenze-huhehaote.cmecloud.cn/v1','bW-YElAMDW_Uwd6ZtcMVYX1tqaSvhXxGFL6a-llH7HY','minimax-m2.5',30000,2,2000,5000,0,1,0,'','2026-03-18 15:40:14','system','2026-03-24 11:19:00','system',0),(3,'联通云','https://aigw-gzgy2.cucloud.cn:8443/v1','sk-sp-pPX0ZZVSl7I9I2Jcwzf5hLI5uUvrLkya','MiniMax-M2.5',30000,2,2000,5000,1,1,0,'','2026-03-18 15:41:50','system','2026-03-18 16:04:37','system',0),(4,'移动云2','https://zhenze-huhehaote.cmecloud.cn/api/coding/v1','bW-YElAMDW_Uwd6ZtcMVYX1tqaSvhXxGFL6a-llH7HY','minimax-m2.5',30000,2,2000,5000,0,1,0,'','2026-03-18 15:51:07','system','2026-03-18 15:58:43','system',0);
/*!40000 ALTER TABLE `sys_ai_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数值',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'string' COMMENT '参数类型（string、number、boolean、json）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_config_key` (`config_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='全局变量表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'system.name','基础管理系统','系统名称','系统的名称','string',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(2,'system.version','1.0.0','系统版本','系统的版本号','string',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(3,'user.password.maxRetryCount','5','密码最大重试次数','用户登录密码最大重试次数','number',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(4,'user.password.lockTime','10','账号锁定时间','密码输错后账号锁定时间（分钟）','number',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(5,'captcha.enabled','true','验证码开关','是否开启验证码功能','boolean',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(6,'captcha.type','math','验证码类型','验证码类型（math：数字运算，char：字符验证）','string',1,NULL,'2026-01-14 14:59:03','system','2026-01-14 14:59:03',NULL,0),(7,'stock.sync.batch.threshold','100','K线批量同步阈值',NULL,'number',1,'股票数量超过此值时使用批量接口，默认100','2026-02-06 15:11:16',NULL,'2026-02-06 15:11:16',NULL,0),(8,'stock.sync.batch.size','3','K线批量请求大小',NULL,'number',1,'每次批量请求的最大股票数量，默认100','2026-02-06 15:11:16',NULL,'2026-02-06 15:44:00',NULL,0);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `dept_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (1,0,'总公司','ROOT','管理员','13800138000','admin@example.com',0,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(2,1,'技术部','TECH','技术总监','13800138001','tech@example.com',1,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(3,1,'市场部','MARKET','市场总监','13800138002','market@example.com',2,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(4,1,'财务部','FINANCE','财务总监','13800138003','finance@example.com',3,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(5,2,'研发组','DEV','研发组长','13800138004','dev@example.com',1,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(6,2,'测试组','TEST','测试组长','13800138005','test@example.com',2,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_enum`
--

DROP TABLE IF EXISTS `sys_enum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_enum` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enum_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '枚举类型',
  `type_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '枚举类型中文描述',
  `enum_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '枚举编码',
  `enum_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '枚举值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_type_code` (`enum_type`,`enum_code`) USING BTREE,
  KEY `idx_enum_type` (`enum_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='枚举表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_enum`
--

LOCK TABLES `sys_enum` WRITE;
/*!40000 ALTER TABLE `sys_enum` DISABLE KEYS */;
INSERT INTO `sys_enum` VALUES (1,'gender','性别','0','未知','性别未知',0,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(2,'gender','性别','1','男','男性',1,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(3,'gender','性别','2','女','女性',2,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(4,'status','状态','0','禁用','禁用状态',0,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(5,'status','状态','1','启用','启用状态',1,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(6,'notice_type','通知类型','1','通知','系统通知',1,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(7,'notice_type','通知类型','2','公告','系统公告',2,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(8,'notice_level','通知级别','1','普通','普通级别',1,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(9,'notice_level','通知级别','2','重要','重要级别',2,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(10,'notice_level','通知级别','3','紧急','紧急级别',3,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(11,'data_scope','数据权限范围','1','全部数据','可以查看所有数据',1,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(12,'data_scope','数据权限范围','2','本部门及以下','可以查看本部门及下级部门数据',2,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(13,'data_scope','数据权限范围','3','本部门','只能查看本部门数据',3,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(14,'data_scope','数据权限范围','4','仅本人','只能查看自己的数据',4,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(15,'data_scope','数据权限范围','5','自定义','自定义数据权限',5,1,NULL,'2026-01-14 14:59:03','system','2026-02-05 10:49:44',NULL,0),(16,'login_status','登录状态','0','失败','失败',1,1,NULL,'2026-02-04 11:59:34','system','2026-02-05 10:49:44',NULL,0),(17,'login_status','登录状态','1','成功','成功',2,1,NULL,'2026-02-04 11:59:34','system','2026-02-05 10:49:44',NULL,0);
/*!40000 ALTER TABLE `sys_enum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_export_config`
--

DROP TABLE IF EXISTS `sys_export_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_export_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置编码（唯一标识）',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '配置描述',
  `data_source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SERVICE' COMMENT '数据源类型（SERVICE-服务方法, SQL-自定义SQL）',
  `data_source_bean` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '数据源Bean名称',
  `data_source_method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '数据源方法名',
  `data_source_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '自定义SQL',
  `query_param_class` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '查询参数类全路径',
  `batch_size` int DEFAULT '5000' COMMENT '分批查询大小',
  `max_export_count` int DEFAULT '1000000' COMMENT '最大导出数量',
  `file_name_pattern` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件名模式',
  `enable_multi_sheet` tinyint(1) DEFAULT '0' COMMENT '是否启用多Sheet',
  `sheet_max_rows` int DEFAULT '100000' COMMENT '单Sheet最大行数',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限编码',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_config_code` (`config_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='导出配置主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_export_config`
--

LOCK TABLES `sys_export_config` WRITE;
/*!40000 ALTER TABLE `sys_export_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_export_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_export_field`
--

DROP TABLE IF EXISTS `sys_export_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_export_field` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_id` bigint NOT NULL COMMENT '导出配置ID',
  `field_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段名',
  `field_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段标签（Excel列标题）',
  `field_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'STRING' COMMENT '字段类型（STRING/NUMBER/DATE/DATETIME/BOOLEAN）',
  `field_width` int DEFAULT '20' COMMENT '列宽度',
  `field_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '格式化模式（如日期格式：yyyy-MM-dd）',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '字典类型（关联sys_enum表的enum_type）',
  `mask_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '脱敏类型（PHONE/ID_CARD/EMAIL/BANK_CARD/NAME/CUSTOM）',
  `mask_pattern` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '自定义脱敏正则',
  `mask_replacement` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '自定义脱敏替换字符',
  `converter_bean` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '自定义转换器Bean名称',
  `default_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '默认值（字段值为空时使用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_config_id` (`config_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='导出字段配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_export_field`
--

LOCK TABLES `sys_export_field` WRITE;
/*!40000 ALTER TABLE `sys_export_field` DISABLE KEYS */;
INSERT INTO `sys_export_field` VALUES (1,1,'id','日志ID','NUMBER',10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(2,1,'username','用户名','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(3,1,'loginIp','登录IP','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(4,1,'loginLocation','登录地点','STRING',20,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(5,1,'browser','浏览器','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(6,1,'os','操作系统','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(7,1,'status','登录状态','STRING',10,NULL,'login_status',NULL,NULL,NULL,NULL,NULL,7,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(8,1,'message','提示信息','STRING',30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(9,1,'createTime','登录时间','DATETIME',20,'yyyy-MM-dd HH:mm:ss',NULL,NULL,NULL,NULL,NULL,NULL,9,1,NULL,'2026-02-04 11:58:09',NULL,'2026-02-04 14:15:56',NULL,1),(10,1,'id','日志ID','NUMBER',10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(11,1,'username','用户名','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(12,1,'loginIp','登录IP','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(13,1,'loginLocation','登录地点','STRING',20,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(14,1,'browser','浏览器','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(15,1,'os','操作系统','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(16,1,'status','登录状态','STRING',10,NULL,'login_status',NULL,NULL,NULL,NULL,NULL,6,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(17,1,'message','提示信息','STRING',30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(18,1,'createTime','登录时间','DATETIME',20,'yyyy-MM-dd HH:mm:ss',NULL,NULL,NULL,NULL,NULL,NULL,8,1,NULL,'2026-02-04 14:15:57','system','2026-02-04 14:23:04','system',1),(19,1,'id','日志ID','NUMBER',10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(20,1,'username','用户名','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(21,1,'loginIp','登录IP','STRING',15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(22,1,'loginLocation','登录地点','STRING',20,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(23,1,'browser','浏览器','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(24,1,'os','操作系统','STRING',25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(25,1,'status','登录状态','STRING',10,NULL,'login_status',NULL,NULL,NULL,NULL,NULL,6,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(26,1,'message','提示信息','STRING',30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0),(27,1,'createTime','登录时间','DATETIME',30,'yyyy-MM-dd HH:mm:ss',NULL,NULL,NULL,NULL,NULL,NULL,8,1,NULL,'2026-02-04 14:23:05','system','2026-02-04 14:23:05','system',0);
/*!40000 ALTER TABLE `sys_export_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_export_task`
--

DROP TABLE IF EXISTS `sys_export_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_export_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务编号',
  `config_id` bigint NOT NULL COMMENT '导出配置ID',
  `config_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '导出配置编码',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '导出配置名称',
  `query_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '查询参数（JSON格式）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-待处理，1-处理中，2-已完成，3-失败，4-已取消）',
  `total_count` int DEFAULT '0' COMMENT '总记录数',
  `processed_count` int DEFAULT '0' COMMENT '已处理记录数',
  `progress` int DEFAULT '0' COMMENT '进度百分比（0-100）',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小（字节）',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件下载URL',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误信息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `expire_time` datetime DEFAULT NULL COMMENT '文件过期时间',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_task_no` (`task_no`) USING BTREE,
  KEY `idx_config_id` (`config_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_create_by` (`create_by`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='导出任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_export_task`
--

LOCK TABLES `sys_export_task` WRITE;
/*!40000 ALTER TABLE `sys_export_task` DISABLE KEYS */;
INSERT INTO `sys_export_task` VALUES (1,'EXP20260204141611A0A32F87',1,'login_log_export','登录日志导出','{\"username\":\"\",\"loginIp\":\"\",\"status\":null,\"startTime\":\"\",\"endTime\":\"\"}',2,18,18,100,'登录日志_20260204141612.xlsx','D:/upload/export/2026/02/04/登录日志_20260204141612.xlsx',4372,'/api/system/export/task/EXP20260204141611A0A32F87',NULL,'2026-02-04 14:16:12','2026-02-04 14:16:13','2026-02-11 14:16:13',1,'2026-02-04 14:16:12','system','2026-02-04 14:21:23','system',0),(2,'EXP2026020414422819CCE668',1,'login_log_export','登录日志导出','{\"username\":\"\",\"loginIp\":\"\",\"status\":null,\"startTime\":\"\",\"endTime\":\"\"}',2,18,18,100,'登录日志_20260204144229.xlsx','D:/upload/export/2026/02/04/登录日志_20260204144229.xlsx',4592,'/api/system/export/task/EXP2026020414422819CCE668',NULL,'2026-02-04 14:42:29','2026-02-04 14:42:31','2026-02-11 14:42:31',1,'2026-02-04 14:42:29','system','2026-02-04 14:42:51','system',0);
/*!40000 ALTER TABLE `sys_export_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_file`
--

DROP TABLE IF EXISTS `sys_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名（FastDFS路径）',
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名',
  `file_ext` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型（MIME类型）',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件访问URL',
  `file_group` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT 'default' COMMENT '文件分组',
  `file_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件说明',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传人ID',
  `upload_user_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传人名称',
  `status` tinyint DEFAULT '1' COMMENT '状态（0-禁用 1-正常）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_group` (`file_group`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_file`
--

LOCK TABLES `sys_file` WRITE;
/*!40000 ALTER TABLE `sys_file` DISABLE KEYS */;
INSERT INTO `sys_file` VALUES (2,'group1/M00/00/00/CgoAAmm85rSAdKH2AAACGzzd5W8810.jpg','111.jpg','jpg',539,'image/jpeg','group1/M00/00/00/CgoAAmm85rSAdKH2AAACGzzd5W8810.jpg','http://119.45.176.101/group1/M00/00/00/CgoAAmm85rSAdKH2AAACGzzd5W8810.jpg','default','',1,'admin',1,'2026-03-20 14:18:29','2026-03-20 14:18:29');
/*!40000 ALTER TABLE `sys_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_file_log`
--

DROP TABLE IF EXISTS `sys_file_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_file_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径',
  `operation_type` tinyint DEFAULT NULL COMMENT '操作类型（1-上传 2-下载 3-删除 4-预览）',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人名称',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作地点',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求UA',
  `status` tinyint DEFAULT '1' COMMENT '操作状态（0-失败 1-成功）',
  `error_msg` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `execute_time` int DEFAULT NULL COMMENT '执行时长（毫秒）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_file_log`
--

LOCK TABLES `sys_file_log` WRITE;
/*!40000 ALTER TABLE `sys_file_log` DISABLE KEYS */;
INSERT INTO `sys_file_log` VALUES (1,1,'111.jpg','group1/M00/00/00/0f72e146a0f0444094dac0c5d6d276d0.jpg',1,539,1,'admin',NULL,NULL,NULL,1,NULL,20,NULL,'2026-03-20 11:50:33'),(2,2,'111.jpg','group1/M00/00/00/CgoAAmm85rSAdKH2AAACGzzd5W8810.jpg',1,539,1,'admin','183.95.75.117','',NULL,1,NULL,45,NULL,'2026-03-20 14:18:29'),(3,1,'111.jpg','group1/M00/00/00/0f72e146a0f0444094dac0c5d6d276d0.jpg',3,539,1,'admin',NULL,NULL,NULL,1,NULL,NULL,NULL,'2026-03-20 14:18:42');
/*!40000 ALTER TABLE `sys_file_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log_login`
--

DROP TABLE IF EXISTS `sys_log_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log_login` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作系统',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：失败，1：成功）',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '提示信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log_login`
--

LOCK TABLES `sys_log_login` WRITE;
/*!40000 ALTER TABLE `sys_log_login` DISABLE KEYS */;
INSERT INTO `sys_log_login` VALUES (42,'admin','127.0.0.1','内网IP','未知','未知',1,'登录成功','2026-03-23 19:49:24'),(43,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 19:51:52'),(44,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:09:11'),(45,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:13:44'),(46,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:17:24'),(47,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:17:30'),(48,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:17:36'),(49,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:18:55'),(50,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:19:00'),(51,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:20:25'),(52,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:22:26'),(53,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:32:41'),(54,'admin','127.0.0.1','内网IP','未知','未知',1,'登录成功','2026-03-23 20:33:58'),(55,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:34:28'),(56,'admin','127.0.0.1','内网IP','未知','未知',1,'登录成功','2026-03-23 20:35:13'),(57,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:38:41'),(58,'admin','111.60.0.86','未知','Chrome','Linux',1,'登录成功','2026-03-23 20:43:56'),(59,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-24 11:03:36'),(60,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-24 11:10:01'),(61,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 14:22:24'),(62,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 14:48:41'),(63,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 14:57:40'),(64,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 14:57:44'),(65,'wx_o5E_W5GTeXzQomvf','223.104.121.48','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 14:59:44'),(66,'wx_o5E_W5GTeXzQomvf','223.104.121.48','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 14:59:57'),(67,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 15:08:49'),(68,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-24 15:10:03'),(69,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 15:21:21'),(70,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:18:03'),(71,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:18:07'),(72,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:18:13'),(73,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:18:36'),(74,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:19:03'),(75,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:19:08'),(76,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:19:12'),(77,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:19:28'),(78,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:20:30'),(79,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:21:01'),(80,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-24 17:21:26'),(81,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:24:17'),(82,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:30:25'),(83,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:36:20'),(84,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-24 17:42:25'),(85,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-25 09:02:44'),(86,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 09:11:35'),(87,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-25 09:14:08'),(88,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-25 09:14:50'),(89,'test','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-25 09:15:49'),(90,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-25 09:16:04'),(91,'admin','127.0.0.1','内网IP','未知','未知',1,'登录成功','2026-03-25 09:27:49'),(92,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-25 09:28:22'),(93,'admin','127.0.0.1','内网IP','未知','未知',1,'登录成功','2026-03-25 09:29:50'),(94,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 09:37:01'),(95,'wx_o5E_W5IG1oulS63e','182.126.222.177','未知','未知','Mac OS',1,'微信登录成功','2026-03-25 09:41:31'),(96,'wx_o5E_W5M7nMvaRHVF','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 09:42:18'),(97,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-25 09:48:59'),(98,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-25 10:09:58'),(99,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 10:59:39'),(100,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Safari','Mac OS',1,'微信登录成功','2026-03-25 11:01:35'),(101,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 13:46:54'),(102,'wx_o5E_W5GTeXzQomvf','111.60.0.86','未知','Chrome','Linux',1,'微信登录成功','2026-03-25 20:24:56'),(103,'wx_o5E_W5M7nMvaRHVF','111.60.0.86','未知','Chrome','Linux',1,'微信登录成功','2026-03-26 00:03:53'),(104,'wx_o5E_W5GTeXzQomvf','183.95.75.68','未知','Chrome','Linux',1,'微信登录成功','2026-03-26 09:55:27'),(105,'admin','183.95.75.68','未知','Chrome','Windows',1,'登录成功','2026-03-26 11:51:07'),(106,'wx_o5E_W5GTeXzQomvf','223.104.122.59','未知','Chrome','Linux',1,'微信登录成功','2026-03-26 17:25:16'),(107,'wx_o5E_W5GTeXzQomvf','111.60.0.86','未知','Chrome','Linux',1,'微信登录成功','2026-03-26 22:45:34'),(108,'wx_o5E_W5GTeXzQomvf','223.104.122.51','未知','Chrome','Linux',1,'微信登录成功','2026-03-27 15:36:39');
/*!40000 ALTER TABLE `sys_log_login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` tinyint(1) DEFAULT '1' COMMENT '类型（1：通知，2：公告）',
  `level` tinyint(1) DEFAULT '1' COMMENT '级别（1：普通，2：重要，3：紧急）',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：草稿，1：已发布）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色描述',
  `data_scope` tinyint(1) DEFAULT '1' COMMENT '数据权限范围（1：全部，2：本部门及以下，3：本部门，4：仅本人，5：自定义）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_code` (`role_code`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','SUPER_ADMIN','拥有系统所有权限',1,1,0,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(2,'系统管理员','ADMIN','拥有系统管理权限',2,1,1,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(3,'普通用户','USER','普通用户权限',4,1,2,NULL,'2026-01-14 14:59:02','system','2026-01-14 14:59:02',NULL,0),(4,'小程序用户','MINI_SERVICE',NULL,2,1,4,'小程序用户角色\n','2026-03-24 15:12:07','system','2026-03-24 15:12:07','system',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别（0：未知，1：男，2：女）',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  KEY `idx_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$dptAyYkp3nrMNff2X7fT1uZSVS6I7KxdCyEI0MhD/9yWfe57togM.','超级管理员','admin@example.com','13800138000',NULL,1,1,1,NULL,'2026-01-14 14:59:03','system','2026-01-22 13:37:00',NULL,0),(2,'test','$2a$10$dptAyYkp3nrMNff2X7fT1uZSVS6I7KxdCyEI0MhD/9yWfe57togM.','测试用户','test@example.com','13800138001',NULL,1,5,1,NULL,'2026-01-14 14:59:03','system','2026-01-22 13:37:00',NULL,0),(3,'wx_o5E_W5GTeXzQomvf','$2a$10$sK9Z0GULGB53BAX1R.eKCeVXvArKpeeKL482qvSbx1zf6lvRFTbpm','微信用户',NULL,NULL,NULL,0,NULL,1,NULL,'2026-03-24 14:22:14','system','2026-03-24 14:22:14','system',0),(4,'wx_o5E_W5IG1oulS63e','$2a$10$mLW.QM7Gtq6fP0kHmp/IXepYns8E2ucpGoumKrNhyrPE5K.qQthsK','微信用户',NULL,NULL,NULL,0,NULL,1,NULL,'2026-03-25 09:41:31','system','2026-03-25 09:41:31','system',0),(5,'wx_o5E_W5M7nMvaRHVF','$2a$10$uBNGgMhf8ale96CoWcdIY.SNDHotgsgAP1ZE4cI0PogapuQ49JFcq','微信用户',NULL,NULL,NULL,0,NULL,1,NULL,'2026-03-25 09:42:18','system','2026-03-25 09:42:18','system',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_oauth`
--

DROP TABLE IF EXISTS `sys_user_oauth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_oauth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '系统用户ID',
  `oauth_type` varchar(20) NOT NULL COMMENT '第三方平台类型（github/wechat/gitee）',
  `oauth_id` varchar(100) NOT NULL COMMENT '第三方平台用户唯一标识',
  `oauth_name` varchar(100) DEFAULT NULL COMMENT '第三方平台用户名',
  `oauth_avatar` varchar(500) DEFAULT NULL COMMENT '第三方平台头像',
  `oauth_email` varchar(200) DEFAULT NULL COMMENT '第三方平台邮箱',
  `access_token` varchar(500) DEFAULT NULL COMMENT 'access_token',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_oauth` (`oauth_type`,`oauth_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户第三方登录绑定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_oauth`
--

LOCK TABLES `sys_user_oauth` WRITE;
/*!40000 ALTER TABLE `sys_user_oauth` DISABLE KEYS */;
INSERT INTO `sys_user_oauth` VALUES (2,1,'','','',NULL,NULL,NULL,'2026-02-11 15:47:52','2026-02-27 15:57:17'),(4,2,'feishu','ou_a4eb7aa95a1f4a3df0f9632377c4379e','刘意念',NULL,NULL,NULL,'2026-02-27 15:57:35','2026-02-27 15:57:35'),(5,3,'wechat','o5E_W5GTeXzQomvfr6eHipE0bFnM',NULL,NULL,NULL,NULL,'2026-03-23 18:37:55','2026-03-23 18:37:54'),(7,4,'wechat','o5E_W5IG1oulS63esmgXNLSCzzgM',NULL,NULL,NULL,NULL,'2026-03-25 09:41:31','2026-03-25 09:41:30'),(8,5,'wechat','o5E_W5M7nMvaRHVFmee_zClFFMfY',NULL,NULL,NULL,NULL,'2026-03-25 09:42:18','2026-03-25 09:42:18');
/*!40000 ALTER TABLE `sys_user_oauth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,1,'2026-01-14 14:59:03'),(2,2,3,'2026-01-14 14:59:03'),(3,3,2,'2026-03-24 14:22:13'),(4,4,4,'2026-03-25 09:41:30'),(5,5,4,'2026-03-25 09:42:18');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_department`
--

DROP TABLE IF EXISTS `sys_role_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `department_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_dept` (`role_id`,`department_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_department`
--

LOCK TABLES `sys_role_department` WRITE;
/*!40000 ALTER TABLE `sys_role_department` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_department` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-27 19:12:36
