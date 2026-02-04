-- 登录日志导出配置初始化
-- 执行时间: 2026-02-04

-- 插入导出配置
INSERT INTO `sys_export_config` (
    `config_code`, `config_name`, `description`, `data_source_type`,
    `data_source_bean`, `data_source_method`, `query_param_class`,
    `batch_size`, `max_export_count`, `file_name_pattern`,
    `enable_multi_sheet`, `sheet_max_rows`, `status`, `sort`
) VALUES (
    'login_log_export', '登录日志导出', '导出系统登录日志记录', 'SERVICE',
    'loginLogServiceImpl', 'exportPage', 'com.base.system.dto.log.LoginLogQueryRequest',
    5000, 1000000, '登录日志',
    1, 100000, 1, 1
);

-- 获取刚插入的配置ID
SET @config_id = LAST_INSERT_ID();

-- 插入字段配置
INSERT INTO `sys_export_field` (`config_id`, `field_name`, `field_label`, `field_type`, `field_width`, `field_format`, `dict_type`, `mask_type`, `sort`, `status`) VALUES
(@config_id, 'id', '日志ID', 'NUMBER', 10, NULL, NULL, NULL, 1, 1),
(@config_id, 'username', '用户名', 'STRING', 15, NULL, NULL, NULL, 2, 1),
(@config_id, 'loginIp', '登录IP', 'STRING', 15, NULL, NULL, NULL, 3, 1),
(@config_id, 'loginLocation', '登录地点', 'STRING', 20, NULL, NULL, NULL, 4, 1),
(@config_id, 'browser', '浏览器', 'STRING', 25, NULL, NULL, NULL, 5, 1),
(@config_id, 'os', '操作系统', 'STRING', 25, NULL, NULL, NULL, 6, 1),
(@config_id, 'status', '登录状态', 'STRING', 10, NULL, 'login_status', NULL, 7, 1),
(@config_id, 'message', '提示信息', 'STRING', 30, NULL, NULL, NULL, 8, 1),
(@config_id, 'createTime', '登录时间', 'DATETIME', 20, 'yyyy-MM-dd HH:mm:ss', NULL, NULL, 9, 1);

-- 插入登录状态字典（如果不存在）
INSERT IGNORE INTO `sys_enum` (`enum_type`, `enum_value`, `description`, `sort`, `status`) VALUES
('login_status', '0', '失败', 1, 1),
('login_status', '1', '成功', 2, 1);
