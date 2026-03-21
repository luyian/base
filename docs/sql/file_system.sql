-- 文件管理表
CREATE TABLE IF NOT EXISTS `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名（FastDFS路径）',
  `original_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `file_ext` varchar(50) DEFAULT NULL COMMENT '文件扩展名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型（MIME类型）',
  `file_path` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件访问URL',
  `file_group` varchar(100) DEFAULT 'default' COMMENT '文件分组',
  `file_desc` varchar(500) DEFAULT NULL COMMENT '文件说明',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传人ID',
  `upload_user_name` varchar(100) DEFAULT NULL COMMENT '上传人名称',
  `status` tinyint DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_group` (`file_group`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件管理表';

-- 文件操作日志表
CREATE TABLE IF NOT EXISTS `sys_file_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `file_path` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `operation_type` tinyint DEFAULT NULL COMMENT '操作类型（1-上传 2-下载 3-删除 4-预览）',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人名称',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(200) DEFAULT NULL COMMENT '操作地点',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '请求UA',
  `status` tinyint DEFAULT 1 COMMENT '操作状态（0-失败 1-成功）',
  `error_msg` text COMMENT '错误信息',
  `execute_time` int DEFAULT NULL COMMENT '执行时长（毫秒）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件操作日志表';