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
-- Table structure for table `kb_directory`
--

DROP TABLE IF EXISTS `kb_directory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_directory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `knowledge_base_id` bigint NOT NULL COMMENT '所属知识库ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父目录ID（0表示根目录）',
  `name` varchar(100) NOT NULL COMMENT '目录名称',
  `sort` int DEFAULT '0' COMMENT '目录排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_base_id` (`knowledge_base_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='目录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kb_document`
--

DROP TABLE IF EXISTS `kb_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `knowledge_base_id` bigint NOT NULL COMMENT '所属知识库ID',
  `title` varchar(200) NOT NULL COMMENT '文档标题',
  `content` longtext COMMENT 'MD文档内容',
  `directory_id` bigint DEFAULT NULL COMMENT '所属目录ID',
  `tags` varchar(1000) DEFAULT NULL COMMENT '标签列表（JSON格式存储）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_base_id` (`knowledge_base_id`),
  KEY `idx_directory_id` (`directory_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文档表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kb_document_tag`
--

DROP TABLE IF EXISTS `kb_document_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_tag` (`document_id`,`tag_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文档-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kb_knowledge_base`
--

DROP TABLE IF EXISTS `kb_knowledge_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_knowledge_base` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '知识库名称',
  `description` varchar(500) DEFAULT NULL COMMENT '知识库描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kb_tag`
--

DROP TABLE IF EXISTS `kb_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `knowledge_base_id` bigint NOT NULL COMMENT '所属知识库ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_base_name` (`knowledge_base_id`,`name`),
  KEY `idx_knowledge_base_id` (`knowledge_base_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msg_subscription`
--

DROP TABLE IF EXISTS `msg_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `msg_subscription` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sub_type` varchar(50) NOT NULL COMMENT '订阅类型：fund_valuation-基金估值',
  `channel` varchar(50) NOT NULL DEFAULT 'feishu' COMMENT '推送渠道：feishu/dingtalk/email',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `last_push_time` datetime DEFAULT NULL COMMENT '最后推送时间',
  `last_push_status` tinyint DEFAULT NULL COMMENT '最后推送状态：0-失败，1-成功',
  `last_push_msg` varchar(500) DEFAULT NULL COMMENT '最后推送结果描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_channel` (`user_id`,`sub_type`,`channel`),
  KEY `idx_sub_type_status` (`sub_type`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息订阅表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_api_token`
--

DROP TABLE IF EXISTS `stk_api_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_api_token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `token_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Token值',
  `token_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Token名称/备注',
  `provider` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'itick' COMMENT '服务商',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-作废, 1-正常）',
  `last_used_time` datetime DEFAULT NULL COMMENT '最后使用时间',
  `use_count` int NOT NULL DEFAULT '0' COMMENT '使用次数',
  `daily_limit` int NOT NULL DEFAULT '0' COMMENT '每日限额（0表示无限制）',
  `daily_used` int NOT NULL DEFAULT '0' COMMENT '当日已用次数',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '连续失败次数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（null表示永不过期）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_provider_status` (`provider`,`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='API Token管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_data_mapping`
--

DROP TABLE IF EXISTS `stk_data_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_data_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mapping_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '映射编码（唯一标识）',
  `mapping_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '映射名称',
  `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源数据类型',
  `target_table` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标表名',
  `field_mapping` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段映射配置（JSON格式）',
  `transform_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '转换脚本（可选）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-禁用, 1-启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_mapping_code` (`mapping_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据映射配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_fund_config`
--

DROP TABLE IF EXISTS `stk_fund_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_fund_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fund_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '基金名称',
  `fund_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '基金代码（可选）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_fund_code` (`fund_code`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='基金配置主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_fund_holding`
--

DROP TABLE IF EXISTS `stk_fund_holding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_fund_holding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `weight` decimal(8,4) NOT NULL COMMENT '权重占比(%)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_fund_stock` (`fund_id`,`stock_code`) USING BTREE,
  KEY `idx_fund_id` (`fund_id`) USING BTREE,
  KEY `idx_stock_code` (`stock_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='基金持仓明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_fund_valuation_record`
--

DROP TABLE IF EXISTS `stk_fund_valuation_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_fund_valuation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `estimated_change_percent` decimal(10,4) DEFAULT NULL COMMENT '估算涨跌幅(%)',
  `holding_count` int DEFAULT NULL COMMENT '持仓数量',
  `success_count` int DEFAULT NULL COMMENT '成功获取报价数量',
  `fail_count` int DEFAULT NULL COMMENT '失败获取报价数量',
  `total_weight` decimal(10,4) DEFAULT NULL COMMENT '总权重(%)',
  `quotes_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '股票报价明细（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_fund_date` (`fund_id`,`trade_date`) USING BTREE,
  KEY `idx_trade_date` (`trade_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='基金估值记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_fund_watchlist`
--

DROP TABLE IF EXISTS `stk_fund_watchlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_fund_watchlist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_fund_user` (`fund_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='基金自选表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_kline_daily`
--

DROP TABLE IF EXISTS `stk_kline_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_kline_daily` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `open_price` decimal(12,4) DEFAULT NULL COMMENT '开盘价',
  `high_price` decimal(12,4) DEFAULT NULL COMMENT '最高价',
  `low_price` decimal(12,4) DEFAULT NULL COMMENT '最低价',
  `close_price` decimal(12,4) DEFAULT NULL COMMENT '收盘价',
  `volume` bigint DEFAULT NULL COMMENT '成交量',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '成交额',
  `change_rate` decimal(10,4) DEFAULT NULL COMMENT '涨跌幅(%)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_date` (`stock_code`,`trade_date`) USING BTREE,
  KEY `idx_trade_date` (`trade_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=713413 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='K线数据表（日K）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_recommend`
--

DROP TABLE IF EXISTS `stk_recommend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_recommend` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `recommend_date` date NOT NULL COMMENT '推荐日期',
  `total_score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总分',
  `hit_rule_count` int NOT NULL DEFAULT '0' COMMENT '命中规则数',
  `total_rule_count` int NOT NULL DEFAULT '0' COMMENT '总规则数',
  `hit_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '命中率（%）',
  `score_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '得分明细（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_date` (`stock_code`,`recommend_date`) USING BTREE,
  KEY `idx_recommend_date_score` (`recommend_date`,`total_score` DESC) USING BTREE,
  KEY `idx_recommend_date_rank` (`recommend_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25517 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='推荐股票表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_score_record`
--

DROP TABLE IF EXISTS `stk_score_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_score_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `rule_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则编码',
  `score_date` date NOT NULL COMMENT '打分日期',
  `score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '得分',
  `weighted_score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '加权得分（得分 * 权重）',
  `hit_flag` tinyint NOT NULL DEFAULT '0' COMMENT '命中标志（0-未命中, 1-命中）',
  `detail_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '打分详情（JSON格式，记录计算过程）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_rule_date` (`stock_code`,`rule_code`,`score_date`) USING BTREE,
  KEY `idx_score_date` (`score_date`) USING BTREE,
  KEY `idx_stock_date` (`stock_code`,`score_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=231466 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打分记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_score_rule`
--

DROP TABLE IF EXISTS `stk_score_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_score_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则编码（唯一标识，对应策略Bean名称）',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `rule_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规则描述',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'TECHNICAL' COMMENT '规则分类（TECHNICAL-技术面, FUNDAMENTAL-基本面）',
  `score_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FIXED' COMMENT '打分类型（FIXED-固定分, DYNAMIC-动态分）',
  `base_score` int NOT NULL DEFAULT '10' COMMENT '基础分数',
  `max_score` int NOT NULL DEFAULT '10' COMMENT '最高分数（动态打分时使用）',
  `weight` decimal(5,2) NOT NULL DEFAULT '1.00' COMMENT '权重系数（最终得分 = 规则得分 * 权重）',
  `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '规则参数配置（JSON格式，如均线周期、阈值等）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-禁用, 1-启用）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号（执行顺序）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_rule_code` (`rule_code`) USING BTREE,
  KEY `idx_status_sort` (`status`,`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打分规则配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_stock_info`
--

DROP TABLE IF EXISTS `stk_stock_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_stock_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票名称',
  `market` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '市场（SH-沪市, SZ-深市, HK-港股）',
  `exchange` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '交易所',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '交易货币（CNY/HKD）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-退市, 1-正常）',
  `stock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '股票类型（stock-股票）',
  `sector` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属板块',
  `industry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属行业',
  `business_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '公司简介',
  `website_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司网站URL',
  `market_cap` decimal(20,2) DEFAULT NULL COMMENT '总市值',
  `total_shares` decimal(20,2) DEFAULT NULL COMMENT '总股本',
  `pe_ratio` decimal(10,4) DEFAULT NULL COMMENT '市盈率',
  `high_52_week` decimal(10,4) DEFAULT NULL COMMENT '52周最高价',
  `low_52_week` decimal(10,4) DEFAULT NULL COMMENT '52周最低价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_code` (`stock_code`) USING BTREE,
  KEY `idx_market` (`market`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10318 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='股票基础信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_sync_failure`
--

DROP TABLE IF EXISTS `stk_sync_failure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_sync_failure` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `failure_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '失败原因',
  `retry_count` int DEFAULT '0' COMMENT '重试次数',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待重试，1-重试成功，2-放弃重试',
  `last_retry_time` datetime DEFAULT NULL COMMENT '最后重试时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_stock_code` (`stock_code`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='股票同步失败记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stk_watchlist`
--

DROP TABLE IF EXISTS `stk_watchlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stk_watchlist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '股票代码',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_stock` (`user_id`,`stock_code`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='自选股票表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `sys_log_operation`
--

DROP TABLE IF EXISTS `sys_log_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作模块',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作类型',
  `operation_type` tinyint DEFAULT NULL COMMENT '操作类型（1-新增 2-修改 3-删除 4-查询 5-导出 6-导入 7-其他）',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求方法（GET/POST/PUT/DELETE等）',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求URL',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '返回结果',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作地点',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户代理',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：失败，1：成功）',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误信息',
  `execute_time` int DEFAULT '0' COMMENT '执行时长（毫秒）',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `sys_notice_read`
--

DROP TABLE IF EXISTS `sys_notice_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_notice_user` (`notice_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='通知阅读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限编码',
  `type` tinyint(1) NOT NULL COMMENT '类型（1：目录，2：菜单，3：按钮）',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见（0：隐藏，1：显示）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60304 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='权限/菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_region`
--

DROP TABLE IF EXISTS `sys_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区划代码',
  `region_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区划名称',
  `level` tinyint NOT NULL COMMENT '层级（1-省，2-市，3-区）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-禁用，1-正常）',
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整名称',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简称',
  `pinyin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拼音',
  `pinyin_prefix` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拼音首字母',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk` (`region_code`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_level` (`level`) USING BTREE,
  KEY `idx_region_name` (`region_name`) USING BTREE,
  KEY `idx_pinyin_prefix` (`pinyin_prefix`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3425 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='行政区划表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `deleted` int DEFAULT '0' COMMENT '删除标志（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE,
  KEY `idx_permission_id` (`permission_id`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1202 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-27 19:12:12
