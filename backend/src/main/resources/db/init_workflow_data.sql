-- ----------------------------
-- 初始化工单提交流程
-- ----------------------------

-- 1. 插入流程定义
INSERT INTO sys_process_definition 
(process_key, process_name, category, version, status, description, create_time, create_by)
VALUES 
('work_order_submit', '工单提交流程', '工单管理', 1, 1, '简单的工单提交流程，提交后由研发部成员审批', NOW(), 'system');

-- 2. 插入节点配置
SET @process_id = LAST_INSERT_ID();

INSERT INTO sys_process_node (process_id, node_key, node_name, node_type, candidate_type, candidate_config, approve_type, approve_ratio, can_rollback, position_x, position_y, create_time, update_time)
VALUES 
(@process_id, 'start', '开始', 'START', NULL, NULL, NULL, NULL, 0, 100, 200, NOW(), NOW()),
(@process_id, 'submit_to_dev', '提交到研发部', 'APPROVAL', 'DEPARTMENT', '{"deptIds":[5],"includeChildren":true,"selectType":"ALL_USER"}', 'OR_SIGN', 1.0, 0, 300, 200, NOW(), NOW()),
(@process_id, 'end', '结束', 'END', NULL, NULL, NULL, NULL, 0, 500, 200, NOW(), NOW());

-- 3. 插入节点关系
INSERT INTO sys_process_node_relation (process_id, source_node_key, target_node_key, relation_name, create_time)
VALUES 
(@process_id, 'start', 'submit_to_dev', '提交', NOW()),
(@process_id, 'submit_to_dev', 'end', '通过', NOW());
