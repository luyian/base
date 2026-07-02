-- 高考志愿招生计划表结构
-- 说明：candidate_province_code/name 表示考生省份，用于隔离不同省份招生计划数据。

CREATE TABLE IF NOT EXISTS `edu_major_admission_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `admission_year` smallint NOT NULL COMMENT '招生年份',
  `candidate_province_code` varchar(12) NOT NULL COMMENT '考生省份编码，河南为410000',
  `candidate_province_name` varchar(32) NOT NULL COMMENT '考生省份名称',
  `batch_name` varchar(32) NOT NULL COMMENT '批次',
  `subject_category` varchar(32) NOT NULL COMMENT '科类，如物理类、历史类',
  `college_code` varchar(32) NOT NULL COMMENT '招生院校代码，保留前导零',
  `college_name` varchar(128) NOT NULL COMMENT '院校名称',
  `major_group_code` varchar(32) NOT NULL COMMENT '专业组代码',
  `major_code` varchar(32) NOT NULL COMMENT '专业代码，保留前导零',
  `major_name` varchar(128) NOT NULL COMMENT '专业名称',
  `major_remark` varchar(500) DEFAULT NULL COMMENT '专业备注',
  `selection_requirement` varchar(128) NOT NULL COMMENT '选科要求',
  `study_years` tinyint unsigned DEFAULT NULL COMMENT '学制',
  `tuition_fee` varchar(32) DEFAULT NULL COMMENT '学费',
  `plan_count` int unsigned NOT NULL COMMENT '计划人数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_plan_business` (`candidate_province_code`, `admission_year`, `batch_name`, `subject_category`, `college_code`, `major_group_code`, `major_code`, `deleted`) USING BTREE,
  KEY `idx_plan_query` (`candidate_province_code`, `admission_year`, `subject_category`, `batch_name`, `plan_count`) USING BTREE,
  KEY `idx_college_group` (`candidate_province_code`, `admission_year`, `college_code`, `subject_category`, `batch_name`, `major_group_code`) USING BTREE,
  KEY `idx_major_name` (`major_name`) USING BTREE,
  KEY `idx_major_code` (`major_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='高考专业招生计划表';
