-- 高考数据菜单与权限初始化

INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(10, 0, '高考数据', NULL, 1, '/gaokao', NULL, 'School', 10, 1, 1, '高考志愿数据查询模块', NOW(), 'system', NOW(), NULL, 0),
(10001, 10, '院校录取分数', 'gaokao:college-score:list', 2, '/gaokao/college-scores', 'gaokao/CollegeScores', 'DataLine', 1, 1, 1, '院校录取分数线查询', NOW(), 'system', NOW(), NULL, 0),
(10002, 10, '专业录取分数', 'gaokao:major-score:list', 2, '/gaokao/major-scores', 'gaokao/MajorScores', 'Histogram', 2, 1, 1, '专业录取分数线查询', NOW(), 'system', NOW(), NULL, 0),
(10003, 10, '招生计划', 'gaokao:admission-plan:list', 2, '/gaokao/admission-plans', 'gaokao/AdmissionPlans', 'Tickets', 3, 1, 1, '专业招生计划查询', NOW(), 'system', NOW(), NULL, 0)
ON DUPLICATE KEY UPDATE
  `parent_id` = VALUES(`parent_id`),
  `permission_name` = VALUES(`permission_name`),
  `permission_code` = VALUES(`permission_code`),
  `type` = VALUES(`type`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `icon` = VALUES(`icon`),
  `sort` = VALUES(`sort`),
  `visible` = VALUES(`visible`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_time` = NOW(),
  `deleted` = 0;

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`, `create_by`, `deleted`) VALUES
(1, 10, NOW(), 'system', 0),
(1, 10001, NOW(), 'system', 0),
(1, 10002, NOW(), 'system', 0),
(1, 10003, NOW(), 'system', 0),
(2, 10, NOW(), 'system', 0),
(2, 10001, NOW(), 'system', 0),
(2, 10002, NOW(), 'system', 0),
(2, 10003, NOW(), 'system', 0)
ON DUPLICATE KEY UPDATE
  `deleted` = 0,
  `update_time` = NOW();
