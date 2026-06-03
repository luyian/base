-- ----------------------------
-- 天气地图菜单初始化 SQL
-- 放在系统管理（ID=1）下
-- ----------------------------

-- 天气地图菜单（挂在系统管理下）
INSERT INTO `sys_permission` VALUES (112, 1, '天气地图', 'system:weather:list', 2, '/weather', 'WeatherMap', 'Sunny', 12, 1, 1, '全国实时天气地图', NOW(), 'system', NOW(), NULL, 0);

-- 给超级管理员角色(role_id=1)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 112);

-- 给系统管理员角色(role_id=2)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (2, 112);

-- ----------------------------
-- 天气 API Key 配置（sys_config）
-- ----------------------------
INSERT INTO `sys_config` (config_key, config_value, config_name, type, status, remark, create_time, create_by)
VALUES
('weather.amap.key', 'ac6eab2fe5d85b345b726b13cbdbbef1', '高德天气API Key', 'string', 1, '高德开放平台Web服务Key，用于天气查询', NOW(), 'system'),
('weather.hefeng.key', '', '和风天气API Key', 'string', 1, '和风天气开发者Key，用于天气查询降级', NOW(), 'system'),
('weather.seniverse.key', '', '心知天气API Key', 'string', 1, '心知天气API Key，用于天气查询降级', NOW(), 'system');
