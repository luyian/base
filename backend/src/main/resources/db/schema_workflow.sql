-- ----------------------------
-- 流程引擎表结构
-- ----------------------------

-- ----------------------------
-- Table structure for sys_process_definition
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_definition`;
CREATE TABLE `sys_process_definition`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程标识（唯一）',
  `process_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程分类',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-草稿, 1-已发布, 2-禁用）',
  `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '流程配置JSON',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_process_key_version`(`process_key` ASC, `version` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_process_node
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_node`;
CREATE TABLE `sys_process_node`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_id` bigint NOT NULL COMMENT '所属流程定义ID',
  `node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点标识',
  `node_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点名称',
  `node_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点类型（START-开始, END-结束, APPROVAL-审批, CONDITION-条件网关, PARALLEL-并行网关）',
  `event_handler` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件处理器类名',
  `candidate_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '候选人类型（USER-用户, ROLE-角色, DEPARTMENT-部门, DEPARTMENT_LEADER-部门负责人, INITIATOR_DEPT-发起人部门, EXPRESSION-表达式）',
  `candidate_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '候选人配置JSON',
  `approve_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批类型（SEQUENCE-顺序, COUNTER_SIGN-会签, OR_SIGN-或签）',
  `approve_ratio` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '审批通过比例',
  `can_rollback` tinyint NOT NULL DEFAULT 0 COMMENT '是否可回退（0-否, 1-是）',
  `rollback_nodes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可回退节点列表（逗号分隔）',
  `position_x` int NULL DEFAULT 0 COMMENT '可视化X坐标',
  `position_y` int NULL DEFAULT 0 COMMENT '可视化Y坐标',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_process_node`(`process_id` ASC, `node_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程节点配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_process_node_relation
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_node_relation`;
CREATE TABLE `sys_process_node_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_id` bigint NOT NULL COMMENT '所属流程定义ID',
  `source_node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源节点标识',
  `target_node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标节点标识',
  `condition_expr` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条件表达式',
  `relation_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连线名称',
  `position_x` int NULL DEFAULT 0 COMMENT '可视化X坐标',
  `position_y` int NULL DEFAULT 0 COMMENT '可视化Y坐标',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程节点关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_process_instance
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_instance`;
CREATE TABLE `sys_process_instance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程标识',
  `process_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程名称',
  `process_definition_id` bigint NOT NULL COMMENT '流程定义ID',
  `business_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务主键（关联业务表）',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型',
  `current_node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前节点标识',
  `current_node_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前节点名称',
  `initiator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发起人',
  `initiator_dept_id` bigint NULL DEFAULT NULL COMMENT '发起人部门ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'RUNNING' COMMENT '流程状态（RUNNING-进行中, COMPLETED-已完成, TERMINATED-已终止, ROLLED_BACK-已回退）',
  `variables` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '流程变量JSON',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程标题',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_process_key`(`process_key` ASC) USING BTREE,
  INDEX `idx_business_key`(`business_key` ASC) USING BTREE,
  INDEX `idx_initiator`(`initiator` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程实例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_process_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_task`;
CREATE TABLE `sys_process_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `instance_id` bigint NOT NULL COMMENT '流程实例ID',
  `process_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程标识',
  `node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点标识',
  `node_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点名称',
  `task_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NORMAL' COMMENT '任务类型（NORMAL-普通, PARALLEL-并行）',
  `assignee` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '办理人',
  `candidate_users` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '候选人JSON（并行任务）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING-待办, COMPLETED-已办, REJECTED-已拒绝, DELEGATED-已转办）',
  `approve_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批类型（SEQUENCE-顺序, COUNTER_SIGN-会签, OR_SIGN-或签）',
  `approve_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批结果（APPROVE-通过, REJECT-拒绝）',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批意见',
  `claim_time` datetime NULL DEFAULT NULL COMMENT '签收时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE,
  INDEX `idx_assignee`(`assignee` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_process_history
-- ----------------------------
DROP TABLE IF EXISTS `sys_process_history`;
CREATE TABLE `sys_process_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `instance_id` bigint NOT NULL COMMENT '流程实例ID',
  `process_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程标识',
  `node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点标识',
  `node_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点名称',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作人',
  `operator_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `action` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型（START-启动, APPROVE-审批, REJECT-拒绝, ROLLBACK-回退, DELEGATE-转办, TERMINATE-终止）',
  `assignee` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被办理人',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程历史记录表' ROW_FORMAT = Dynamic;
