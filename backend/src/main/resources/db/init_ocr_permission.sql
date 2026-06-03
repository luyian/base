-- ----------------------------
-- OCR 识别菜单初始化 SQL
-- 放在系统管理（ID=1）下
-- ----------------------------

-- OCR 识别菜单（挂在系统管理下）
INSERT INTO `sys_permission` VALUES (113, 1, 'OCR识别', 'system:ocr:list', 2, '/ocr', 'OcrRecognize', 'PictureFilled', 13, 1, 1, 'OCR智能识别（身份证/发票/银行卡）', NOW(), 'system', NOW(), NULL, 0);

-- 给超级管理员角色(role_id=1)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (1, 113);

-- 给系统管理员角色(role_id=2)分配权限
INSERT INTO `sys_role_permission` (role_id, permission_id) VALUES (2, 113);

-- ----------------------------
-- OCR API Key 配置（sys_config）
-- ----------------------------
INSERT INTO `sys_config` (config_key, config_value, config_name, type, status, remark, create_time, create_by)
VALUES
('ocr.tencent.secret-id', '', '腾讯云OCR SecretId', 'string', 1, '腾讯云API密钥ID，用于OCR文字识别', NOW(), 'system'),
('ocr.tencent.secret-key', '', '腾讯云OCR SecretKey', 'string', 1, '腾讯云API密钥Key，用于OCR文字识别', NOW(), 'system'),
('ocr.baidu.api-key', '', '百度云OCR ApiKey', 'string', 1, '百度云文字识别应用API Key', NOW(), 'system'),
('ocr.baidu.secret-key', '', '百度云OCR SecretKey', 'string', 1, '百度云文字识别应用Secret Key', NOW(), 'system'),
('ocr.aliyun.access-key-id', '', '阿里云OCR AccessKeyId', 'string', 1, '阿里云AccessKey ID，用于OCR文字识别', NOW(), 'system'),
('ocr.aliyun.access-key-secret', '', '阿里云OCR AccessKeySecret', 'string', 1, '阿里云AccessKey Secret，用于OCR文字识别', NOW(), 'system');
