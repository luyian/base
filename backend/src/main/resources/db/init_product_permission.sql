-- 条码/二维码能力层 菜单与权限初始化
-- 结构：商品管理（一级目录，可见）
--           -> 商品管理（二级菜单，商品列表+商品条码生成，权限码 common:barcode:use）
--           -> 生成条码（二级菜单，独立页批量生成条码/二维码）
-- 商品 CRUD 登录即用（按用户隔离，不上菜单权限锁定）
-- 通用条码/二维码能力（生成/识别/登记）受 common:barcode:use 权限管控
-- 授权角色：1 超级管理员、2 系统管理员（PC端）、4 小程序用户（扫码识别）

INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`) VALUES
-- 一级目录：商品管理（type=1 目录，path 仅作展示，不产生路由）
(130000, 0, '商品管理', NULL, 1, '/product', NULL, 'Goods', 13, 1, 1, '商品管理与条码/二维码工具', NOW(), 'system', NOW(), NULL, 0),
-- 二级菜单：商品管理（type=2 菜单，产生 /product 路由，权限码 common:barcode:use）
(130001, 130000, '商品管理', 'common:barcode:use', 2, '/product', 'product/ProductList', 'Goods', 1, 1, 1, '商品列表、新增编辑删除、商品条码生成/下载', NOW(), 'system', NOW(), NULL, 0),
-- 二级菜单：生成条码（type=2 菜单，产生 /barcode/generate 路由；权限保护由接口 common:barcode:use 承担，name 自动生成避免与上级同名冲突）
(130002, 130000, '条码管理', NULL, 2, '/barcode/generate', 'barcode/GenerateBarcode', 'aim', 2, 1, 1, '独立页批量生成条码/二维码并下载', NOW(), 'system', NOW(), NULL, 0)
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
(1, 130000, NOW(), 'system', 0),
(1, 130001, NOW(), 'system', 0),
(1, 130002, NOW(), 'system', 0),
(2, 130000, NOW(), 'system', 0),
(2, 130001, NOW(), 'system', 0),
(2, 130002, NOW(), 'system', 0),
(4, 130000, NOW(), 'system', 0),
(4, 130001, NOW(), 'system', 0),
(4, 130002, NOW(), 'system', 0)
ON DUPLICATE KEY UPDATE
  `deleted` = 0;