-- 图片去水印功能菜单与权限初始化
-- 子菜单挂到系统管理(id=1)下
-- 权限码 system:imageWatermark:use（后端 ImageWatermarkController @PreAuthorize 已改为此）
-- 授权角色：1 超级管理员、2 系统管理员（PC端）、4 小程序用户（小程序去水印）

INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
(116, 1, '图片去水印', 'system:imageWatermark:use', 2, '/image-watermark', 'system/ImageWatermark', 'Picture', 16, 1, 1, '图片水印去除工具（自动识别/手动框选）', NOW(), 'system', NOW(), NULL, 0)
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
(1, 116, NOW(), 'system', 0),
(2, 116, NOW(), 'system', 0),
(4, 116, NOW(), 'system', 0)
ON DUPLICATE KEY UPDATE
  `deleted` = 0;