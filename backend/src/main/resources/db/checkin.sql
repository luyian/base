-- ----------------------------
-- 小程序打卡功能建表（按用户维度隔离）
-- 计划模板表 checkin_plan + 每日打卡记录表 checkin_record
-- ----------------------------

-- ----------------------------
-- Table: checkin_plan 打卡计划表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `checkin_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(50) NOT NULL COMMENT '计划名称',
  `icon` varchar(20) DEFAULT NULL COMMENT '图标（emoji）',
  `color` varchar(20) DEFAULT NULL COMMENT '卡片颜色（hex）',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `plan_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '计划类型（0长期计划 1单日事件）',
  `target_date` date DEFAULT NULL COMMENT '目标日期（仅单日事件）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号（越小越靠前）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `deleted_time` datetime DEFAULT NULL COMMENT '删除时间（用于历史统计）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_target_date` (`target_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='打卡计划表';

-- ----------------------------
-- Table: checkin_record 每日打卡记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `checkin_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_plan_date` (`plan_id`,`checkin_date`) USING BTREE,
  KEY `idx_user_date` (`user_id`,`checkin_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='每日打卡记录表';
