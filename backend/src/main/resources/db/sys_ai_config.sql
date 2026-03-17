-- ----------------------------
-- Table structure for sys_ai_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_ai_config`;
CREATE TABLE `sys_ai_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称（用于区分多条配置）',
  `base_url` varchar(500) NOT NULL COMMENT 'API 基础地址',
  `api_key` varchar(500) NOT NULL COMMENT 'API Key',
  `model` varchar(100) DEFAULT 'qwen-plus' COMMENT '模型名称',
  `timeout` int NOT NULL DEFAULT 30000 COMMENT '超时时间(ms)',
  `retry` int NOT NULL DEFAULT 2 COMMENT '重试次数',
  `max_message_length` int NOT NULL DEFAULT 2000 COMMENT '消息最大长度',
  `max_context_length` int NOT NULL DEFAULT 5000 COMMENT '上下文最大长度',
  `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否当前生效（0否 1是，仅一条可为1）',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1启用）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='大模型配置表（支持多条，选一条生效）';

-- 默认一条配置（示例，is_active=1 表示当前生效）
INSERT INTO `sys_ai_config` (`config_name`, `base_url`, `api_key`, `model`, `timeout`, `retry`, `max_message_length`, `max_context_length`, `is_active`, `status`, `sort_order`, `remark`) VALUES
('默认（通义千问）', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 'sk-your-api-key', 'qwen-plus', 30000, 2, 2000, 5000, 1, 1, 0, '请将 api_key 改为实际 Key 后保存，并点击「设为生效」');
