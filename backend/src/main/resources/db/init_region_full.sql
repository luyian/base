-- =============================================
-- 中国省市区三级行政区划数据
-- 数据源：https://github.com/modood/Administrative-divisions-of-China
-- 生成时间：2026-01-26 13:49:31
-- 数据总数：3424 条
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 表结构：sys_region
-- ----------------------------
DROP TABLE IF EXISTS `sys_region`;
CREATE TABLE `sys_region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级ID',
  `region_code` varchar(20) NOT NULL COMMENT '区划代码',
  `region_name` varchar(50) NOT NULL COMMENT '区划名称',
  `level` tinyint NOT NULL COMMENT '层级（1-省，2-市，3-区）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
  `full_name` varchar(200) DEFAULT NULL COMMENT '完整名称',
  `short_name` varchar(50) DEFAULT NULL COMMENT '简称',
  `pinyin` varchar(100) DEFAULT NULL COMMENT '拼音',
  `pinyin_prefix` varchar(20) DEFAULT NULL COMMENT '拼音首字母',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`region_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_region_name` (`region_name`),
  KEY `idx_pinyin_prefix` (`pinyin_prefix`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='行政区划表';

-- ----------------------------
-- 权限配置
-- ----------------------------
DELETE FROM sys_permission WHERE id >= 108 AND id <= 10805;
DELETE FROM sys_role_permission WHERE permission_id >= 108 AND permission_id <= 10805;

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, route_path, component_path, icon, sort, status, visible, remark, create_time, update_time, deleted)
VALUES (108, 1, '行政区划', 'system:region', 1, '/system/region', 'system/Region', 'Location', 8, 1, 1, '行政区划管理', NOW(), NOW(), 0);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, sort, status, visible, remark, create_time, update_time, deleted) VALUES
(10801, 108, '区划查询', 'system:region:query', 2, 1, 1, 1, '查询行政区划', NOW(), NOW(), 0),
(10802, 108, '区划新增', 'system:region:add', 2, 2, 1, 1, '新增行政区划', NOW(), NOW(), 0),
(10803, 108, '区划编辑', 'system:region:edit', 2, 3, 1, 1, '编辑行政区划', NOW(), NOW(), 0),
(10804, 108, '区划删除', 'system:region:delete', 2, 4, 1, 1, '删除行政区划', NOW(), NOW(), 0),
(10805, 108, '区划导入', 'system:region:import', 2, 5, 1, 1, '导入行政区划', NOW(), NOW(), 0);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 108), (1, 10801), (1, 10802), (1, 10803), (1, 10804), (1, 10805);

-- ----------------------------
-- 行政区划数据
-- ----------------------------

-- 省级数据（共 31 条）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '11', '北京市', 1, 1, 1, '北京市', '北京', 'Beijing', 'B', NOW(), NOW(), 0);
SET @北京_11_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '12', '天津市', 1, 2, 1, '天津市', '天津', 'Tianjin', 'T', NOW(), NOW(), 0);
SET @天津_12_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '13', '河北省', 1, 3, 1, '河北省', '河北', 'Hebei', 'H', NOW(), NOW(), 0);
SET @河北_13_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '14', '山西省', 1, 4, 1, '山西省', '山西', 'Shanxi', 'S', NOW(), NOW(), 0);
SET @山西_14_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '15', '内蒙古自治区', 1, 5, 1, '内蒙古自治区', '内蒙古自治', '内蒙古自治', '', NOW(), NOW(), 0);
SET @内蒙古自治_15_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '21', '辽宁省', 1, 6, 1, '辽宁省', '辽宁', 'Liaoning', 'L', NOW(), NOW(), 0);
SET @辽宁_21_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '22', '吉林省', 1, 7, 1, '吉林省', '吉林', 'Jilin', 'J', NOW(), NOW(), 0);
SET @吉林_22_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '23', '黑龙江省', 1, 8, 1, '黑龙江省', '黑龙江', 'Heilongjiang', 'H', NOW(), NOW(), 0);
SET @黑龙江_23_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '31', '上海市', 1, 9, 1, '上海市', '上海', 'Shanghai', 'S', NOW(), NOW(), 0);
SET @上海_31_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '32', '江苏省', 1, 10, 1, '江苏省', '江苏', 'Jiangsu', 'J', NOW(), NOW(), 0);
SET @江苏_32_id = LAST_INSERT_ID();

-- 已插入 10/31 个省级区划

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '33', '浙江省', 1, 11, 1, '浙江省', '浙江', 'Zhejiang', 'Z', NOW(), NOW(), 0);
SET @浙江_33_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '34', '安徽省', 1, 12, 1, '安徽省', '安徽', 'Anhui', 'A', NOW(), NOW(), 0);
SET @安徽_34_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '35', '福建省', 1, 13, 1, '福建省', '福建', 'Fujian', 'F', NOW(), NOW(), 0);
SET @福建_35_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '36', '江西省', 1, 14, 1, '江西省', '江西', 'Jiangxi', 'J', NOW(), NOW(), 0);
SET @江西_36_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '37', '山东省', 1, 15, 1, '山东省', '山东', 'Shandong', 'S', NOW(), NOW(), 0);
SET @山东_37_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '41', '河南省', 1, 16, 1, '河南省', '河南', 'Henan', 'H', NOW(), NOW(), 0);
SET @河南_41_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '42', '湖北省', 1, 17, 1, '湖北省', '湖北', 'Hubei', 'H', NOW(), NOW(), 0);
SET @湖北_42_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '43', '湖南省', 1, 18, 1, '湖南省', '湖南', 'Hunan', 'H', NOW(), NOW(), 0);
SET @湖南_43_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '44', '广东省', 1, 19, 1, '广东省', '广东', 'Guangdong', 'G', NOW(), NOW(), 0);
SET @广东_44_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '45', '广西壮族自治区', 1, 20, 1, '广西壮族自治区', '广西壮族自治', '广西壮族自治', '', NOW(), NOW(), 0);
SET @广西壮族自治_45_id = LAST_INSERT_ID();

-- 已插入 20/31 个省级区划

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '46', '海南省', 1, 21, 1, '海南省', '海南', 'Hainan', 'H', NOW(), NOW(), 0);
SET @海南_46_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '50', '重庆市', 1, 22, 1, '重庆市', '重庆', 'Chongqing', 'C', NOW(), NOW(), 0);
SET @重庆_50_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '51', '四川省', 1, 23, 1, '四川省', '四川', 'Sichuan', 'S', NOW(), NOW(), 0);
SET @四川_51_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '52', '贵州省', 1, 24, 1, '贵州省', '贵州', 'Guizhou', 'G', NOW(), NOW(), 0);
SET @贵州_52_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '53', '云南省', 1, 25, 1, '云南省', '云南', 'Yunnan', 'Y', NOW(), NOW(), 0);
SET @云南_53_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '54', '西藏自治区', 1, 26, 1, '西藏自治区', '西藏自治', '西藏自治', '', NOW(), NOW(), 0);
SET @西藏自治_54_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '61', '陕西省', 1, 27, 1, '陕西省', '陕西', 'Shaanxi', 'S', NOW(), NOW(), 0);
SET @陕西_61_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '62', '甘肃省', 1, 28, 1, '甘肃省', '甘肃', 'Gansu', 'G', NOW(), NOW(), 0);
SET @甘肃_62_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '63', '青海省', 1, 29, 1, '青海省', '青海', 'Qinghai', 'Q', NOW(), NOW(), 0);
SET @青海_63_id = LAST_INSERT_ID();

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '64', '宁夏回族自治区', 1, 30, 1, '宁夏回族自治区', '宁夏回族自治', '宁夏回族自治', '', NOW(), NOW(), 0);
SET @宁夏回族自治_64_id = LAST_INSERT_ID();

-- 已插入 30/31 个省级区划

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '65', '新疆维吾尔自治区', 1, 31, 1, '新疆维吾尔自治区', '新疆维吾尔自治', '新疆维吾尔自治', '', NOW(), NOW(), 0);
SET @新疆维吾尔自治_65_id = LAST_INSERT_ID();

-- 市级数据（共 337 条）
-- 批次 1：插入 100 条市级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1301', '石家庄市', 2, 0, 1, '石家庄市', '石家庄', '石家庄', '', NOW(), NOW(), 0);
SET @石家庄_1301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1302', '唐山市', 2, 0, 1, '唐山市', '唐山', '唐山', '', NOW(), NOW(), 0);
SET @唐山_1302_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1303', '秦皇岛市', 2, 0, 1, '秦皇岛市', '秦皇岛', '秦皇岛', '', NOW(), NOW(), 0);
SET @秦皇岛_1303_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1304', '邯郸市', 2, 0, 1, '邯郸市', '邯郸', '邯郸', '', NOW(), NOW(), 0);
SET @邯郸_1304_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1305', '邢台市', 2, 0, 1, '邢台市', '邢台', '邢台', '', NOW(), NOW(), 0);
SET @邢台_1305_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1306', '保定市', 2, 0, 1, '保定市', '保定', '保定', '', NOW(), NOW(), 0);
SET @保定_1306_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1307', '张家口市', 2, 0, 1, '张家口市', '张家口', '张家口', '', NOW(), NOW(), 0);
SET @张家口_1307_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1308', '承德市', 2, 0, 1, '承德市', '承德', '承德', '', NOW(), NOW(), 0);
SET @承德_1308_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1309', '沧州市', 2, 0, 1, '沧州市', '沧州', '沧州', '', NOW(), NOW(), 0);
SET @沧州_1309_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1310', '廊坊市', 2, 0, 1, '廊坊市', '廊坊', '廊坊', '', NOW(), NOW(), 0);
SET @廊坊_1310_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河北_13_id, '1311', '衡水市', 2, 0, 1, '衡水市', '衡水', '衡水', '', NOW(), NOW(), 0);
SET @衡水_1311_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1401', '太原市', 2, 0, 1, '太原市', '太原', '太原', '', NOW(), NOW(), 0);
SET @太原_1401_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1402', '大同市', 2, 0, 1, '大同市', '大同', '大同', '', NOW(), NOW(), 0);
SET @大同_1402_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1403', '阳泉市', 2, 0, 1, '阳泉市', '阳泉', '阳泉', '', NOW(), NOW(), 0);
SET @阳泉_1403_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1404', '长治市', 2, 0, 1, '长治市', '长治', '长治', '', NOW(), NOW(), 0);
SET @长治_1404_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1405', '晋城市', 2, 0, 1, '晋城市', '晋城', '晋城', '', NOW(), NOW(), 0);
SET @晋城_1405_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1406', '朔州市', 2, 0, 1, '朔州市', '朔州', '朔州', '', NOW(), NOW(), 0);
SET @朔州_1406_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1407', '晋中市', 2, 0, 1, '晋中市', '晋中', '晋中', '', NOW(), NOW(), 0);
SET @晋中_1407_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1408', '运城市', 2, 0, 1, '运城市', '运城', '运城', '', NOW(), NOW(), 0);
SET @运城_1408_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1409', '忻州市', 2, 0, 1, '忻州市', '忻州', '忻州', '', NOW(), NOW(), 0);
SET @忻州_1409_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1410', '临汾市', 2, 0, 1, '临汾市', '临汾', '临汾', '', NOW(), NOW(), 0);
SET @临汾_1410_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山西_14_id, '1411', '吕梁市', 2, 0, 1, '吕梁市', '吕梁', '吕梁', '', NOW(), NOW(), 0);
SET @吕梁_1411_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1501', '呼和浩特市', 2, 0, 1, '呼和浩特市', '呼和浩特', '呼和浩特', '', NOW(), NOW(), 0);
SET @呼和浩特_1501_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1502', '包头市', 2, 0, 1, '包头市', '包头', '包头', '', NOW(), NOW(), 0);
SET @包头_1502_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1503', '乌海市', 2, 0, 1, '乌海市', '乌海', '乌海', '', NOW(), NOW(), 0);
SET @乌海_1503_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1504', '赤峰市', 2, 0, 1, '赤峰市', '赤峰', '赤峰', '', NOW(), NOW(), 0);
SET @赤峰_1504_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1505', '通辽市', 2, 0, 1, '通辽市', '通辽', '通辽', '', NOW(), NOW(), 0);
SET @通辽_1505_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1506', '鄂尔多斯市', 2, 0, 1, '鄂尔多斯市', '鄂尔多斯', '鄂尔多斯', '', NOW(), NOW(), 0);
SET @鄂尔多斯_1506_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1507', '呼伦贝尔市', 2, 0, 1, '呼伦贝尔市', '呼伦贝尔', '呼伦贝尔', '', NOW(), NOW(), 0);
SET @呼伦贝尔_1507_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1508', '巴彦淖尔市', 2, 0, 1, '巴彦淖尔市', '巴彦淖尔', '巴彦淖尔', '', NOW(), NOW(), 0);
SET @巴彦淖尔_1508_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1509', '乌兰察布市', 2, 0, 1, '乌兰察布市', '乌兰察布', '乌兰察布', '', NOW(), NOW(), 0);
SET @乌兰察布_1509_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1522', '兴安盟', 2, 0, 1, '兴安盟', '兴安盟', '兴安盟', '', NOW(), NOW(), 0);
SET @兴安盟_1522_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1525', '锡林郭勒盟', 2, 0, 1, '锡林郭勒盟', '锡林郭勒盟', '锡林郭勒盟', '', NOW(), NOW(), 0);
SET @锡林郭勒盟_1525_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内蒙古自治_15_id, '1529', '阿拉善盟', 2, 0, 1, '阿拉善盟', '阿拉善盟', '阿拉善盟', '', NOW(), NOW(), 0);
SET @阿拉善盟_1529_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2101', '沈阳市', 2, 0, 1, '沈阳市', '沈阳', '沈阳', '', NOW(), NOW(), 0);
SET @沈阳_2101_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2102', '大连市', 2, 0, 1, '大连市', '大连', '大连', '', NOW(), NOW(), 0);
SET @大连_2102_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2103', '鞍山市', 2, 0, 1, '鞍山市', '鞍山', '鞍山', '', NOW(), NOW(), 0);
SET @鞍山_2103_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2104', '抚顺市', 2, 0, 1, '抚顺市', '抚顺', '抚顺', '', NOW(), NOW(), 0);
SET @抚顺_2104_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2105', '本溪市', 2, 0, 1, '本溪市', '本溪', '本溪', '', NOW(), NOW(), 0);
SET @本溪_2105_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2106', '丹东市', 2, 0, 1, '丹东市', '丹东', '丹东', '', NOW(), NOW(), 0);
SET @丹东_2106_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2107', '锦州市', 2, 0, 1, '锦州市', '锦州', '锦州', '', NOW(), NOW(), 0);
SET @锦州_2107_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2108', '营口市', 2, 0, 1, '营口市', '营口', '营口', '', NOW(), NOW(), 0);
SET @营口_2108_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2109', '阜新市', 2, 0, 1, '阜新市', '阜新', '阜新', '', NOW(), NOW(), 0);
SET @阜新_2109_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2110', '辽阳市', 2, 0, 1, '辽阳市', '辽阳', '辽阳', '', NOW(), NOW(), 0);
SET @辽阳_2110_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2111', '盘锦市', 2, 0, 1, '盘锦市', '盘锦', '盘锦', '', NOW(), NOW(), 0);
SET @盘锦_2111_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2112', '铁岭市', 2, 0, 1, '铁岭市', '铁岭', '铁岭', '', NOW(), NOW(), 0);
SET @铁岭_2112_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2113', '朝阳市', 2, 0, 1, '朝阳市', '朝阳', '朝阳', '', NOW(), NOW(), 0);
SET @朝阳_2113_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽宁_21_id, '2114', '葫芦岛市', 2, 0, 1, '葫芦岛市', '葫芦岛', '葫芦岛', '', NOW(), NOW(), 0);
SET @葫芦岛_2114_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2201', '长春市', 2, 0, 1, '长春市', '长春', '长春', '', NOW(), NOW(), 0);
SET @长春_2201_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2202', '吉林市', 2, 0, 1, '吉林市', '吉林', 'Jilin', 'J', NOW(), NOW(), 0);
SET @吉林_2202_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2203', '四平市', 2, 0, 1, '四平市', '四平', '四平', '', NOW(), NOW(), 0);
SET @四平_2203_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2204', '辽源市', 2, 0, 1, '辽源市', '辽源', '辽源', '', NOW(), NOW(), 0);
SET @辽源_2204_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2205', '通化市', 2, 0, 1, '通化市', '通化', '通化', '', NOW(), NOW(), 0);
SET @通化_2205_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2206', '白山市', 2, 0, 1, '白山市', '白山', '白山', '', NOW(), NOW(), 0);
SET @白山_2206_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2207', '松原市', 2, 0, 1, '松原市', '松原', '松原', '', NOW(), NOW(), 0);
SET @松原_2207_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2208', '白城市', 2, 0, 1, '白城市', '白城', '白城', '', NOW(), NOW(), 0);
SET @白城_2208_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_22_id, '2224', '延边朝鲜族自治州', 2, 0, 1, '延边朝鲜族自治州', '延边朝鲜族', '延边朝鲜族', '', NOW(), NOW(), 0);
SET @延边朝鲜族_2224_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2301', '哈尔滨市', 2, 0, 1, '哈尔滨市', '哈尔滨', '哈尔滨', '', NOW(), NOW(), 0);
SET @哈尔滨_2301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2302', '齐齐哈尔市', 2, 0, 1, '齐齐哈尔市', '齐齐哈尔', '齐齐哈尔', '', NOW(), NOW(), 0);
SET @齐齐哈尔_2302_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2303', '鸡西市', 2, 0, 1, '鸡西市', '鸡西', '鸡西', '', NOW(), NOW(), 0);
SET @鸡西_2303_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2304', '鹤岗市', 2, 0, 1, '鹤岗市', '鹤岗', '鹤岗', '', NOW(), NOW(), 0);
SET @鹤岗_2304_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2305', '双鸭山市', 2, 0, 1, '双鸭山市', '双鸭山', '双鸭山', '', NOW(), NOW(), 0);
SET @双鸭山_2305_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2306', '大庆市', 2, 0, 1, '大庆市', '大庆', '大庆', '', NOW(), NOW(), 0);
SET @大庆_2306_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2307', '伊春市', 2, 0, 1, '伊春市', '伊春', '伊春', '', NOW(), NOW(), 0);
SET @伊春_2307_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2308', '佳木斯市', 2, 0, 1, '佳木斯市', '佳木斯', '佳木斯', '', NOW(), NOW(), 0);
SET @佳木斯_2308_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2309', '七台河市', 2, 0, 1, '七台河市', '七台河', '七台河', '', NOW(), NOW(), 0);
SET @七台河_2309_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2310', '牡丹江市', 2, 0, 1, '牡丹江市', '牡丹江', '牡丹江', '', NOW(), NOW(), 0);
SET @牡丹江_2310_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2311', '黑河市', 2, 0, 1, '黑河市', '黑河', '黑河', '', NOW(), NOW(), 0);
SET @黑河_2311_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2312', '绥化市', 2, 0, 1, '绥化市', '绥化', '绥化', '', NOW(), NOW(), 0);
SET @绥化_2312_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑龙江_23_id, '2327', '大兴安岭地区', 2, 0, 1, '大兴安岭地区', '大兴安岭地', '大兴安岭地', '', NOW(), NOW(), 0);
SET @大兴安岭地_2327_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3201', '南京市', 2, 0, 1, '南京市', '南京', '南京', '', NOW(), NOW(), 0);
SET @南京_3201_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3202', '无锡市', 2, 0, 1, '无锡市', '无锡', '无锡', '', NOW(), NOW(), 0);
SET @无锡_3202_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3203', '徐州市', 2, 0, 1, '徐州市', '徐州', '徐州', '', NOW(), NOW(), 0);
SET @徐州_3203_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3204', '常州市', 2, 0, 1, '常州市', '常州', '常州', '', NOW(), NOW(), 0);
SET @常州_3204_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3205', '苏州市', 2, 0, 1, '苏州市', '苏州', '苏州', '', NOW(), NOW(), 0);
SET @苏州_3205_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3206', '南通市', 2, 0, 1, '南通市', '南通', '南通', '', NOW(), NOW(), 0);
SET @南通_3206_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3207', '连云港市', 2, 0, 1, '连云港市', '连云港', '连云港', '', NOW(), NOW(), 0);
SET @连云港_3207_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3208', '淮安市', 2, 0, 1, '淮安市', '淮安', '淮安', '', NOW(), NOW(), 0);
SET @淮安_3208_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3209', '盐城市', 2, 0, 1, '盐城市', '盐城', '盐城', '', NOW(), NOW(), 0);
SET @盐城_3209_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3210', '扬州市', 2, 0, 1, '扬州市', '扬州', '扬州', '', NOW(), NOW(), 0);
SET @扬州_3210_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3211', '镇江市', 2, 0, 1, '镇江市', '镇江', '镇江', '', NOW(), NOW(), 0);
SET @镇江_3211_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3212', '泰州市', 2, 0, 1, '泰州市', '泰州', '泰州', '', NOW(), NOW(), 0);
SET @泰州_3212_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江苏_32_id, '3213', '宿迁市', 2, 0, 1, '宿迁市', '宿迁', '宿迁', '', NOW(), NOW(), 0);
SET @宿迁_3213_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3301', '杭州市', 2, 0, 1, '杭州市', '杭州', '杭州', '', NOW(), NOW(), 0);
SET @杭州_3301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3302', '宁波市', 2, 0, 1, '宁波市', '宁波', '宁波', '', NOW(), NOW(), 0);
SET @宁波_3302_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3303', '温州市', 2, 0, 1, '温州市', '温州', '温州', '', NOW(), NOW(), 0);
SET @温州_3303_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3304', '嘉兴市', 2, 0, 1, '嘉兴市', '嘉兴', '嘉兴', '', NOW(), NOW(), 0);
SET @嘉兴_3304_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3305', '湖州市', 2, 0, 1, '湖州市', '湖州', '湖州', '', NOW(), NOW(), 0);
SET @湖州_3305_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3306', '绍兴市', 2, 0, 1, '绍兴市', '绍兴', '绍兴', '', NOW(), NOW(), 0);
SET @绍兴_3306_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3307', '金华市', 2, 0, 1, '金华市', '金华', '金华', '', NOW(), NOW(), 0);
SET @金华_3307_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3308', '衢州市', 2, 0, 1, '衢州市', '衢州', '衢州', '', NOW(), NOW(), 0);
SET @衢州_3308_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3309', '舟山市', 2, 0, 1, '舟山市', '舟山', '舟山', '', NOW(), NOW(), 0);
SET @舟山_3309_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3310', '台州市', 2, 0, 1, '台州市', '台州', '台州', '', NOW(), NOW(), 0);
SET @台州_3310_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@浙江_33_id, '3311', '丽水市', 2, 0, 1, '丽水市', '丽水', '丽水', '', NOW(), NOW(), 0);
SET @丽水_3311_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3401', '合肥市', 2, 0, 1, '合肥市', '合肥', '合肥', '', NOW(), NOW(), 0);
SET @合肥_3401_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3402', '芜湖市', 2, 0, 1, '芜湖市', '芜湖', '芜湖', '', NOW(), NOW(), 0);
SET @芜湖_3402_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3403', '蚌埠市', 2, 0, 1, '蚌埠市', '蚌埠', '蚌埠', '', NOW(), NOW(), 0);
SET @蚌埠_3403_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3404', '淮南市', 2, 0, 1, '淮南市', '淮南', '淮南', '', NOW(), NOW(), 0);
SET @淮南_3404_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3405', '马鞍山市', 2, 0, 1, '马鞍山市', '马鞍山', '马鞍山', '', NOW(), NOW(), 0);
SET @马鞍山_3405_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3406', '淮北市', 2, 0, 1, '淮北市', '淮北', '淮北', '', NOW(), NOW(), 0);
SET @淮北_3406_id = LAST_INSERT_ID();
-- 已插入 100/337 条市级数据

-- 批次 2：插入 100 条市级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3407', '铜陵市', 2, 0, 1, '铜陵市', '铜陵', '铜陵', '', NOW(), NOW(), 0);
SET @铜陵_3407_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3408', '安庆市', 2, 0, 1, '安庆市', '安庆', '安庆', '', NOW(), NOW(), 0);
SET @安庆_3408_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3410', '黄山市', 2, 0, 1, '黄山市', '黄山', '黄山', '', NOW(), NOW(), 0);
SET @黄山_3410_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3411', '滁州市', 2, 0, 1, '滁州市', '滁州', '滁州', '', NOW(), NOW(), 0);
SET @滁州_3411_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3412', '阜阳市', 2, 0, 1, '阜阳市', '阜阳', '阜阳', '', NOW(), NOW(), 0);
SET @阜阳_3412_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3413', '宿州市', 2, 0, 1, '宿州市', '宿州', '宿州', '', NOW(), NOW(), 0);
SET @宿州_3413_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3415', '六安市', 2, 0, 1, '六安市', '六安', '六安', '', NOW(), NOW(), 0);
SET @六安_3415_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3416', '亳州市', 2, 0, 1, '亳州市', '亳州', '亳州', '', NOW(), NOW(), 0);
SET @亳州_3416_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3417', '池州市', 2, 0, 1, '池州市', '池州', '池州', '', NOW(), NOW(), 0);
SET @池州_3417_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安徽_34_id, '3418', '宣城市', 2, 0, 1, '宣城市', '宣城', '宣城', '', NOW(), NOW(), 0);
SET @宣城_3418_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3501', '福州市', 2, 0, 1, '福州市', '福州', '福州', '', NOW(), NOW(), 0);
SET @福州_3501_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3502', '厦门市', 2, 0, 1, '厦门市', '厦门', '厦门', '', NOW(), NOW(), 0);
SET @厦门_3502_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3503', '莆田市', 2, 0, 1, '莆田市', '莆田', '莆田', '', NOW(), NOW(), 0);
SET @莆田_3503_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3504', '三明市', 2, 0, 1, '三明市', '三明', '三明', '', NOW(), NOW(), 0);
SET @三明_3504_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3505', '泉州市', 2, 0, 1, '泉州市', '泉州', '泉州', '', NOW(), NOW(), 0);
SET @泉州_3505_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3506', '漳州市', 2, 0, 1, '漳州市', '漳州', '漳州', '', NOW(), NOW(), 0);
SET @漳州_3506_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3507', '南平市', 2, 0, 1, '南平市', '南平', '南平', '', NOW(), NOW(), 0);
SET @南平_3507_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3508', '龙岩市', 2, 0, 1, '龙岩市', '龙岩', '龙岩', '', NOW(), NOW(), 0);
SET @龙岩_3508_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福建_35_id, '3509', '宁德市', 2, 0, 1, '宁德市', '宁德', '宁德', '', NOW(), NOW(), 0);
SET @宁德_3509_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3601', '南昌市', 2, 0, 1, '南昌市', '南昌', '南昌', '', NOW(), NOW(), 0);
SET @南昌_3601_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3602', '景德镇市', 2, 0, 1, '景德镇市', '景德镇', '景德镇', '', NOW(), NOW(), 0);
SET @景德镇_3602_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3603', '萍乡市', 2, 0, 1, '萍乡市', '萍乡', '萍乡', '', NOW(), NOW(), 0);
SET @萍乡_3603_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3604', '九江市', 2, 0, 1, '九江市', '九江', '九江', '', NOW(), NOW(), 0);
SET @九江_3604_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3605', '新余市', 2, 0, 1, '新余市', '新余', '新余', '', NOW(), NOW(), 0);
SET @新余_3605_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3606', '鹰潭市', 2, 0, 1, '鹰潭市', '鹰潭', '鹰潭', '', NOW(), NOW(), 0);
SET @鹰潭_3606_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3607', '赣州市', 2, 0, 1, '赣州市', '赣州', '赣州', '', NOW(), NOW(), 0);
SET @赣州_3607_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3608', '吉安市', 2, 0, 1, '吉安市', '吉安', '吉安', '', NOW(), NOW(), 0);
SET @吉安_3608_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3609', '宜春市', 2, 0, 1, '宜春市', '宜春', '宜春', '', NOW(), NOW(), 0);
SET @宜春_3609_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3610', '抚州市', 2, 0, 1, '抚州市', '抚州', '抚州', '', NOW(), NOW(), 0);
SET @抚州_3610_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江西_36_id, '3611', '上饶市', 2, 0, 1, '上饶市', '上饶', '上饶', '', NOW(), NOW(), 0);
SET @上饶_3611_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3701', '济南市', 2, 0, 1, '济南市', '济南', '济南', '', NOW(), NOW(), 0);
SET @济南_3701_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3702', '青岛市', 2, 0, 1, '青岛市', '青岛', '青岛', '', NOW(), NOW(), 0);
SET @青岛_3702_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3703', '淄博市', 2, 0, 1, '淄博市', '淄博', '淄博', '', NOW(), NOW(), 0);
SET @淄博_3703_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3704', '枣庄市', 2, 0, 1, '枣庄市', '枣庄', '枣庄', '', NOW(), NOW(), 0);
SET @枣庄_3704_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3705', '东营市', 2, 0, 1, '东营市', '东营', '东营', '', NOW(), NOW(), 0);
SET @东营_3705_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3706', '烟台市', 2, 0, 1, '烟台市', '烟台', '烟台', '', NOW(), NOW(), 0);
SET @烟台_3706_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3707', '潍坊市', 2, 0, 1, '潍坊市', '潍坊', '潍坊', '', NOW(), NOW(), 0);
SET @潍坊_3707_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3708', '济宁市', 2, 0, 1, '济宁市', '济宁', '济宁', '', NOW(), NOW(), 0);
SET @济宁_3708_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3709', '泰安市', 2, 0, 1, '泰安市', '泰安', '泰安', '', NOW(), NOW(), 0);
SET @泰安_3709_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3710', '威海市', 2, 0, 1, '威海市', '威海', '威海', '', NOW(), NOW(), 0);
SET @威海_3710_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3711', '日照市', 2, 0, 1, '日照市', '日照', '日照', '', NOW(), NOW(), 0);
SET @日照_3711_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3713', '临沂市', 2, 0, 1, '临沂市', '临沂', '临沂', '', NOW(), NOW(), 0);
SET @临沂_3713_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3714', '德州市', 2, 0, 1, '德州市', '德州', '德州', '', NOW(), NOW(), 0);
SET @德州_3714_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3715', '聊城市', 2, 0, 1, '聊城市', '聊城', '聊城', '', NOW(), NOW(), 0);
SET @聊城_3715_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3716', '滨州市', 2, 0, 1, '滨州市', '滨州', '滨州', '', NOW(), NOW(), 0);
SET @滨州_3716_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山东_37_id, '3717', '菏泽市', 2, 0, 1, '菏泽市', '菏泽', '菏泽', '', NOW(), NOW(), 0);
SET @菏泽_3717_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4101', '郑州市', 2, 0, 1, '郑州市', '郑州', '郑州', '', NOW(), NOW(), 0);
SET @郑州_4101_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4102', '开封市', 2, 0, 1, '开封市', '开封', '开封', '', NOW(), NOW(), 0);
SET @开封_4102_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4103', '洛阳市', 2, 0, 1, '洛阳市', '洛阳', '洛阳', '', NOW(), NOW(), 0);
SET @洛阳_4103_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4104', '平顶山市', 2, 0, 1, '平顶山市', '平顶山', '平顶山', '', NOW(), NOW(), 0);
SET @平顶山_4104_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4105', '安阳市', 2, 0, 1, '安阳市', '安阳', '安阳', '', NOW(), NOW(), 0);
SET @安阳_4105_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4106', '鹤壁市', 2, 0, 1, '鹤壁市', '鹤壁', '鹤壁', '', NOW(), NOW(), 0);
SET @鹤壁_4106_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4107', '新乡市', 2, 0, 1, '新乡市', '新乡', '新乡', '', NOW(), NOW(), 0);
SET @新乡_4107_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4108', '焦作市', 2, 0, 1, '焦作市', '焦作', '焦作', '', NOW(), NOW(), 0);
SET @焦作_4108_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4109', '濮阳市', 2, 0, 1, '濮阳市', '濮阳', '濮阳', '', NOW(), NOW(), 0);
SET @濮阳_4109_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4110', '许昌市', 2, 0, 1, '许昌市', '许昌', '许昌', '', NOW(), NOW(), 0);
SET @许昌_4110_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4111', '漯河市', 2, 0, 1, '漯河市', '漯河', '漯河', '', NOW(), NOW(), 0);
SET @漯河_4111_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4112', '三门峡市', 2, 0, 1, '三门峡市', '三门峡', '三门峡', '', NOW(), NOW(), 0);
SET @三门峡_4112_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4113', '南阳市', 2, 0, 1, '南阳市', '南阳', '南阳', '', NOW(), NOW(), 0);
SET @南阳_4113_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4114', '商丘市', 2, 0, 1, '商丘市', '商丘', '商丘', '', NOW(), NOW(), 0);
SET @商丘_4114_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4115', '信阳市', 2, 0, 1, '信阳市', '信阳', '信阳', '', NOW(), NOW(), 0);
SET @信阳_4115_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4116', '周口市', 2, 0, 1, '周口市', '周口', '周口', '', NOW(), NOW(), 0);
SET @周口_4116_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4117', '驻马店市', 2, 0, 1, '驻马店市', '驻马店', '驻马店', '', NOW(), NOW(), 0);
SET @驻马店_4117_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河南_41_id, '4190', '省直辖县级行政区划', 2, 0, 1, '省直辖县级行政区划', '省直辖县级行政区划', '省直辖县级行政区划', '', NOW(), NOW(), 0);
SET @省直辖县级行政区划_4190_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4201', '武汉市', 2, 0, 1, '武汉市', '武汉', '武汉', '', NOW(), NOW(), 0);
SET @武汉_4201_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4202', '黄石市', 2, 0, 1, '黄石市', '黄石', '黄石', '', NOW(), NOW(), 0);
SET @黄石_4202_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4203', '十堰市', 2, 0, 1, '十堰市', '十堰', '十堰', '', NOW(), NOW(), 0);
SET @十堰_4203_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4205', '宜昌市', 2, 0, 1, '宜昌市', '宜昌', '宜昌', '', NOW(), NOW(), 0);
SET @宜昌_4205_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4206', '襄阳市', 2, 0, 1, '襄阳市', '襄阳', '襄阳', '', NOW(), NOW(), 0);
SET @襄阳_4206_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4207', '鄂州市', 2, 0, 1, '鄂州市', '鄂州', '鄂州', '', NOW(), NOW(), 0);
SET @鄂州_4207_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4208', '荆门市', 2, 0, 1, '荆门市', '荆门', '荆门', '', NOW(), NOW(), 0);
SET @荆门_4208_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4209', '孝感市', 2, 0, 1, '孝感市', '孝感', '孝感', '', NOW(), NOW(), 0);
SET @孝感_4209_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4210', '荆州市', 2, 0, 1, '荆州市', '荆州', '荆州', '', NOW(), NOW(), 0);
SET @荆州_4210_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4211', '黄冈市', 2, 0, 1, '黄冈市', '黄冈', '黄冈', '', NOW(), NOW(), 0);
SET @黄冈_4211_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4212', '咸宁市', 2, 0, 1, '咸宁市', '咸宁', '咸宁', '', NOW(), NOW(), 0);
SET @咸宁_4212_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4213', '随州市', 2, 0, 1, '随州市', '随州', '随州', '', NOW(), NOW(), 0);
SET @随州_4213_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4228', '恩施土家族苗族自治州', 2, 0, 1, '恩施土家族苗族自治州', '恩施土家族苗族', '恩施土家族苗族', '', NOW(), NOW(), 0);
SET @恩施土家族苗族_4228_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖北_42_id, '4290', '省直辖县级行政区划', 2, 0, 1, '省直辖县级行政区划', '省直辖县级行政区划', '省直辖县级行政区划', '', NOW(), NOW(), 0);
SET @省直辖县级行政区划_4290_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4301', '长沙市', 2, 0, 1, '长沙市', '长沙', '长沙', '', NOW(), NOW(), 0);
SET @长沙_4301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4302', '株洲市', 2, 0, 1, '株洲市', '株洲', '株洲', '', NOW(), NOW(), 0);
SET @株洲_4302_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4303', '湘潭市', 2, 0, 1, '湘潭市', '湘潭', '湘潭', '', NOW(), NOW(), 0);
SET @湘潭_4303_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4304', '衡阳市', 2, 0, 1, '衡阳市', '衡阳', '衡阳', '', NOW(), NOW(), 0);
SET @衡阳_4304_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4305', '邵阳市', 2, 0, 1, '邵阳市', '邵阳', '邵阳', '', NOW(), NOW(), 0);
SET @邵阳_4305_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4306', '岳阳市', 2, 0, 1, '岳阳市', '岳阳', '岳阳', '', NOW(), NOW(), 0);
SET @岳阳_4306_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4307', '常德市', 2, 0, 1, '常德市', '常德', '常德', '', NOW(), NOW(), 0);
SET @常德_4307_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4308', '张家界市', 2, 0, 1, '张家界市', '张家界', '张家界', '', NOW(), NOW(), 0);
SET @张家界_4308_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4309', '益阳市', 2, 0, 1, '益阳市', '益阳', '益阳', '', NOW(), NOW(), 0);
SET @益阳_4309_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4310', '郴州市', 2, 0, 1, '郴州市', '郴州', '郴州', '', NOW(), NOW(), 0);
SET @郴州_4310_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4311', '永州市', 2, 0, 1, '永州市', '永州', '永州', '', NOW(), NOW(), 0);
SET @永州_4311_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4312', '怀化市', 2, 0, 1, '怀化市', '怀化', '怀化', '', NOW(), NOW(), 0);
SET @怀化_4312_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4313', '娄底市', 2, 0, 1, '娄底市', '娄底', '娄底', '', NOW(), NOW(), 0);
SET @娄底_4313_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖南_43_id, '4331', '湘西土家族苗族自治州', 2, 0, 1, '湘西土家族苗族自治州', '湘西土家族苗族', '湘西土家族苗族', '', NOW(), NOW(), 0);
SET @湘西土家族苗族_4331_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4401', '广州市', 2, 0, 1, '广州市', '广州', '广州', '', NOW(), NOW(), 0);
SET @广州_4401_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4402', '韶关市', 2, 0, 1, '韶关市', '韶关', '韶关', '', NOW(), NOW(), 0);
SET @韶关_4402_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4403', '深圳市', 2, 0, 1, '深圳市', '深圳', '深圳', '', NOW(), NOW(), 0);
SET @深圳_4403_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4404', '珠海市', 2, 0, 1, '珠海市', '珠海', '珠海', '', NOW(), NOW(), 0);
SET @珠海_4404_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4405', '汕头市', 2, 0, 1, '汕头市', '汕头', '汕头', '', NOW(), NOW(), 0);
SET @汕头_4405_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4406', '佛山市', 2, 0, 1, '佛山市', '佛山', '佛山', '', NOW(), NOW(), 0);
SET @佛山_4406_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4407', '江门市', 2, 0, 1, '江门市', '江门', '江门', '', NOW(), NOW(), 0);
SET @江门_4407_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4408', '湛江市', 2, 0, 1, '湛江市', '湛江', '湛江', '', NOW(), NOW(), 0);
SET @湛江_4408_id = LAST_INSERT_ID();
-- 已插入 200/337 条市级数据

-- 批次 3：插入 100 条市级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4409', '茂名市', 2, 0, 1, '茂名市', '茂名', '茂名', '', NOW(), NOW(), 0);
SET @茂名_4409_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4412', '肇庆市', 2, 0, 1, '肇庆市', '肇庆', '肇庆', '', NOW(), NOW(), 0);
SET @肇庆_4412_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4413', '惠州市', 2, 0, 1, '惠州市', '惠州', '惠州', '', NOW(), NOW(), 0);
SET @惠州_4413_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4414', '梅州市', 2, 0, 1, '梅州市', '梅州', '梅州', '', NOW(), NOW(), 0);
SET @梅州_4414_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4415', '汕尾市', 2, 0, 1, '汕尾市', '汕尾', '汕尾', '', NOW(), NOW(), 0);
SET @汕尾_4415_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4416', '河源市', 2, 0, 1, '河源市', '河源', '河源', '', NOW(), NOW(), 0);
SET @河源_4416_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4417', '阳江市', 2, 0, 1, '阳江市', '阳江', '阳江', '', NOW(), NOW(), 0);
SET @阳江_4417_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4418', '清远市', 2, 0, 1, '清远市', '清远', '清远', '', NOW(), NOW(), 0);
SET @清远_4418_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4419', '东莞市', 2, 0, 1, '东莞市', '东莞', '东莞', '', NOW(), NOW(), 0);
SET @东莞_4419_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4420', '中山市', 2, 0, 1, '中山市', '中山', '中山', '', NOW(), NOW(), 0);
SET @中山_4420_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4451', '潮州市', 2, 0, 1, '潮州市', '潮州', '潮州', '', NOW(), NOW(), 0);
SET @潮州_4451_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4452', '揭阳市', 2, 0, 1, '揭阳市', '揭阳', '揭阳', '', NOW(), NOW(), 0);
SET @揭阳_4452_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广东_44_id, '4453', '云浮市', 2, 0, 1, '云浮市', '云浮', '云浮', '', NOW(), NOW(), 0);
SET @云浮_4453_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4501', '南宁市', 2, 0, 1, '南宁市', '南宁', '南宁', '', NOW(), NOW(), 0);
SET @南宁_4501_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4502', '柳州市', 2, 0, 1, '柳州市', '柳州', '柳州', '', NOW(), NOW(), 0);
SET @柳州_4502_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4503', '桂林市', 2, 0, 1, '桂林市', '桂林', '桂林', '', NOW(), NOW(), 0);
SET @桂林_4503_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4504', '梧州市', 2, 0, 1, '梧州市', '梧州', '梧州', '', NOW(), NOW(), 0);
SET @梧州_4504_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4505', '北海市', 2, 0, 1, '北海市', '北海', '北海', '', NOW(), NOW(), 0);
SET @北海_4505_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4506', '防城港市', 2, 0, 1, '防城港市', '防城港', '防城港', '', NOW(), NOW(), 0);
SET @防城港_4506_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4507', '钦州市', 2, 0, 1, '钦州市', '钦州', '钦州', '', NOW(), NOW(), 0);
SET @钦州_4507_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4508', '贵港市', 2, 0, 1, '贵港市', '贵港', '贵港', '', NOW(), NOW(), 0);
SET @贵港_4508_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4509', '玉林市', 2, 0, 1, '玉林市', '玉林', '玉林', '', NOW(), NOW(), 0);
SET @玉林_4509_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4510', '百色市', 2, 0, 1, '百色市', '百色', '百色', '', NOW(), NOW(), 0);
SET @百色_4510_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4511', '贺州市', 2, 0, 1, '贺州市', '贺州', '贺州', '', NOW(), NOW(), 0);
SET @贺州_4511_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4512', '河池市', 2, 0, 1, '河池市', '河池', '河池', '', NOW(), NOW(), 0);
SET @河池_4512_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4513', '来宾市', 2, 0, 1, '来宾市', '来宾', '来宾', '', NOW(), NOW(), 0);
SET @来宾_4513_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广西壮族自治_45_id, '4514', '崇左市', 2, 0, 1, '崇左市', '崇左', '崇左', '', NOW(), NOW(), 0);
SET @崇左_4514_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南_46_id, '4601', '海口市', 2, 0, 1, '海口市', '海口', '海口', '', NOW(), NOW(), 0);
SET @海口_4601_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南_46_id, '4602', '三亚市', 2, 0, 1, '三亚市', '三亚', '三亚', '', NOW(), NOW(), 0);
SET @三亚_4602_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南_46_id, '4603', '三沙市', 2, 0, 1, '三沙市', '三沙', '三沙', '', NOW(), NOW(), 0);
SET @三沙_4603_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南_46_id, '4604', '儋州市', 2, 0, 1, '儋州市', '儋州', '儋州', '', NOW(), NOW(), 0);
SET @儋州_4604_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南_46_id, '4690', '省直辖县级行政区划', 2, 0, 1, '省直辖县级行政区划', '省直辖县级行政区划', '省直辖县级行政区划', '', NOW(), NOW(), 0);
SET @省直辖县级行政区划_4690_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5101', '成都市', 2, 0, 1, '成都市', '成都', '成都', '', NOW(), NOW(), 0);
SET @成都_5101_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5103', '自贡市', 2, 0, 1, '自贡市', '自贡', '自贡', '', NOW(), NOW(), 0);
SET @自贡_5103_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5104', '攀枝花市', 2, 0, 1, '攀枝花市', '攀枝花', '攀枝花', '', NOW(), NOW(), 0);
SET @攀枝花_5104_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5105', '泸州市', 2, 0, 1, '泸州市', '泸州', '泸州', '', NOW(), NOW(), 0);
SET @泸州_5105_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5106', '德阳市', 2, 0, 1, '德阳市', '德阳', '德阳', '', NOW(), NOW(), 0);
SET @德阳_5106_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5107', '绵阳市', 2, 0, 1, '绵阳市', '绵阳', '绵阳', '', NOW(), NOW(), 0);
SET @绵阳_5107_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5108', '广元市', 2, 0, 1, '广元市', '广元', '广元', '', NOW(), NOW(), 0);
SET @广元_5108_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5109', '遂宁市', 2, 0, 1, '遂宁市', '遂宁', '遂宁', '', NOW(), NOW(), 0);
SET @遂宁_5109_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5110', '内江市', 2, 0, 1, '内江市', '内江', '内江', '', NOW(), NOW(), 0);
SET @内江_5110_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5111', '乐山市', 2, 0, 1, '乐山市', '乐山', '乐山', '', NOW(), NOW(), 0);
SET @乐山_5111_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5113', '南充市', 2, 0, 1, '南充市', '南充', '南充', '', NOW(), NOW(), 0);
SET @南充_5113_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5114', '眉山市', 2, 0, 1, '眉山市', '眉山', '眉山', '', NOW(), NOW(), 0);
SET @眉山_5114_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5115', '宜宾市', 2, 0, 1, '宜宾市', '宜宾', '宜宾', '', NOW(), NOW(), 0);
SET @宜宾_5115_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5116', '广安市', 2, 0, 1, '广安市', '广安', '广安', '', NOW(), NOW(), 0);
SET @广安_5116_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5117', '达州市', 2, 0, 1, '达州市', '达州', '达州', '', NOW(), NOW(), 0);
SET @达州_5117_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5118', '雅安市', 2, 0, 1, '雅安市', '雅安', '雅安', '', NOW(), NOW(), 0);
SET @雅安_5118_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5119', '巴中市', 2, 0, 1, '巴中市', '巴中', '巴中', '', NOW(), NOW(), 0);
SET @巴中_5119_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5120', '资阳市', 2, 0, 1, '资阳市', '资阳', '资阳', '', NOW(), NOW(), 0);
SET @资阳_5120_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5132', '阿坝藏族羌族自治州', 2, 0, 1, '阿坝藏族羌族自治州', '阿坝藏族羌族', '阿坝藏族羌族', '', NOW(), NOW(), 0);
SET @阿坝藏族羌族_5132_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5133', '甘孜藏族自治州', 2, 0, 1, '甘孜藏族自治州', '甘孜藏族', '甘孜藏族', '', NOW(), NOW(), 0);
SET @甘孜藏族_5133_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四川_51_id, '5134', '凉山彝族自治州', 2, 0, 1, '凉山彝族自治州', '凉山彝族', '凉山彝族', '', NOW(), NOW(), 0);
SET @凉山彝族_5134_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5201', '贵阳市', 2, 0, 1, '贵阳市', '贵阳', '贵阳', '', NOW(), NOW(), 0);
SET @贵阳_5201_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5202', '六盘水市', 2, 0, 1, '六盘水市', '六盘水', '六盘水', '', NOW(), NOW(), 0);
SET @六盘水_5202_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5203', '遵义市', 2, 0, 1, '遵义市', '遵义', '遵义', '', NOW(), NOW(), 0);
SET @遵义_5203_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5204', '安顺市', 2, 0, 1, '安顺市', '安顺', '安顺', '', NOW(), NOW(), 0);
SET @安顺_5204_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5205', '毕节市', 2, 0, 1, '毕节市', '毕节', '毕节', '', NOW(), NOW(), 0);
SET @毕节_5205_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5206', '铜仁市', 2, 0, 1, '铜仁市', '铜仁', '铜仁', '', NOW(), NOW(), 0);
SET @铜仁_5206_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5223', '黔西南布依族苗族自治州', 2, 0, 1, '黔西南布依族苗族自治州', '黔西南布依族苗族', '黔西南布依族苗族', '', NOW(), NOW(), 0);
SET @黔西南布依族苗族_5223_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5226', '黔东南苗族侗族自治州', 2, 0, 1, '黔东南苗族侗族自治州', '黔东南苗族侗族', '黔东南苗族侗族', '', NOW(), NOW(), 0);
SET @黔东南苗族侗族_5226_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵州_52_id, '5227', '黔南布依族苗族自治州', 2, 0, 1, '黔南布依族苗族自治州', '黔南布依族苗族', '黔南布依族苗族', '', NOW(), NOW(), 0);
SET @黔南布依族苗族_5227_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5301', '昆明市', 2, 0, 1, '昆明市', '昆明', '昆明', '', NOW(), NOW(), 0);
SET @昆明_5301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5303', '曲靖市', 2, 0, 1, '曲靖市', '曲靖', '曲靖', '', NOW(), NOW(), 0);
SET @曲靖_5303_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5304', '玉溪市', 2, 0, 1, '玉溪市', '玉溪', '玉溪', '', NOW(), NOW(), 0);
SET @玉溪_5304_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5305', '保山市', 2, 0, 1, '保山市', '保山', '保山', '', NOW(), NOW(), 0);
SET @保山_5305_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5306', '昭通市', 2, 0, 1, '昭通市', '昭通', '昭通', '', NOW(), NOW(), 0);
SET @昭通_5306_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5307', '丽江市', 2, 0, 1, '丽江市', '丽江', '丽江', '', NOW(), NOW(), 0);
SET @丽江_5307_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5308', '普洱市', 2, 0, 1, '普洱市', '普洱', '普洱', '', NOW(), NOW(), 0);
SET @普洱_5308_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5309', '临沧市', 2, 0, 1, '临沧市', '临沧', '临沧', '', NOW(), NOW(), 0);
SET @临沧_5309_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5323', '楚雄彝族自治州', 2, 0, 1, '楚雄彝族自治州', '楚雄彝族', '楚雄彝族', '', NOW(), NOW(), 0);
SET @楚雄彝族_5323_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5325', '红河哈尼族彝族自治州', 2, 0, 1, '红河哈尼族彝族自治州', '红河哈尼族彝族', '红河哈尼族彝族', '', NOW(), NOW(), 0);
SET @红河哈尼族彝族_5325_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5326', '文山壮族苗族自治州', 2, 0, 1, '文山壮族苗族自治州', '文山壮族苗族', '文山壮族苗族', '', NOW(), NOW(), 0);
SET @文山壮族苗族_5326_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5328', '西双版纳傣族自治州', 2, 0, 1, '西双版纳傣族自治州', '西双版纳傣族', '西双版纳傣族', '', NOW(), NOW(), 0);
SET @西双版纳傣族_5328_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5329', '大理白族自治州', 2, 0, 1, '大理白族自治州', '大理白族', '大理白族', '', NOW(), NOW(), 0);
SET @大理白族_5329_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5331', '德宏傣族景颇族自治州', 2, 0, 1, '德宏傣族景颇族自治州', '德宏傣族景颇族', '德宏傣族景颇族', '', NOW(), NOW(), 0);
SET @德宏傣族景颇族_5331_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5333', '怒江傈僳族自治州', 2, 0, 1, '怒江傈僳族自治州', '怒江傈僳族', '怒江傈僳族', '', NOW(), NOW(), 0);
SET @怒江傈僳族_5333_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云南_53_id, '5334', '迪庆藏族自治州', 2, 0, 1, '迪庆藏族自治州', '迪庆藏族', '迪庆藏族', '', NOW(), NOW(), 0);
SET @迪庆藏族_5334_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5401', '拉萨市', 2, 0, 1, '拉萨市', '拉萨', '拉萨', '', NOW(), NOW(), 0);
SET @拉萨_5401_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5402', '日喀则市', 2, 0, 1, '日喀则市', '日喀则', '日喀则', '', NOW(), NOW(), 0);
SET @日喀则_5402_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5403', '昌都市', 2, 0, 1, '昌都市', '昌都', '昌都', '', NOW(), NOW(), 0);
SET @昌都_5403_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5404', '林芝市', 2, 0, 1, '林芝市', '林芝', '林芝', '', NOW(), NOW(), 0);
SET @林芝_5404_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5405', '山南市', 2, 0, 1, '山南市', '山南', '山南', '', NOW(), NOW(), 0);
SET @山南_5405_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5406', '那曲市', 2, 0, 1, '那曲市', '那曲', '那曲', '', NOW(), NOW(), 0);
SET @那曲_5406_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西藏自治_54_id, '5425', '阿里地区', 2, 0, 1, '阿里地区', '阿里地', '阿里地', '', NOW(), NOW(), 0);
SET @阿里地_5425_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6101', '西安市', 2, 0, 1, '西安市', '西安', '西安', '', NOW(), NOW(), 0);
SET @西安_6101_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6102', '铜川市', 2, 0, 1, '铜川市', '铜川', '铜川', '', NOW(), NOW(), 0);
SET @铜川_6102_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6103', '宝鸡市', 2, 0, 1, '宝鸡市', '宝鸡', '宝鸡', '', NOW(), NOW(), 0);
SET @宝鸡_6103_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6104', '咸阳市', 2, 0, 1, '咸阳市', '咸阳', '咸阳', '', NOW(), NOW(), 0);
SET @咸阳_6104_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6105', '渭南市', 2, 0, 1, '渭南市', '渭南', '渭南', '', NOW(), NOW(), 0);
SET @渭南_6105_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6106', '延安市', 2, 0, 1, '延安市', '延安', '延安', '', NOW(), NOW(), 0);
SET @延安_6106_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6107', '汉中市', 2, 0, 1, '汉中市', '汉中', '汉中', '', NOW(), NOW(), 0);
SET @汉中_6107_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6108', '榆林市', 2, 0, 1, '榆林市', '榆林', '榆林', '', NOW(), NOW(), 0);
SET @榆林_6108_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6109', '安康市', 2, 0, 1, '安康市', '安康', '安康', '', NOW(), NOW(), 0);
SET @安康_6109_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陕西_61_id, '6110', '商洛市', 2, 0, 1, '商洛市', '商洛', '商洛', '', NOW(), NOW(), 0);
SET @商洛_6110_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6201', '兰州市', 2, 0, 1, '兰州市', '兰州', '兰州', '', NOW(), NOW(), 0);
SET @兰州_6201_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6202', '嘉峪关市', 2, 0, 1, '嘉峪关市', '嘉峪关', '嘉峪关', '', NOW(), NOW(), 0);
SET @嘉峪关_6202_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6203', '金昌市', 2, 0, 1, '金昌市', '金昌', '金昌', '', NOW(), NOW(), 0);
SET @金昌_6203_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6204', '白银市', 2, 0, 1, '白银市', '白银', '白银', '', NOW(), NOW(), 0);
SET @白银_6204_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6205', '天水市', 2, 0, 1, '天水市', '天水', '天水', '', NOW(), NOW(), 0);
SET @天水_6205_id = LAST_INSERT_ID();
-- 已插入 300/337 条市级数据

-- 批次 4：插入 37 条市级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6206', '武威市', 2, 0, 1, '武威市', '武威', '武威', '', NOW(), NOW(), 0);
SET @武威_6206_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6207', '张掖市', 2, 0, 1, '张掖市', '张掖', '张掖', '', NOW(), NOW(), 0);
SET @张掖_6207_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6208', '平凉市', 2, 0, 1, '平凉市', '平凉', '平凉', '', NOW(), NOW(), 0);
SET @平凉_6208_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6209', '酒泉市', 2, 0, 1, '酒泉市', '酒泉', '酒泉', '', NOW(), NOW(), 0);
SET @酒泉_6209_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6210', '庆阳市', 2, 0, 1, '庆阳市', '庆阳', '庆阳', '', NOW(), NOW(), 0);
SET @庆阳_6210_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6211', '定西市', 2, 0, 1, '定西市', '定西', '定西', '', NOW(), NOW(), 0);
SET @定西_6211_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6212', '陇南市', 2, 0, 1, '陇南市', '陇南', '陇南', '', NOW(), NOW(), 0);
SET @陇南_6212_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6229', '临夏回族自治州', 2, 0, 1, '临夏回族自治州', '临夏回族', '临夏回族', '', NOW(), NOW(), 0);
SET @临夏回族_6229_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘肃_62_id, '6230', '甘南藏族自治州', 2, 0, 1, '甘南藏族自治州', '甘南藏族', '甘南藏族', '', NOW(), NOW(), 0);
SET @甘南藏族_6230_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6301', '西宁市', 2, 0, 1, '西宁市', '西宁', '西宁', '', NOW(), NOW(), 0);
SET @西宁_6301_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6302', '海东市', 2, 0, 1, '海东市', '海东', '海东', '', NOW(), NOW(), 0);
SET @海东_6302_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6322', '海北藏族自治州', 2, 0, 1, '海北藏族自治州', '海北藏族', '海北藏族', '', NOW(), NOW(), 0);
SET @海北藏族_6322_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6323', '黄南藏族自治州', 2, 0, 1, '黄南藏族自治州', '黄南藏族', '黄南藏族', '', NOW(), NOW(), 0);
SET @黄南藏族_6323_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6325', '海南藏族自治州', 2, 0, 1, '海南藏族自治州', '海南藏族', '海南藏族', '', NOW(), NOW(), 0);
SET @海南藏族_6325_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6326', '果洛藏族自治州', 2, 0, 1, '果洛藏族自治州', '果洛藏族', '果洛藏族', '', NOW(), NOW(), 0);
SET @果洛藏族_6326_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6327', '玉树藏族自治州', 2, 0, 1, '玉树藏族自治州', '玉树藏族', '玉树藏族', '', NOW(), NOW(), 0);
SET @玉树藏族_6327_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青海_63_id, '6328', '海西蒙古族藏族自治州', 2, 0, 1, '海西蒙古族藏族自治州', '海西蒙古族藏族', '海西蒙古族藏族', '', NOW(), NOW(), 0);
SET @海西蒙古族藏族_6328_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁夏回族自治_64_id, '6401', '银川市', 2, 0, 1, '银川市', '银川', '银川', '', NOW(), NOW(), 0);
SET @银川_6401_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁夏回族自治_64_id, '6402', '石嘴山市', 2, 0, 1, '石嘴山市', '石嘴山', '石嘴山', '', NOW(), NOW(), 0);
SET @石嘴山_6402_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁夏回族自治_64_id, '6403', '吴忠市', 2, 0, 1, '吴忠市', '吴忠', '吴忠', '', NOW(), NOW(), 0);
SET @吴忠_6403_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁夏回族自治_64_id, '6404', '固原市', 2, 0, 1, '固原市', '固原', '固原', '', NOW(), NOW(), 0);
SET @固原_6404_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁夏回族自治_64_id, '6405', '中卫市', 2, 0, 1, '中卫市', '中卫', '中卫', '', NOW(), NOW(), 0);
SET @中卫_6405_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6501', '乌鲁木齐市', 2, 0, 1, '乌鲁木齐市', '乌鲁木齐', '乌鲁木齐', '', NOW(), NOW(), 0);
SET @乌鲁木齐_6501_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6502', '克拉玛依市', 2, 0, 1, '克拉玛依市', '克拉玛依', '克拉玛依', '', NOW(), NOW(), 0);
SET @克拉玛依_6502_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6504', '吐鲁番市', 2, 0, 1, '吐鲁番市', '吐鲁番', '吐鲁番', '', NOW(), NOW(), 0);
SET @吐鲁番_6504_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6505', '哈密市', 2, 0, 1, '哈密市', '哈密', '哈密', '', NOW(), NOW(), 0);
SET @哈密_6505_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6523', '昌吉回族自治州', 2, 0, 1, '昌吉回族自治州', '昌吉回族', '昌吉回族', '', NOW(), NOW(), 0);
SET @昌吉回族_6523_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6527', '博尔塔拉蒙古自治州', 2, 0, 1, '博尔塔拉蒙古自治州', '博尔塔拉蒙古', '博尔塔拉蒙古', '', NOW(), NOW(), 0);
SET @博尔塔拉蒙古_6527_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6528', '巴音郭楞蒙古自治州', 2, 0, 1, '巴音郭楞蒙古自治州', '巴音郭楞蒙古', '巴音郭楞蒙古', '', NOW(), NOW(), 0);
SET @巴音郭楞蒙古_6528_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6529', '阿克苏地区', 2, 0, 1, '阿克苏地区', '阿克苏地', '阿克苏地', '', NOW(), NOW(), 0);
SET @阿克苏地_6529_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6530', '克孜勒苏柯尔克孜自治州', 2, 0, 1, '克孜勒苏柯尔克孜自治州', '克孜勒苏柯尔克孜', '克孜勒苏柯尔克孜', '', NOW(), NOW(), 0);
SET @克孜勒苏柯尔克孜_6530_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6531', '喀什地区', 2, 0, 1, '喀什地区', '喀什地', '喀什地', '', NOW(), NOW(), 0);
SET @喀什地_6531_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6532', '和田地区', 2, 0, 1, '和田地区', '和田地', '和田地', '', NOW(), NOW(), 0);
SET @和田地_6532_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6540', '伊犁哈萨克自治州', 2, 0, 1, '伊犁哈萨克自治州', '伊犁哈萨克', '伊犁哈萨克', '', NOW(), NOW(), 0);
SET @伊犁哈萨克_6540_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6542', '塔城地区', 2, 0, 1, '塔城地区', '塔城地', '塔城地', '', NOW(), NOW(), 0);
SET @塔城地_6542_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6543', '阿勒泰地区', 2, 0, 1, '阿勒泰地区', '阿勒泰地', '阿勒泰地', '', NOW(), NOW(), 0);
SET @阿勒泰地_6543_id = LAST_INSERT_ID();
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新疆维吾尔自治_65_id, '6590', '自治区直辖县级行政区划', 2, 0, 1, '自治区直辖县级行政区划', '自治区直辖县级行政区划', '自治区直辖县级行政区划', '', NOW(), NOW(), 0);
SET @自治区直辖县级行政区划_6590_id = LAST_INSERT_ID();
-- 已插入 337/337 条市级数据

-- 区级数据（共 3056 条）
-- 批次 1：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110101', '东城区', 3, 0, 1, '东城区', '东城', '东城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110102', '西城区', 3, 0, 1, '西城区', '西城', '西城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110105', '朝阳区', 3, 0, 1, '朝阳区', '朝阳', '朝阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110106', '丰台区', 3, 0, 1, '丰台区', '丰台', '丰台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110107', '石景山区', 3, 0, 1, '石景山区', '石景山', '石景山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110108', '海淀区', 3, 0, 1, '海淀区', '海淀', '海淀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110109', '门头沟区', 3, 0, 1, '门头沟区', '门头沟', '门头沟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110111', '房山区', 3, 0, 1, '房山区', '房山', '房山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110112', '通州区', 3, 0, 1, '通州区', '通州', '通州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110113', '顺义区', 3, 0, 1, '顺义区', '顺义', '顺义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110114', '昌平区', 3, 0, 1, '昌平区', '昌平', '昌平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110115', '大兴区', 3, 0, 1, '大兴区', '大兴', '大兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110116', '怀柔区', 3, 0, 1, '怀柔区', '怀柔', '怀柔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110117', '平谷区', 3, 0, 1, '平谷区', '平谷', '平谷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110118', '密云区', 3, 0, 1, '密云区', '密云', '密云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北京_11_id, '110119', '延庆区', 3, 0, 1, '延庆区', '延庆', '延庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120101', '和平区', 3, 0, 1, '和平区', '和平', '和平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120102', '河东区', 3, 0, 1, '河东区', '河东', '河东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120103', '河西区', 3, 0, 1, '河西区', '河西', '河西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120104', '南开区', 3, 0, 1, '南开区', '南开', '南开', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120105', '河北区', 3, 0, 1, '河北区', '河北', 'Hebei', 'H', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120106', '红桥区', 3, 0, 1, '红桥区', '红桥', '红桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120110', '东丽区', 3, 0, 1, '东丽区', '东丽', '东丽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120111', '西青区', 3, 0, 1, '西青区', '西青', '西青', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120112', '津南区', 3, 0, 1, '津南区', '津南', '津南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120113', '北辰区', 3, 0, 1, '北辰区', '北辰', '北辰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120114', '武清区', 3, 0, 1, '武清区', '武清', '武清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120115', '宝坻区', 3, 0, 1, '宝坻区', '宝坻', '宝坻', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120116', '滨海新区', 3, 0, 1, '滨海新区', '滨海新', '滨海新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120117', '宁河区', 3, 0, 1, '宁河区', '宁河', '宁河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120118', '静海区', 3, 0, 1, '静海区', '静海', '静海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天津_12_id, '120119', '蓟州区', 3, 0, 1, '蓟州区', '蓟州', '蓟州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130102', '长安区', 3, 0, 1, '长安区', '长安', '长安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130104', '桥西区', 3, 0, 1, '桥西区', '桥西', '桥西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130105', '新华区', 3, 0, 1, '新华区', '新华', '新华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130107', '井陉矿区', 3, 0, 1, '井陉矿区', '井陉矿', '井陉矿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130108', '裕华区', 3, 0, 1, '裕华区', '裕华', '裕华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130109', '藁城区', 3, 0, 1, '藁城区', '藁城', '藁城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130110', '鹿泉区', 3, 0, 1, '鹿泉区', '鹿泉', '鹿泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130111', '栾城区', 3, 0, 1, '栾城区', '栾城', '栾城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130121', '井陉县', 3, 0, 1, '井陉县', '井陉', '井陉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130123', '正定县', 3, 0, 1, '正定县', '正定', '正定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130125', '行唐县', 3, 0, 1, '行唐县', '行唐', '行唐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130126', '灵寿县', 3, 0, 1, '灵寿县', '灵寿', '灵寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130127', '高邑县', 3, 0, 1, '高邑县', '高邑', '高邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130128', '深泽县', 3, 0, 1, '深泽县', '深泽', '深泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130129', '赞皇县', 3, 0, 1, '赞皇县', '赞皇', '赞皇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130130', '无极县', 3, 0, 1, '无极县', '无极', '无极', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130131', '平山县', 3, 0, 1, '平山县', '平山', '平山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130132', '元氏县', 3, 0, 1, '元氏县', '元氏', '元氏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130133', '赵县', 3, 0, 1, '赵县', '赵', '赵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130171', '石家庄高新技术产业开发区', 3, 0, 1, '石家庄高新技术产业开发区', '石家庄高新技术产业开发', '石家庄高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130172', '石家庄循环化工园区', 3, 0, 1, '石家庄循环化工园区', '石家庄循环化工园', '石家庄循环化工园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130181', '辛集市', 3, 0, 1, '辛集市', '辛集', '辛集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130183', '晋州市', 3, 0, 1, '晋州市', '晋州', '晋州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石家庄_1301_id, '130184', '新乐市', 3, 0, 1, '新乐市', '新乐', '新乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130202', '路南区', 3, 0, 1, '路南区', '路南', '路南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130203', '路北区', 3, 0, 1, '路北区', '路北', '路北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130204', '古冶区', 3, 0, 1, '古冶区', '古冶', '古冶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130205', '开平区', 3, 0, 1, '开平区', '开平', '开平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130207', '丰南区', 3, 0, 1, '丰南区', '丰南', '丰南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130208', '丰润区', 3, 0, 1, '丰润区', '丰润', '丰润', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130209', '曹妃甸区', 3, 0, 1, '曹妃甸区', '曹妃甸', '曹妃甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130224', '滦南县', 3, 0, 1, '滦南县', '滦南', '滦南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130225', '乐亭县', 3, 0, 1, '乐亭县', '乐亭', '乐亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130227', '迁西县', 3, 0, 1, '迁西县', '迁西', '迁西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130229', '玉田县', 3, 0, 1, '玉田县', '玉田', '玉田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130271', '河北唐山芦台经济开发区', 3, 0, 1, '河北唐山芦台经济开发区', '河北唐山芦台经济开发', '河北唐山芦台经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130272', '唐山市汉沽管理区', 3, 0, 1, '唐山市汉沽管理区', '唐山市汉沽管理', '唐山市汉沽管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130273', '唐山高新技术产业开发区', 3, 0, 1, '唐山高新技术产业开发区', '唐山高新技术产业开发', '唐山高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130274', '河北唐山海港经济开发区', 3, 0, 1, '河北唐山海港经济开发区', '河北唐山海港经济开发', '河北唐山海港经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130281', '遵化市', 3, 0, 1, '遵化市', '遵化', '遵化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130283', '迁安市', 3, 0, 1, '迁安市', '迁安', '迁安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@唐山_1302_id, '130284', '滦州市', 3, 0, 1, '滦州市', '滦州', '滦州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130302', '海港区', 3, 0, 1, '海港区', '海港', '海港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130303', '山海关区', 3, 0, 1, '山海关区', '山海关', '山海关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130304', '北戴河区', 3, 0, 1, '北戴河区', '北戴河', '北戴河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130306', '抚宁区', 3, 0, 1, '抚宁区', '抚宁', '抚宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130321', '青龙满族自治县', 3, 0, 1, '青龙满族自治县', '青龙满族自治', '青龙满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130322', '昌黎县', 3, 0, 1, '昌黎县', '昌黎', '昌黎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130324', '卢龙县', 3, 0, 1, '卢龙县', '卢龙', '卢龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130371', '秦皇岛市经济技术开发区', 3, 0, 1, '秦皇岛市经济技术开发区', '秦皇岛市经济技术开发', '秦皇岛市经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@秦皇岛_1303_id, '130372', '北戴河新区', 3, 0, 1, '北戴河新区', '北戴河新', '北戴河新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130402', '邯山区', 3, 0, 1, '邯山区', '邯山', '邯山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130403', '丛台区', 3, 0, 1, '丛台区', '丛台', '丛台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130404', '复兴区', 3, 0, 1, '复兴区', '复兴', '复兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130406', '峰峰矿区', 3, 0, 1, '峰峰矿区', '峰峰矿', '峰峰矿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130407', '肥乡区', 3, 0, 1, '肥乡区', '肥乡', '肥乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130408', '永年区', 3, 0, 1, '永年区', '永年', '永年', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130423', '临漳县', 3, 0, 1, '临漳县', '临漳', '临漳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130424', '成安县', 3, 0, 1, '成安县', '成安', '成安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130425', '大名县', 3, 0, 1, '大名县', '大名', '大名', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130426', '涉县', 3, 0, 1, '涉县', '涉', '涉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130427', '磁县', 3, 0, 1, '磁县', '磁', '磁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130430', '邱县', 3, 0, 1, '邱县', '邱', '邱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130431', '鸡泽县', 3, 0, 1, '鸡泽县', '鸡泽', '鸡泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130432', '广平县', 3, 0, 1, '广平县', '广平', '广平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130433', '馆陶县', 3, 0, 1, '馆陶县', '馆陶', '馆陶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130434', '魏县', 3, 0, 1, '魏县', '魏', '魏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130435', '曲周县', 3, 0, 1, '曲周县', '曲周', '曲周', '', NOW(), NOW(), 0);
-- 已插入 100/3056 条区级数据

-- 批次 2：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130471', '邯郸经济技术开发区', 3, 0, 1, '邯郸经济技术开发区', '邯郸经济技术开发', '邯郸经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130473', '邯郸冀南新区', 3, 0, 1, '邯郸冀南新区', '邯郸冀南新', '邯郸冀南新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邯郸_1304_id, '130481', '武安市', 3, 0, 1, '武安市', '武安', '武安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130502', '襄都区', 3, 0, 1, '襄都区', '襄都', '襄都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130503', '信都区', 3, 0, 1, '信都区', '信都', '信都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130505', '任泽区', 3, 0, 1, '任泽区', '任泽', '任泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130506', '南和区', 3, 0, 1, '南和区', '南和', '南和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130522', '临城县', 3, 0, 1, '临城县', '临城', '临城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130523', '内丘县', 3, 0, 1, '内丘县', '内丘', '内丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130524', '柏乡县', 3, 0, 1, '柏乡县', '柏乡', '柏乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130525', '隆尧县', 3, 0, 1, '隆尧县', '隆尧', '隆尧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130528', '宁晋县', 3, 0, 1, '宁晋县', '宁晋', '宁晋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130529', '巨鹿县', 3, 0, 1, '巨鹿县', '巨鹿', '巨鹿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130530', '新河县', 3, 0, 1, '新河县', '新河', '新河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130531', '广宗县', 3, 0, 1, '广宗县', '广宗', '广宗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130532', '平乡县', 3, 0, 1, '平乡县', '平乡', '平乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130533', '威县', 3, 0, 1, '威县', '威', '威', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130534', '清河县', 3, 0, 1, '清河县', '清河', '清河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130535', '临西县', 3, 0, 1, '临西县', '临西', '临西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130571', '河北邢台经济开发区', 3, 0, 1, '河北邢台经济开发区', '河北邢台经济开发', '河北邢台经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130581', '南宫市', 3, 0, 1, '南宫市', '南宫', '南宫', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邢台_1305_id, '130582', '沙河市', 3, 0, 1, '沙河市', '沙河', '沙河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130602', '竞秀区', 3, 0, 1, '竞秀区', '竞秀', '竞秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130606', '莲池区', 3, 0, 1, '莲池区', '莲池', '莲池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130607', '满城区', 3, 0, 1, '满城区', '满城', '满城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130608', '清苑区', 3, 0, 1, '清苑区', '清苑', '清苑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130609', '徐水区', 3, 0, 1, '徐水区', '徐水', '徐水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130623', '涞水县', 3, 0, 1, '涞水县', '涞水', '涞水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130624', '阜平县', 3, 0, 1, '阜平县', '阜平', '阜平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130626', '定兴县', 3, 0, 1, '定兴县', '定兴', '定兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130627', '唐县', 3, 0, 1, '唐县', '唐', '唐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130628', '高阳县', 3, 0, 1, '高阳县', '高阳', '高阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130629', '容城县', 3, 0, 1, '容城县', '容城', '容城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130630', '涞源县', 3, 0, 1, '涞源县', '涞源', '涞源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130631', '望都县', 3, 0, 1, '望都县', '望都', '望都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130632', '安新县', 3, 0, 1, '安新县', '安新', '安新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130633', '易县', 3, 0, 1, '易县', '易', '易', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130634', '曲阳县', 3, 0, 1, '曲阳县', '曲阳', '曲阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130635', '蠡县', 3, 0, 1, '蠡县', '蠡', '蠡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130636', '顺平县', 3, 0, 1, '顺平县', '顺平', '顺平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130637', '博野县', 3, 0, 1, '博野县', '博野', '博野', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130638', '雄县', 3, 0, 1, '雄县', '雄', '雄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130671', '保定高新技术产业开发区', 3, 0, 1, '保定高新技术产业开发区', '保定高新技术产业开发', '保定高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130672', '保定白沟新城', 3, 0, 1, '保定白沟新城', '保定白沟新城', '保定白沟新城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130681', '涿州市', 3, 0, 1, '涿州市', '涿州', '涿州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130682', '定州市', 3, 0, 1, '定州市', '定州', '定州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130683', '安国市', 3, 0, 1, '安国市', '安国', '安国', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保定_1306_id, '130684', '高碑店市', 3, 0, 1, '高碑店市', '高碑店', '高碑店', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130702', '桥东区', 3, 0, 1, '桥东区', '桥东', '桥东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130703', '桥西区', 3, 0, 1, '桥西区', '桥西', '桥西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130705', '宣化区', 3, 0, 1, '宣化区', '宣化', '宣化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130706', '下花园区', 3, 0, 1, '下花园区', '下花园', '下花园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130708', '万全区', 3, 0, 1, '万全区', '万全', '万全', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130709', '崇礼区', 3, 0, 1, '崇礼区', '崇礼', '崇礼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130722', '张北县', 3, 0, 1, '张北县', '张北', '张北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130723', '康保县', 3, 0, 1, '康保县', '康保', '康保', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130724', '沽源县', 3, 0, 1, '沽源县', '沽源', '沽源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130725', '尚义县', 3, 0, 1, '尚义县', '尚义', '尚义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130726', '蔚县', 3, 0, 1, '蔚县', '蔚', '蔚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130727', '阳原县', 3, 0, 1, '阳原县', '阳原', '阳原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130728', '怀安县', 3, 0, 1, '怀安县', '怀安', '怀安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130730', '怀来县', 3, 0, 1, '怀来县', '怀来', '怀来', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130731', '涿鹿县', 3, 0, 1, '涿鹿县', '涿鹿', '涿鹿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130732', '赤城县', 3, 0, 1, '赤城县', '赤城', '赤城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130771', '张家口经济开发区', 3, 0, 1, '张家口经济开发区', '张家口经济开发', '张家口经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130772', '张家口市察北管理区', 3, 0, 1, '张家口市察北管理区', '张家口市察北管理', '张家口市察北管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家口_1307_id, '130773', '张家口市塞北管理区', 3, 0, 1, '张家口市塞北管理区', '张家口市塞北管理', '张家口市塞北管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130802', '双桥区', 3, 0, 1, '双桥区', '双桥', '双桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130803', '双滦区', 3, 0, 1, '双滦区', '双滦', '双滦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130804', '鹰手营子矿区', 3, 0, 1, '鹰手营子矿区', '鹰手营子矿', '鹰手营子矿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130821', '承德县', 3, 0, 1, '承德县', '承德', '承德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130822', '兴隆县', 3, 0, 1, '兴隆县', '兴隆', '兴隆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130824', '滦平县', 3, 0, 1, '滦平县', '滦平', '滦平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130825', '隆化县', 3, 0, 1, '隆化县', '隆化', '隆化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130826', '丰宁满族自治县', 3, 0, 1, '丰宁满族自治县', '丰宁满族自治', '丰宁满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130827', '宽城满族自治县', 3, 0, 1, '宽城满族自治县', '宽城满族自治', '宽城满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130828', '围场满族蒙古族自治县', 3, 0, 1, '围场满族蒙古族自治县', '围场满族蒙古族自治', '围场满族蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130871', '承德高新技术产业开发区', 3, 0, 1, '承德高新技术产业开发区', '承德高新技术产业开发', '承德高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@承德_1308_id, '130881', '平泉市', 3, 0, 1, '平泉市', '平泉', '平泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130902', '新华区', 3, 0, 1, '新华区', '新华', '新华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130903', '运河区', 3, 0, 1, '运河区', '运河', '运河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130921', '沧县', 3, 0, 1, '沧县', '沧', '沧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130922', '青县', 3, 0, 1, '青县', '青', '青', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130923', '东光县', 3, 0, 1, '东光县', '东光', '东光', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130924', '海兴县', 3, 0, 1, '海兴县', '海兴', '海兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130925', '盐山县', 3, 0, 1, '盐山县', '盐山', '盐山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130926', '肃宁县', 3, 0, 1, '肃宁县', '肃宁', '肃宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130927', '南皮县', 3, 0, 1, '南皮县', '南皮', '南皮', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130928', '吴桥县', 3, 0, 1, '吴桥县', '吴桥', '吴桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130929', '献县', 3, 0, 1, '献县', '献', '献', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130930', '孟村回族自治县', 3, 0, 1, '孟村回族自治县', '孟村回族自治', '孟村回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130971', '河北沧州经济开发区', 3, 0, 1, '河北沧州经济开发区', '河北沧州经济开发', '河北沧州经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130972', '沧州高新技术产业开发区', 3, 0, 1, '沧州高新技术产业开发区', '沧州高新技术产业开发', '沧州高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130973', '沧州渤海新区', 3, 0, 1, '沧州渤海新区', '沧州渤海新', '沧州渤海新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130981', '泊头市', 3, 0, 1, '泊头市', '泊头', '泊头', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130982', '任丘市', 3, 0, 1, '任丘市', '任丘', '任丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130983', '黄骅市', 3, 0, 1, '黄骅市', '黄骅', '黄骅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沧州_1309_id, '130984', '河间市', 3, 0, 1, '河间市', '河间', '河间', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131002', '安次区', 3, 0, 1, '安次区', '安次', '安次', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131003', '广阳区', 3, 0, 1, '广阳区', '广阳', '广阳', '', NOW(), NOW(), 0);
-- 已插入 200/3056 条区级数据

-- 批次 3：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131022', '固安县', 3, 0, 1, '固安县', '固安', '固安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131023', '永清县', 3, 0, 1, '永清县', '永清', '永清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131024', '香河县', 3, 0, 1, '香河县', '香河', '香河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131025', '大城县', 3, 0, 1, '大城县', '大城', '大城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131026', '文安县', 3, 0, 1, '文安县', '文安', '文安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131028', '大厂回族自治县', 3, 0, 1, '大厂回族自治县', '大厂回族自治', '大厂回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131071', '廊坊经济技术开发区', 3, 0, 1, '廊坊经济技术开发区', '廊坊经济技术开发', '廊坊经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131081', '霸州市', 3, 0, 1, '霸州市', '霸州', '霸州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@廊坊_1310_id, '131082', '三河市', 3, 0, 1, '三河市', '三河', '三河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131102', '桃城区', 3, 0, 1, '桃城区', '桃城', '桃城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131103', '冀州区', 3, 0, 1, '冀州区', '冀州', '冀州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131121', '枣强县', 3, 0, 1, '枣强县', '枣强', '枣强', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131122', '武邑县', 3, 0, 1, '武邑县', '武邑', '武邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131123', '武强县', 3, 0, 1, '武强县', '武强', '武强', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131124', '饶阳县', 3, 0, 1, '饶阳县', '饶阳', '饶阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131125', '安平县', 3, 0, 1, '安平县', '安平', '安平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131126', '故城县', 3, 0, 1, '故城县', '故城', '故城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131127', '景县', 3, 0, 1, '景县', '景', '景', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131128', '阜城县', 3, 0, 1, '阜城县', '阜城', '阜城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131171', '河北衡水高新技术产业开发区', 3, 0, 1, '河北衡水高新技术产业开发区', '河北衡水高新技术产业开发', '河北衡水高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131172', '衡水滨湖新区', 3, 0, 1, '衡水滨湖新区', '衡水滨湖新', '衡水滨湖新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡水_1311_id, '131182', '深州市', 3, 0, 1, '深州市', '深州', '深州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140105', '小店区', 3, 0, 1, '小店区', '小店', '小店', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140106', '迎泽区', 3, 0, 1, '迎泽区', '迎泽', '迎泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140107', '杏花岭区', 3, 0, 1, '杏花岭区', '杏花岭', '杏花岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140108', '尖草坪区', 3, 0, 1, '尖草坪区', '尖草坪', '尖草坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140109', '万柏林区', 3, 0, 1, '万柏林区', '万柏林', '万柏林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140110', '晋源区', 3, 0, 1, '晋源区', '晋源', '晋源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140121', '清徐县', 3, 0, 1, '清徐县', '清徐', '清徐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140122', '阳曲县', 3, 0, 1, '阳曲县', '阳曲', '阳曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140123', '娄烦县', 3, 0, 1, '娄烦县', '娄烦', '娄烦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140171', '山西转型综合改革示范区', 3, 0, 1, '山西转型综合改革示范区', '山西转型综合改革示范', '山西转型综合改革示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@太原_1401_id, '140181', '古交市', 3, 0, 1, '古交市', '古交', '古交', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140212', '新荣区', 3, 0, 1, '新荣区', '新荣', '新荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140213', '平城区', 3, 0, 1, '平城区', '平城', '平城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140214', '云冈区', 3, 0, 1, '云冈区', '云冈', '云冈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140215', '云州区', 3, 0, 1, '云州区', '云州', '云州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140221', '阳高县', 3, 0, 1, '阳高县', '阳高', '阳高', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140222', '天镇县', 3, 0, 1, '天镇县', '天镇', '天镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140223', '广灵县', 3, 0, 1, '广灵县', '广灵', '广灵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140224', '灵丘县', 3, 0, 1, '灵丘县', '灵丘', '灵丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140225', '浑源县', 3, 0, 1, '浑源县', '浑源', '浑源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140226', '左云县', 3, 0, 1, '左云县', '左云', '左云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大同_1402_id, '140271', '山西大同经济开发区', 3, 0, 1, '山西大同经济开发区', '山西大同经济开发', '山西大同经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳泉_1403_id, '140302', '城区', 3, 0, 1, '城区', '城', '城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳泉_1403_id, '140303', '矿区', 3, 0, 1, '矿区', '矿', '矿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳泉_1403_id, '140311', '郊区', 3, 0, 1, '郊区', '郊', '郊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳泉_1403_id, '140321', '平定县', 3, 0, 1, '平定县', '平定', '平定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳泉_1403_id, '140322', '盂县', 3, 0, 1, '盂县', '盂', '盂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140403', '潞州区', 3, 0, 1, '潞州区', '潞州', '潞州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140404', '上党区', 3, 0, 1, '上党区', '上党', '上党', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140405', '屯留区', 3, 0, 1, '屯留区', '屯留', '屯留', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140406', '潞城区', 3, 0, 1, '潞城区', '潞城', '潞城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140423', '襄垣县', 3, 0, 1, '襄垣县', '襄垣', '襄垣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140425', '平顺县', 3, 0, 1, '平顺县', '平顺', '平顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140426', '黎城县', 3, 0, 1, '黎城县', '黎城', '黎城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140427', '壶关县', 3, 0, 1, '壶关县', '壶关', '壶关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140428', '长子县', 3, 0, 1, '长子县', '长子', '长子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140429', '武乡县', 3, 0, 1, '武乡县', '武乡', '武乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140430', '沁县', 3, 0, 1, '沁县', '沁', '沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长治_1404_id, '140431', '沁源县', 3, 0, 1, '沁源县', '沁源', '沁源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140502', '城区', 3, 0, 1, '城区', '城', '城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140521', '沁水县', 3, 0, 1, '沁水县', '沁水', '沁水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140522', '阳城县', 3, 0, 1, '阳城县', '阳城', '阳城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140524', '陵川县', 3, 0, 1, '陵川县', '陵川', '陵川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140525', '泽州县', 3, 0, 1, '泽州县', '泽州', '泽州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋城_1405_id, '140581', '高平市', 3, 0, 1, '高平市', '高平', '高平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140602', '朔城区', 3, 0, 1, '朔城区', '朔城', '朔城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140603', '平鲁区', 3, 0, 1, '平鲁区', '平鲁', '平鲁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140621', '山阴县', 3, 0, 1, '山阴县', '山阴', '山阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140622', '应县', 3, 0, 1, '应县', '应', '应', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140623', '右玉县', 3, 0, 1, '右玉县', '右玉', '右玉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140671', '山西朔州经济开发区', 3, 0, 1, '山西朔州经济开发区', '山西朔州经济开发', '山西朔州经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朔州_1406_id, '140681', '怀仁市', 3, 0, 1, '怀仁市', '怀仁', '怀仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140702', '榆次区', 3, 0, 1, '榆次区', '榆次', '榆次', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140703', '太谷区', 3, 0, 1, '太谷区', '太谷', '太谷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140721', '榆社县', 3, 0, 1, '榆社县', '榆社', '榆社', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140722', '左权县', 3, 0, 1, '左权县', '左权', '左权', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140723', '和顺县', 3, 0, 1, '和顺县', '和顺', '和顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140724', '昔阳县', 3, 0, 1, '昔阳县', '昔阳', '昔阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140725', '寿阳县', 3, 0, 1, '寿阳县', '寿阳', '寿阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140727', '祁县', 3, 0, 1, '祁县', '祁', '祁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140728', '平遥县', 3, 0, 1, '平遥县', '平遥', '平遥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140729', '灵石县', 3, 0, 1, '灵石县', '灵石', '灵石', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@晋中_1407_id, '140781', '介休市', 3, 0, 1, '介休市', '介休', '介休', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140802', '盐湖区', 3, 0, 1, '盐湖区', '盐湖', '盐湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140821', '临猗县', 3, 0, 1, '临猗县', '临猗', '临猗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140822', '万荣县', 3, 0, 1, '万荣县', '万荣', '万荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140823', '闻喜县', 3, 0, 1, '闻喜县', '闻喜', '闻喜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140824', '稷山县', 3, 0, 1, '稷山县', '稷山', '稷山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140825', '新绛县', 3, 0, 1, '新绛县', '新绛', '新绛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140826', '绛县', 3, 0, 1, '绛县', '绛', '绛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140827', '垣曲县', 3, 0, 1, '垣曲县', '垣曲', '垣曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140828', '夏县', 3, 0, 1, '夏县', '夏', '夏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140829', '平陆县', 3, 0, 1, '平陆县', '平陆', '平陆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140830', '芮城县', 3, 0, 1, '芮城县', '芮城', '芮城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140881', '永济市', 3, 0, 1, '永济市', '永济', '永济', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@运城_1408_id, '140882', '河津市', 3, 0, 1, '河津市', '河津', '河津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140902', '忻府区', 3, 0, 1, '忻府区', '忻府', '忻府', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140921', '定襄县', 3, 0, 1, '定襄县', '定襄', '定襄', '', NOW(), NOW(), 0);
-- 已插入 300/3056 条区级数据

-- 批次 4：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140922', '五台县', 3, 0, 1, '五台县', '五台', '五台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140923', '代县', 3, 0, 1, '代县', '代', '代', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140924', '繁峙县', 3, 0, 1, '繁峙县', '繁峙', '繁峙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140925', '宁武县', 3, 0, 1, '宁武县', '宁武', '宁武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140926', '静乐县', 3, 0, 1, '静乐县', '静乐', '静乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140927', '神池县', 3, 0, 1, '神池县', '神池', '神池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140928', '五寨县', 3, 0, 1, '五寨县', '五寨', '五寨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140929', '岢岚县', 3, 0, 1, '岢岚县', '岢岚', '岢岚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140930', '河曲县', 3, 0, 1, '河曲县', '河曲', '河曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140931', '保德县', 3, 0, 1, '保德县', '保德', '保德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140932', '偏关县', 3, 0, 1, '偏关县', '偏关', '偏关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140971', '五台山风景名胜区', 3, 0, 1, '五台山风景名胜区', '五台山风景名胜', '五台山风景名胜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@忻州_1409_id, '140981', '原平市', 3, 0, 1, '原平市', '原平', '原平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141002', '尧都区', 3, 0, 1, '尧都区', '尧都', '尧都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141021', '曲沃县', 3, 0, 1, '曲沃县', '曲沃', '曲沃', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141022', '翼城县', 3, 0, 1, '翼城县', '翼城', '翼城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141023', '襄汾县', 3, 0, 1, '襄汾县', '襄汾', '襄汾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141024', '洪洞县', 3, 0, 1, '洪洞县', '洪洞', '洪洞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141025', '古县', 3, 0, 1, '古县', '古', '古', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141026', '安泽县', 3, 0, 1, '安泽县', '安泽', '安泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141027', '浮山县', 3, 0, 1, '浮山县', '浮山', '浮山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141028', '吉县', 3, 0, 1, '吉县', '吉', '吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141029', '乡宁县', 3, 0, 1, '乡宁县', '乡宁', '乡宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141030', '大宁县', 3, 0, 1, '大宁县', '大宁', '大宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141031', '隰县', 3, 0, 1, '隰县', '隰', '隰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141032', '永和县', 3, 0, 1, '永和县', '永和', '永和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141033', '蒲县', 3, 0, 1, '蒲县', '蒲', '蒲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141034', '汾西县', 3, 0, 1, '汾西县', '汾西', '汾西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141081', '侯马市', 3, 0, 1, '侯马市', '侯马', '侯马', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临汾_1410_id, '141082', '霍州市', 3, 0, 1, '霍州市', '霍州', '霍州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141102', '离石区', 3, 0, 1, '离石区', '离石', '离石', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141121', '文水县', 3, 0, 1, '文水县', '文水', '文水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141122', '交城县', 3, 0, 1, '交城县', '交城', '交城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141123', '兴县', 3, 0, 1, '兴县', '兴', '兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141124', '临县', 3, 0, 1, '临县', '临', '临', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141125', '柳林县', 3, 0, 1, '柳林县', '柳林', '柳林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141126', '石楼县', 3, 0, 1, '石楼县', '石楼', '石楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141127', '岚县', 3, 0, 1, '岚县', '岚', '岚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141128', '方山县', 3, 0, 1, '方山县', '方山', '方山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141129', '中阳县', 3, 0, 1, '中阳县', '中阳', '中阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141130', '交口县', 3, 0, 1, '交口县', '交口', '交口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141181', '孝义市', 3, 0, 1, '孝义市', '孝义', '孝义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吕梁_1411_id, '141182', '汾阳市', 3, 0, 1, '汾阳市', '汾阳', '汾阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150102', '新城区', 3, 0, 1, '新城区', '新城', '新城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150103', '回民区', 3, 0, 1, '回民区', '回民', '回民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150104', '玉泉区', 3, 0, 1, '玉泉区', '玉泉', '玉泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150105', '赛罕区', 3, 0, 1, '赛罕区', '赛罕', '赛罕', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150121', '土默特左旗', 3, 0, 1, '土默特左旗', '土默特左', '土默特左', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150122', '托克托县', 3, 0, 1, '托克托县', '托克托', '托克托', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150123', '和林格尔县', 3, 0, 1, '和林格尔县', '和林格尔', '和林格尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150124', '清水河县', 3, 0, 1, '清水河县', '清水河', '清水河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150125', '武川县', 3, 0, 1, '武川县', '武川', '武川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼和浩特_1501_id, '150172', '呼和浩特经济技术开发区', 3, 0, 1, '呼和浩特经济技术开发区', '呼和浩特经济技术开发', '呼和浩特经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150202', '东河区', 3, 0, 1, '东河区', '东河', '东河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150203', '昆都仑区', 3, 0, 1, '昆都仑区', '昆都仑', '昆都仑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150204', '青山区', 3, 0, 1, '青山区', '青山', '青山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150205', '石拐区', 3, 0, 1, '石拐区', '石拐', '石拐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150206', '白云鄂博矿区', 3, 0, 1, '白云鄂博矿区', '白云鄂博矿', '白云鄂博矿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150207', '九原区', 3, 0, 1, '九原区', '九原', '九原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150221', '土默特右旗', 3, 0, 1, '土默特右旗', '土默特右', '土默特右', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150222', '固阳县', 3, 0, 1, '固阳县', '固阳', '固阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150223', '达尔罕茂明安联合旗', 3, 0, 1, '达尔罕茂明安联合旗', '达尔罕茂明安联合', '达尔罕茂明安联合', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@包头_1502_id, '150271', '包头稀土高新技术产业开发区', 3, 0, 1, '包头稀土高新技术产业开发区', '包头稀土高新技术产业开发', '包头稀土高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌海_1503_id, '150302', '海勃湾区', 3, 0, 1, '海勃湾区', '海勃湾', '海勃湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌海_1503_id, '150303', '海南区', 3, 0, 1, '海南区', '海南', 'Hainan', 'H', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌海_1503_id, '150304', '乌达区', 3, 0, 1, '乌达区', '乌达', '乌达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150402', '红山区', 3, 0, 1, '红山区', '红山', '红山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150403', '元宝山区', 3, 0, 1, '元宝山区', '元宝山', '元宝山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150404', '松山区', 3, 0, 1, '松山区', '松山', '松山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150421', '阿鲁科尔沁旗', 3, 0, 1, '阿鲁科尔沁旗', '阿鲁科尔沁', '阿鲁科尔沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150422', '巴林左旗', 3, 0, 1, '巴林左旗', '巴林左', '巴林左', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150423', '巴林右旗', 3, 0, 1, '巴林右旗', '巴林右', '巴林右', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150424', '林西县', 3, 0, 1, '林西县', '林西', '林西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150425', '克什克腾旗', 3, 0, 1, '克什克腾旗', '克什克腾', '克什克腾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150426', '翁牛特旗', 3, 0, 1, '翁牛特旗', '翁牛特', '翁牛特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150428', '喀喇沁旗', 3, 0, 1, '喀喇沁旗', '喀喇沁', '喀喇沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150429', '宁城县', 3, 0, 1, '宁城县', '宁城', '宁城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赤峰_1504_id, '150430', '敖汉旗', 3, 0, 1, '敖汉旗', '敖汉', '敖汉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150502', '科尔沁区', 3, 0, 1, '科尔沁区', '科尔沁', '科尔沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150521', '科尔沁左翼中旗', 3, 0, 1, '科尔沁左翼中旗', '科尔沁左翼中', '科尔沁左翼中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150522', '科尔沁左翼后旗', 3, 0, 1, '科尔沁左翼后旗', '科尔沁左翼后', '科尔沁左翼后', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150523', '开鲁县', 3, 0, 1, '开鲁县', '开鲁', '开鲁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150524', '库伦旗', 3, 0, 1, '库伦旗', '库伦', '库伦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150525', '奈曼旗', 3, 0, 1, '奈曼旗', '奈曼', '奈曼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150526', '扎鲁特旗', 3, 0, 1, '扎鲁特旗', '扎鲁特', '扎鲁特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150571', '通辽经济技术开发区', 3, 0, 1, '通辽经济技术开发区', '通辽经济技术开发', '通辽经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通辽_1505_id, '150581', '霍林郭勒市', 3, 0, 1, '霍林郭勒市', '霍林郭勒', '霍林郭勒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150602', '东胜区', 3, 0, 1, '东胜区', '东胜', '东胜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150603', '康巴什区', 3, 0, 1, '康巴什区', '康巴什', '康巴什', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150621', '达拉特旗', 3, 0, 1, '达拉特旗', '达拉特', '达拉特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150622', '准格尔旗', 3, 0, 1, '准格尔旗', '准格尔', '准格尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150623', '鄂托克前旗', 3, 0, 1, '鄂托克前旗', '鄂托克前', '鄂托克前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150624', '鄂托克旗', 3, 0, 1, '鄂托克旗', '鄂托克', '鄂托克', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150625', '杭锦旗', 3, 0, 1, '杭锦旗', '杭锦', '杭锦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150626', '乌审旗', 3, 0, 1, '乌审旗', '乌审', '乌审', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂尔多斯_1506_id, '150627', '伊金霍洛旗', 3, 0, 1, '伊金霍洛旗', '伊金霍洛', '伊金霍洛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150702', '海拉尔区', 3, 0, 1, '海拉尔区', '海拉尔', '海拉尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150703', '扎赉诺尔区', 3, 0, 1, '扎赉诺尔区', '扎赉诺尔', '扎赉诺尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150721', '阿荣旗', 3, 0, 1, '阿荣旗', '阿荣', '阿荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150722', '莫力达瓦达斡尔族自治旗', 3, 0, 1, '莫力达瓦达斡尔族自治旗', '莫力达瓦达斡尔族自治', '莫力达瓦达斡尔族自治', '', NOW(), NOW(), 0);
-- 已插入 400/3056 条区级数据

-- 批次 5：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150723', '鄂伦春自治旗', 3, 0, 1, '鄂伦春自治旗', '鄂伦春自治', '鄂伦春自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150724', '鄂温克族自治旗', 3, 0, 1, '鄂温克族自治旗', '鄂温克族自治', '鄂温克族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150725', '陈巴尔虎旗', 3, 0, 1, '陈巴尔虎旗', '陈巴尔虎', '陈巴尔虎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150726', '新巴尔虎左旗', 3, 0, 1, '新巴尔虎左旗', '新巴尔虎左', '新巴尔虎左', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150727', '新巴尔虎右旗', 3, 0, 1, '新巴尔虎右旗', '新巴尔虎右', '新巴尔虎右', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150781', '满洲里市', 3, 0, 1, '满洲里市', '满洲里', '满洲里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150782', '牙克石市', 3, 0, 1, '牙克石市', '牙克石', '牙克石', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150783', '扎兰屯市', 3, 0, 1, '扎兰屯市', '扎兰屯', '扎兰屯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150784', '额尔古纳市', 3, 0, 1, '额尔古纳市', '额尔古纳', '额尔古纳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@呼伦贝尔_1507_id, '150785', '根河市', 3, 0, 1, '根河市', '根河', '根河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150802', '临河区', 3, 0, 1, '临河区', '临河', '临河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150821', '五原县', 3, 0, 1, '五原县', '五原', '五原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150822', '磴口县', 3, 0, 1, '磴口县', '磴口', '磴口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150823', '乌拉特前旗', 3, 0, 1, '乌拉特前旗', '乌拉特前', '乌拉特前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150824', '乌拉特中旗', 3, 0, 1, '乌拉特中旗', '乌拉特中', '乌拉特中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150825', '乌拉特后旗', 3, 0, 1, '乌拉特后旗', '乌拉特后', '乌拉特后', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴彦淖尔_1508_id, '150826', '杭锦后旗', 3, 0, 1, '杭锦后旗', '杭锦后', '杭锦后', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150902', '集宁区', 3, 0, 1, '集宁区', '集宁', '集宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150921', '卓资县', 3, 0, 1, '卓资县', '卓资', '卓资', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150922', '化德县', 3, 0, 1, '化德县', '化德', '化德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150923', '商都县', 3, 0, 1, '商都县', '商都', '商都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150924', '兴和县', 3, 0, 1, '兴和县', '兴和', '兴和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150925', '凉城县', 3, 0, 1, '凉城县', '凉城', '凉城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150926', '察哈尔右翼前旗', 3, 0, 1, '察哈尔右翼前旗', '察哈尔右翼前', '察哈尔右翼前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150927', '察哈尔右翼中旗', 3, 0, 1, '察哈尔右翼中旗', '察哈尔右翼中', '察哈尔右翼中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150928', '察哈尔右翼后旗', 3, 0, 1, '察哈尔右翼后旗', '察哈尔右翼后', '察哈尔右翼后', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150929', '四子王旗', 3, 0, 1, '四子王旗', '四子王', '四子王', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌兰察布_1509_id, '150981', '丰镇市', 3, 0, 1, '丰镇市', '丰镇', '丰镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152201', '乌兰浩特市', 3, 0, 1, '乌兰浩特市', '乌兰浩特', '乌兰浩特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152202', '阿尔山市', 3, 0, 1, '阿尔山市', '阿尔山', '阿尔山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152221', '科尔沁右翼前旗', 3, 0, 1, '科尔沁右翼前旗', '科尔沁右翼前', '科尔沁右翼前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152222', '科尔沁右翼中旗', 3, 0, 1, '科尔沁右翼中旗', '科尔沁右翼中', '科尔沁右翼中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152223', '扎赉特旗', 3, 0, 1, '扎赉特旗', '扎赉特', '扎赉特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兴安盟_1522_id, '152224', '突泉县', 3, 0, 1, '突泉县', '突泉', '突泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152501', '二连浩特市', 3, 0, 1, '二连浩特市', '二连浩特', '二连浩特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152502', '锡林浩特市', 3, 0, 1, '锡林浩特市', '锡林浩特', '锡林浩特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152522', '阿巴嘎旗', 3, 0, 1, '阿巴嘎旗', '阿巴嘎', '阿巴嘎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152523', '苏尼特左旗', 3, 0, 1, '苏尼特左旗', '苏尼特左', '苏尼特左', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152524', '苏尼特右旗', 3, 0, 1, '苏尼特右旗', '苏尼特右', '苏尼特右', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152525', '东乌珠穆沁旗', 3, 0, 1, '东乌珠穆沁旗', '东乌珠穆沁', '东乌珠穆沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152526', '西乌珠穆沁旗', 3, 0, 1, '西乌珠穆沁旗', '西乌珠穆沁', '西乌珠穆沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152527', '太仆寺旗', 3, 0, 1, '太仆寺旗', '太仆寺', '太仆寺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152528', '镶黄旗', 3, 0, 1, '镶黄旗', '镶黄', '镶黄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152529', '正镶白旗', 3, 0, 1, '正镶白旗', '正镶白', '正镶白', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152530', '正蓝旗', 3, 0, 1, '正蓝旗', '正蓝', '正蓝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152531', '多伦县', 3, 0, 1, '多伦县', '多伦', '多伦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锡林郭勒盟_1525_id, '152571', '乌拉盖管理区管委会', 3, 0, 1, '乌拉盖管理区管委会', '乌拉盖管理区管委会', '乌拉盖管理区管委会', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿拉善盟_1529_id, '152921', '阿拉善左旗', 3, 0, 1, '阿拉善左旗', '阿拉善左', '阿拉善左', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿拉善盟_1529_id, '152922', '阿拉善右旗', 3, 0, 1, '阿拉善右旗', '阿拉善右', '阿拉善右', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿拉善盟_1529_id, '152923', '额济纳旗', 3, 0, 1, '额济纳旗', '额济纳', '额济纳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿拉善盟_1529_id, '152971', '内蒙古阿拉善高新技术产业开发区', 3, 0, 1, '内蒙古阿拉善高新技术产业开发区', '内蒙古阿拉善高新技术产业开发', '内蒙古阿拉善高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210102', '和平区', 3, 0, 1, '和平区', '和平', '和平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210103', '沈河区', 3, 0, 1, '沈河区', '沈河', '沈河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210104', '大东区', 3, 0, 1, '大东区', '大东', '大东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210105', '皇姑区', 3, 0, 1, '皇姑区', '皇姑', '皇姑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210106', '铁西区', 3, 0, 1, '铁西区', '铁西', '铁西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210111', '苏家屯区', 3, 0, 1, '苏家屯区', '苏家屯', '苏家屯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210112', '浑南区', 3, 0, 1, '浑南区', '浑南', '浑南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210113', '沈北新区', 3, 0, 1, '沈北新区', '沈北新', '沈北新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210114', '于洪区', 3, 0, 1, '于洪区', '于洪', '于洪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210115', '辽中区', 3, 0, 1, '辽中区', '辽中', '辽中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210123', '康平县', 3, 0, 1, '康平县', '康平', '康平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210124', '法库县', 3, 0, 1, '法库县', '法库', '法库', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@沈阳_2101_id, '210181', '新民市', 3, 0, 1, '新民市', '新民', '新民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210202', '中山区', 3, 0, 1, '中山区', '中山', '中山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210203', '西岗区', 3, 0, 1, '西岗区', '西岗', '西岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210204', '沙河口区', 3, 0, 1, '沙河口区', '沙河口', '沙河口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210211', '甘井子区', 3, 0, 1, '甘井子区', '甘井子', '甘井子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210212', '旅顺口区', 3, 0, 1, '旅顺口区', '旅顺口', '旅顺口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210213', '金州区', 3, 0, 1, '金州区', '金州', '金州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210214', '普兰店区', 3, 0, 1, '普兰店区', '普兰店', '普兰店', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210224', '长海县', 3, 0, 1, '长海县', '长海', '长海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210281', '瓦房店市', 3, 0, 1, '瓦房店市', '瓦房店', '瓦房店', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大连_2102_id, '210283', '庄河市', 3, 0, 1, '庄河市', '庄河', '庄河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210302', '铁东区', 3, 0, 1, '铁东区', '铁东', '铁东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210303', '铁西区', 3, 0, 1, '铁西区', '铁西', '铁西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210304', '立山区', 3, 0, 1, '立山区', '立山', '立山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210311', '千山区', 3, 0, 1, '千山区', '千山', '千山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210321', '台安县', 3, 0, 1, '台安县', '台安', '台安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210323', '岫岩满族自治县', 3, 0, 1, '岫岩满族自治县', '岫岩满族自治', '岫岩满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鞍山_2103_id, '210381', '海城市', 3, 0, 1, '海城市', '海城', '海城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210402', '新抚区', 3, 0, 1, '新抚区', '新抚', '新抚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210403', '东洲区', 3, 0, 1, '东洲区', '东洲', '东洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210404', '望花区', 3, 0, 1, '望花区', '望花', '望花', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210411', '顺城区', 3, 0, 1, '顺城区', '顺城', '顺城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210421', '抚顺县', 3, 0, 1, '抚顺县', '抚顺', '抚顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210422', '新宾满族自治县', 3, 0, 1, '新宾满族自治县', '新宾满族自治', '新宾满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚顺_2104_id, '210423', '清原满族自治县', 3, 0, 1, '清原满族自治县', '清原满族自治', '清原满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210502', '平山区', 3, 0, 1, '平山区', '平山', '平山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210503', '溪湖区', 3, 0, 1, '溪湖区', '溪湖', '溪湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210504', '明山区', 3, 0, 1, '明山区', '明山', '明山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210505', '南芬区', 3, 0, 1, '南芬区', '南芬', '南芬', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210521', '本溪满族自治县', 3, 0, 1, '本溪满族自治县', '本溪满族自治', '本溪满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@本溪_2105_id, '210522', '桓仁满族自治县', 3, 0, 1, '桓仁满族自治县', '桓仁满族自治', '桓仁满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210602', '元宝区', 3, 0, 1, '元宝区', '元宝', '元宝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210603', '振兴区', 3, 0, 1, '振兴区', '振兴', '振兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210604', '振安区', 3, 0, 1, '振安区', '振安', '振安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210624', '宽甸满族自治县', 3, 0, 1, '宽甸满族自治县', '宽甸满族自治', '宽甸满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210681', '东港市', 3, 0, 1, '东港市', '东港', '东港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丹东_2106_id, '210682', '凤城市', 3, 0, 1, '凤城市', '凤城', '凤城', '', NOW(), NOW(), 0);
-- 已插入 500/3056 条区级数据

-- 批次 6：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210702', '古塔区', 3, 0, 1, '古塔区', '古塔', '古塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210703', '凌河区', 3, 0, 1, '凌河区', '凌河', '凌河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210711', '太和区', 3, 0, 1, '太和区', '太和', '太和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210726', '黑山县', 3, 0, 1, '黑山县', '黑山', '黑山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210727', '义县', 3, 0, 1, '义县', '义', '义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210781', '凌海市', 3, 0, 1, '凌海市', '凌海', '凌海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@锦州_2107_id, '210782', '北镇市', 3, 0, 1, '北镇市', '北镇', '北镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210802', '站前区', 3, 0, 1, '站前区', '站前', '站前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210803', '西市区', 3, 0, 1, '西市区', '西市', '西市', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210804', '鲅鱼圈区', 3, 0, 1, '鲅鱼圈区', '鲅鱼圈', '鲅鱼圈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210811', '老边区', 3, 0, 1, '老边区', '老边', '老边', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210881', '盖州市', 3, 0, 1, '盖州市', '盖州', '盖州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@营口_2108_id, '210882', '大石桥市', 3, 0, 1, '大石桥市', '大石桥', '大石桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210902', '海州区', 3, 0, 1, '海州区', '海州', '海州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210903', '新邱区', 3, 0, 1, '新邱区', '新邱', '新邱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210904', '太平区', 3, 0, 1, '太平区', '太平', '太平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210905', '清河门区', 3, 0, 1, '清河门区', '清河门', '清河门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210911', '细河区', 3, 0, 1, '细河区', '细河', '细河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210921', '阜新蒙古族自治县', 3, 0, 1, '阜新蒙古族自治县', '阜新蒙古族自治', '阜新蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜新_2109_id, '210922', '彰武县', 3, 0, 1, '彰武县', '彰武', '彰武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211002', '白塔区', 3, 0, 1, '白塔区', '白塔', '白塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211003', '文圣区', 3, 0, 1, '文圣区', '文圣', '文圣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211004', '宏伟区', 3, 0, 1, '宏伟区', '宏伟', '宏伟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211005', '弓长岭区', 3, 0, 1, '弓长岭区', '弓长岭', '弓长岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211011', '太子河区', 3, 0, 1, '太子河区', '太子河', '太子河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211021', '辽阳县', 3, 0, 1, '辽阳县', '辽阳', '辽阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽阳_2110_id, '211081', '灯塔市', 3, 0, 1, '灯塔市', '灯塔', '灯塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盘锦_2111_id, '211102', '双台子区', 3, 0, 1, '双台子区', '双台子', '双台子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盘锦_2111_id, '211103', '兴隆台区', 3, 0, 1, '兴隆台区', '兴隆台', '兴隆台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盘锦_2111_id, '211104', '大洼区', 3, 0, 1, '大洼区', '大洼', '大洼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盘锦_2111_id, '211122', '盘山县', 3, 0, 1, '盘山县', '盘山', '盘山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211202', '银州区', 3, 0, 1, '银州区', '银州', '银州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211204', '清河区', 3, 0, 1, '清河区', '清河', '清河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211221', '铁岭县', 3, 0, 1, '铁岭县', '铁岭', '铁岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211223', '西丰县', 3, 0, 1, '西丰县', '西丰', '西丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211224', '昌图县', 3, 0, 1, '昌图县', '昌图', '昌图', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211281', '调兵山市', 3, 0, 1, '调兵山市', '调兵山', '调兵山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铁岭_2112_id, '211282', '开原市', 3, 0, 1, '开原市', '开原', '开原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211302', '双塔区', 3, 0, 1, '双塔区', '双塔', '双塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211303', '龙城区', 3, 0, 1, '龙城区', '龙城', '龙城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211321', '朝阳县', 3, 0, 1, '朝阳县', '朝阳', '朝阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211322', '建平县', 3, 0, 1, '建平县', '建平', '建平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211324', '喀喇沁左翼蒙古族自治县', 3, 0, 1, '喀喇沁左翼蒙古族自治县', '喀喇沁左翼蒙古族自治', '喀喇沁左翼蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211381', '北票市', 3, 0, 1, '北票市', '北票', '北票', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@朝阳_2113_id, '211382', '凌源市', 3, 0, 1, '凌源市', '凌源', '凌源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211402', '连山区', 3, 0, 1, '连山区', '连山', '连山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211403', '龙港区', 3, 0, 1, '龙港区', '龙港', '龙港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211404', '南票区', 3, 0, 1, '南票区', '南票', '南票', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211421', '绥中县', 3, 0, 1, '绥中县', '绥中', '绥中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211422', '建昌县', 3, 0, 1, '建昌县', '建昌', '建昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@葫芦岛_2114_id, '211481', '兴城市', 3, 0, 1, '兴城市', '兴城', '兴城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220102', '南关区', 3, 0, 1, '南关区', '南关', '南关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220103', '宽城区', 3, 0, 1, '宽城区', '宽城', '宽城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220104', '朝阳区', 3, 0, 1, '朝阳区', '朝阳', '朝阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220105', '二道区', 3, 0, 1, '二道区', '二道', '二道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220106', '绿园区', 3, 0, 1, '绿园区', '绿园', '绿园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220112', '双阳区', 3, 0, 1, '双阳区', '双阳', '双阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220113', '九台区', 3, 0, 1, '九台区', '九台', '九台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220122', '农安县', 3, 0, 1, '农安县', '农安', '农安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220171', '长春经济技术开发区', 3, 0, 1, '长春经济技术开发区', '长春经济技术开发', '长春经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220172', '长春净月高新技术产业开发区', 3, 0, 1, '长春净月高新技术产业开发区', '长春净月高新技术产业开发', '长春净月高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220173', '长春高新技术产业开发区', 3, 0, 1, '长春高新技术产业开发区', '长春高新技术产业开发', '长春高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220174', '长春汽车经济技术开发区', 3, 0, 1, '长春汽车经济技术开发区', '长春汽车经济技术开发', '长春汽车经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220182', '榆树市', 3, 0, 1, '榆树市', '榆树', '榆树', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220183', '德惠市', 3, 0, 1, '德惠市', '德惠', '德惠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长春_2201_id, '220184', '公主岭市', 3, 0, 1, '公主岭市', '公主岭', '公主岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220202', '昌邑区', 3, 0, 1, '昌邑区', '昌邑', '昌邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220203', '龙潭区', 3, 0, 1, '龙潭区', '龙潭', '龙潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220204', '船营区', 3, 0, 1, '船营区', '船营', '船营', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220211', '丰满区', 3, 0, 1, '丰满区', '丰满', '丰满', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220221', '永吉县', 3, 0, 1, '永吉县', '永吉', '永吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220271', '吉林经济开发区', 3, 0, 1, '吉林经济开发区', '吉林经济开发', '吉林经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220272', '吉林高新技术产业开发区', 3, 0, 1, '吉林高新技术产业开发区', '吉林高新技术产业开发', '吉林高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220273', '吉林中国新加坡食品区', 3, 0, 1, '吉林中国新加坡食品区', '吉林中国新加坡食品', '吉林中国新加坡食品', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220281', '蛟河市', 3, 0, 1, '蛟河市', '蛟河', '蛟河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220282', '桦甸市', 3, 0, 1, '桦甸市', '桦甸', '桦甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220283', '舒兰市', 3, 0, 1, '舒兰市', '舒兰', '舒兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉林_2202_id, '220284', '磐石市', 3, 0, 1, '磐石市', '磐石', '磐石', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四平_2203_id, '220302', '铁西区', 3, 0, 1, '铁西区', '铁西', '铁西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四平_2203_id, '220303', '铁东区', 3, 0, 1, '铁东区', '铁东', '铁东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四平_2203_id, '220322', '梨树县', 3, 0, 1, '梨树县', '梨树', '梨树', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四平_2203_id, '220323', '伊通满族自治县', 3, 0, 1, '伊通满族自治县', '伊通满族自治', '伊通满族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@四平_2203_id, '220382', '双辽市', 3, 0, 1, '双辽市', '双辽', '双辽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽源_2204_id, '220402', '龙山区', 3, 0, 1, '龙山区', '龙山', '龙山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽源_2204_id, '220403', '西安区', 3, 0, 1, '西安区', '西安', '西安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽源_2204_id, '220421', '东丰县', 3, 0, 1, '东丰县', '东丰', '东丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@辽源_2204_id, '220422', '东辽县', 3, 0, 1, '东辽县', '东辽', '东辽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220502', '东昌区', 3, 0, 1, '东昌区', '东昌', '东昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220503', '二道江区', 3, 0, 1, '二道江区', '二道江', '二道江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220521', '通化县', 3, 0, 1, '通化县', '通化', '通化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220523', '辉南县', 3, 0, 1, '辉南县', '辉南', '辉南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220524', '柳河县', 3, 0, 1, '柳河县', '柳河', '柳河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220581', '梅河口市', 3, 0, 1, '梅河口市', '梅河口', '梅河口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@通化_2205_id, '220582', '集安市', 3, 0, 1, '集安市', '集安', '集安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220602', '浑江区', 3, 0, 1, '浑江区', '浑江', '浑江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220605', '江源区', 3, 0, 1, '江源区', '江源', '江源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220621', '抚松县', 3, 0, 1, '抚松县', '抚松', '抚松', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220622', '靖宇县', 3, 0, 1, '靖宇县', '靖宇', '靖宇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220623', '长白朝鲜族自治县', 3, 0, 1, '长白朝鲜族自治县', '长白朝鲜族自治', '长白朝鲜族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白山_2206_id, '220681', '临江市', 3, 0, 1, '临江市', '临江', '临江', '', NOW(), NOW(), 0);
-- 已插入 600/3056 条区级数据

-- 批次 7：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220702', '宁江区', 3, 0, 1, '宁江区', '宁江', '宁江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220721', '前郭尔罗斯蒙古族自治县', 3, 0, 1, '前郭尔罗斯蒙古族自治县', '前郭尔罗斯蒙古族自治', '前郭尔罗斯蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220722', '长岭县', 3, 0, 1, '长岭县', '长岭', '长岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220723', '乾安县', 3, 0, 1, '乾安县', '乾安', '乾安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220771', '吉林松原经济开发区', 3, 0, 1, '吉林松原经济开发区', '吉林松原经济开发', '吉林松原经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@松原_2207_id, '220781', '扶余市', 3, 0, 1, '扶余市', '扶余', '扶余', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220802', '洮北区', 3, 0, 1, '洮北区', '洮北', '洮北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220821', '镇赉县', 3, 0, 1, '镇赉县', '镇赉', '镇赉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220822', '通榆县', 3, 0, 1, '通榆县', '通榆', '通榆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220871', '吉林白城经济开发区', 3, 0, 1, '吉林白城经济开发区', '吉林白城经济开发', '吉林白城经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220881', '洮南市', 3, 0, 1, '洮南市', '洮南', '洮南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白城_2208_id, '220882', '大安市', 3, 0, 1, '大安市', '大安', '大安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222401', '延吉市', 3, 0, 1, '延吉市', '延吉', '延吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222402', '图们市', 3, 0, 1, '图们市', '图们', '图们', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222403', '敦化市', 3, 0, 1, '敦化市', '敦化', '敦化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222404', '珲春市', 3, 0, 1, '珲春市', '珲春', '珲春', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222405', '龙井市', 3, 0, 1, '龙井市', '龙井', '龙井', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222406', '和龙市', 3, 0, 1, '和龙市', '和龙', '和龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222424', '汪清县', 3, 0, 1, '汪清县', '汪清', '汪清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延边朝鲜族_2224_id, '222426', '安图县', 3, 0, 1, '安图县', '安图', '安图', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230102', '道里区', 3, 0, 1, '道里区', '道里', '道里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230103', '南岗区', 3, 0, 1, '南岗区', '南岗', '南岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230104', '道外区', 3, 0, 1, '道外区', '道外', '道外', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230108', '平房区', 3, 0, 1, '平房区', '平房', '平房', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230109', '松北区', 3, 0, 1, '松北区', '松北', '松北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230110', '香坊区', 3, 0, 1, '香坊区', '香坊', '香坊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230111', '呼兰区', 3, 0, 1, '呼兰区', '呼兰', '呼兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230112', '阿城区', 3, 0, 1, '阿城区', '阿城', '阿城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230113', '双城区', 3, 0, 1, '双城区', '双城', '双城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230123', '依兰县', 3, 0, 1, '依兰县', '依兰', '依兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230124', '方正县', 3, 0, 1, '方正县', '方正', '方正', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230125', '宾县', 3, 0, 1, '宾县', '宾', '宾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230126', '巴彦县', 3, 0, 1, '巴彦县', '巴彦', '巴彦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230127', '木兰县', 3, 0, 1, '木兰县', '木兰', '木兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230128', '通河县', 3, 0, 1, '通河县', '通河', '通河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230129', '延寿县', 3, 0, 1, '延寿县', '延寿', '延寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230183', '尚志市', 3, 0, 1, '尚志市', '尚志', '尚志', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈尔滨_2301_id, '230184', '五常市', 3, 0, 1, '五常市', '五常', '五常', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230202', '龙沙区', 3, 0, 1, '龙沙区', '龙沙', '龙沙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230203', '建华区', 3, 0, 1, '建华区', '建华', '建华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230204', '铁锋区', 3, 0, 1, '铁锋区', '铁锋', '铁锋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230205', '昂昂溪区', 3, 0, 1, '昂昂溪区', '昂昂溪', '昂昂溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230206', '富拉尔基区', 3, 0, 1, '富拉尔基区', '富拉尔基', '富拉尔基', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230207', '碾子山区', 3, 0, 1, '碾子山区', '碾子山', '碾子山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230208', '梅里斯达斡尔族区', 3, 0, 1, '梅里斯达斡尔族区', '梅里斯达斡尔族', '梅里斯达斡尔族', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230221', '龙江县', 3, 0, 1, '龙江县', '龙江', '龙江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230223', '依安县', 3, 0, 1, '依安县', '依安', '依安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230224', '泰来县', 3, 0, 1, '泰来县', '泰来', '泰来', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230225', '甘南县', 3, 0, 1, '甘南县', '甘南', '甘南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230227', '富裕县', 3, 0, 1, '富裕县', '富裕', '富裕', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230229', '克山县', 3, 0, 1, '克山县', '克山', '克山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230230', '克东县', 3, 0, 1, '克东县', '克东', '克东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230231', '拜泉县', 3, 0, 1, '拜泉县', '拜泉', '拜泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@齐齐哈尔_2302_id, '230281', '讷河市', 3, 0, 1, '讷河市', '讷河', '讷河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230302', '鸡冠区', 3, 0, 1, '鸡冠区', '鸡冠', '鸡冠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230303', '恒山区', 3, 0, 1, '恒山区', '恒山', '恒山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230304', '滴道区', 3, 0, 1, '滴道区', '滴道', '滴道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230305', '梨树区', 3, 0, 1, '梨树区', '梨树', '梨树', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230306', '城子河区', 3, 0, 1, '城子河区', '城子河', '城子河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230307', '麻山区', 3, 0, 1, '麻山区', '麻山', '麻山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230321', '鸡东县', 3, 0, 1, '鸡东县', '鸡东', '鸡东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230381', '虎林市', 3, 0, 1, '虎林市', '虎林', '虎林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鸡西_2303_id, '230382', '密山市', 3, 0, 1, '密山市', '密山', '密山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230402', '向阳区', 3, 0, 1, '向阳区', '向阳', '向阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230403', '工农区', 3, 0, 1, '工农区', '工农', '工农', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230404', '南山区', 3, 0, 1, '南山区', '南山', '南山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230405', '兴安区', 3, 0, 1, '兴安区', '兴安', '兴安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230406', '东山区', 3, 0, 1, '东山区', '东山', '东山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230407', '兴山区', 3, 0, 1, '兴山区', '兴山', '兴山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230421', '萝北县', 3, 0, 1, '萝北县', '萝北', '萝北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤岗_2304_id, '230422', '绥滨县', 3, 0, 1, '绥滨县', '绥滨', '绥滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230502', '尖山区', 3, 0, 1, '尖山区', '尖山', '尖山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230503', '岭东区', 3, 0, 1, '岭东区', '岭东', '岭东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230505', '四方台区', 3, 0, 1, '四方台区', '四方台', '四方台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230506', '宝山区', 3, 0, 1, '宝山区', '宝山', '宝山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230521', '集贤县', 3, 0, 1, '集贤县', '集贤', '集贤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230522', '友谊县', 3, 0, 1, '友谊县', '友谊', '友谊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230523', '宝清县', 3, 0, 1, '宝清县', '宝清', '宝清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@双鸭山_2305_id, '230524', '饶河县', 3, 0, 1, '饶河县', '饶河', '饶河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230602', '萨尔图区', 3, 0, 1, '萨尔图区', '萨尔图', '萨尔图', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230603', '龙凤区', 3, 0, 1, '龙凤区', '龙凤', '龙凤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230604', '让胡路区', 3, 0, 1, '让胡路区', '让胡路', '让胡路', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230605', '红岗区', 3, 0, 1, '红岗区', '红岗', '红岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230606', '大同区', 3, 0, 1, '大同区', '大同', '大同', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230621', '肇州县', 3, 0, 1, '肇州县', '肇州', '肇州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230622', '肇源县', 3, 0, 1, '肇源县', '肇源', '肇源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230623', '林甸县', 3, 0, 1, '林甸县', '林甸', '林甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230624', '杜尔伯特蒙古族自治县', 3, 0, 1, '杜尔伯特蒙古族自治县', '杜尔伯特蒙古族自治', '杜尔伯特蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大庆_2306_id, '230671', '大庆高新技术产业开发区', 3, 0, 1, '大庆高新技术产业开发区', '大庆高新技术产业开发', '大庆高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230717', '伊美区', 3, 0, 1, '伊美区', '伊美', '伊美', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230718', '乌翠区', 3, 0, 1, '乌翠区', '乌翠', '乌翠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230719', '友好区', 3, 0, 1, '友好区', '友好', '友好', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230722', '嘉荫县', 3, 0, 1, '嘉荫县', '嘉荫', '嘉荫', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230723', '汤旺县', 3, 0, 1, '汤旺县', '汤旺', '汤旺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230724', '丰林县', 3, 0, 1, '丰林县', '丰林', '丰林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230725', '大箐山县', 3, 0, 1, '大箐山县', '大箐山', '大箐山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230726', '南岔县', 3, 0, 1, '南岔县', '南岔', '南岔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230751', '金林区', 3, 0, 1, '金林区', '金林', '金林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊春_2307_id, '230781', '铁力市', 3, 0, 1, '铁力市', '铁力', '铁力', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230803', '向阳区', 3, 0, 1, '向阳区', '向阳', '向阳', '', NOW(), NOW(), 0);
-- 已插入 700/3056 条区级数据

-- 批次 8：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230804', '前进区', 3, 0, 1, '前进区', '前进', '前进', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230805', '东风区', 3, 0, 1, '东风区', '东风', '东风', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230811', '郊区', 3, 0, 1, '郊区', '郊', '郊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230822', '桦南县', 3, 0, 1, '桦南县', '桦南', '桦南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230826', '桦川县', 3, 0, 1, '桦川县', '桦川', '桦川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230828', '汤原县', 3, 0, 1, '汤原县', '汤原', '汤原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230881', '同江市', 3, 0, 1, '同江市', '同江', '同江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230882', '富锦市', 3, 0, 1, '富锦市', '富锦', '富锦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佳木斯_2308_id, '230883', '抚远市', 3, 0, 1, '抚远市', '抚远', '抚远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@七台河_2309_id, '230902', '新兴区', 3, 0, 1, '新兴区', '新兴', '新兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@七台河_2309_id, '230903', '桃山区', 3, 0, 1, '桃山区', '桃山', '桃山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@七台河_2309_id, '230904', '茄子河区', 3, 0, 1, '茄子河区', '茄子河', '茄子河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@七台河_2309_id, '230921', '勃利县', 3, 0, 1, '勃利县', '勃利', '勃利', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231002', '东安区', 3, 0, 1, '东安区', '东安', '东安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231003', '阳明区', 3, 0, 1, '阳明区', '阳明', '阳明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231004', '爱民区', 3, 0, 1, '爱民区', '爱民', '爱民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231005', '西安区', 3, 0, 1, '西安区', '西安', '西安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231025', '林口县', 3, 0, 1, '林口县', '林口', '林口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231081', '绥芬河市', 3, 0, 1, '绥芬河市', '绥芬河', '绥芬河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231083', '海林市', 3, 0, 1, '海林市', '海林', '海林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231084', '宁安市', 3, 0, 1, '宁安市', '宁安', '宁安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231085', '穆棱市', 3, 0, 1, '穆棱市', '穆棱', '穆棱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@牡丹江_2310_id, '231086', '东宁市', 3, 0, 1, '东宁市', '东宁', '东宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231102', '爱辉区', 3, 0, 1, '爱辉区', '爱辉', '爱辉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231123', '逊克县', 3, 0, 1, '逊克县', '逊克', '逊克', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231124', '孙吴县', 3, 0, 1, '孙吴县', '孙吴', '孙吴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231181', '北安市', 3, 0, 1, '北安市', '北安', '北安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231182', '五大连池市', 3, 0, 1, '五大连池市', '五大连池', '五大连池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黑河_2311_id, '231183', '嫩江市', 3, 0, 1, '嫩江市', '嫩江', '嫩江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231202', '北林区', 3, 0, 1, '北林区', '北林', '北林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231221', '望奎县', 3, 0, 1, '望奎县', '望奎', '望奎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231222', '兰西县', 3, 0, 1, '兰西县', '兰西', '兰西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231223', '青冈县', 3, 0, 1, '青冈县', '青冈', '青冈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231224', '庆安县', 3, 0, 1, '庆安县', '庆安', '庆安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231225', '明水县', 3, 0, 1, '明水县', '明水', '明水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231226', '绥棱县', 3, 0, 1, '绥棱县', '绥棱', '绥棱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231281', '安达市', 3, 0, 1, '安达市', '安达', '安达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231282', '肇东市', 3, 0, 1, '肇东市', '肇东', '肇东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绥化_2312_id, '231283', '海伦市', 3, 0, 1, '海伦市', '海伦', '海伦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232701', '漠河市', 3, 0, 1, '漠河市', '漠河', '漠河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232721', '呼玛县', 3, 0, 1, '呼玛县', '呼玛', '呼玛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232722', '塔河县', 3, 0, 1, '塔河县', '塔河', '塔河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232761', '加格达奇区', 3, 0, 1, '加格达奇区', '加格达奇', '加格达奇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232762', '松岭区', 3, 0, 1, '松岭区', '松岭', '松岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232763', '新林区', 3, 0, 1, '新林区', '新林', '新林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大兴安岭地_2327_id, '232764', '呼中区', 3, 0, 1, '呼中区', '呼中', '呼中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310101', '黄浦区', 3, 0, 1, '黄浦区', '黄浦', '黄浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310104', '徐汇区', 3, 0, 1, '徐汇区', '徐汇', '徐汇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310105', '长宁区', 3, 0, 1, '长宁区', '长宁', '长宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310106', '静安区', 3, 0, 1, '静安区', '静安', '静安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310107', '普陀区', 3, 0, 1, '普陀区', '普陀', '普陀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310109', '虹口区', 3, 0, 1, '虹口区', '虹口', '虹口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310110', '杨浦区', 3, 0, 1, '杨浦区', '杨浦', '杨浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310112', '闵行区', 3, 0, 1, '闵行区', '闵行', '闵行', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310113', '宝山区', 3, 0, 1, '宝山区', '宝山', '宝山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310114', '嘉定区', 3, 0, 1, '嘉定区', '嘉定', '嘉定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310115', '浦东新区', 3, 0, 1, '浦东新区', '浦东新', '浦东新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310116', '金山区', 3, 0, 1, '金山区', '金山', '金山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310117', '松江区', 3, 0, 1, '松江区', '松江', '松江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310118', '青浦区', 3, 0, 1, '青浦区', '青浦', '青浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310120', '奉贤区', 3, 0, 1, '奉贤区', '奉贤', '奉贤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上海_31_id, '310151', '崇明区', 3, 0, 1, '崇明区', '崇明', '崇明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320102', '玄武区', 3, 0, 1, '玄武区', '玄武', '玄武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320104', '秦淮区', 3, 0, 1, '秦淮区', '秦淮', '秦淮', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320105', '建邺区', 3, 0, 1, '建邺区', '建邺', '建邺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320106', '鼓楼区', 3, 0, 1, '鼓楼区', '鼓楼', '鼓楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320111', '浦口区', 3, 0, 1, '浦口区', '浦口', '浦口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320113', '栖霞区', 3, 0, 1, '栖霞区', '栖霞', '栖霞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320114', '雨花台区', 3, 0, 1, '雨花台区', '雨花台', '雨花台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320115', '江宁区', 3, 0, 1, '江宁区', '江宁', '江宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320116', '六合区', 3, 0, 1, '六合区', '六合', '六合', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320117', '溧水区', 3, 0, 1, '溧水区', '溧水', '溧水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南京_3201_id, '320118', '高淳区', 3, 0, 1, '高淳区', '高淳', '高淳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320205', '锡山区', 3, 0, 1, '锡山区', '锡山', '锡山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320206', '惠山区', 3, 0, 1, '惠山区', '惠山', '惠山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320211', '滨湖区', 3, 0, 1, '滨湖区', '滨湖', '滨湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320213', '梁溪区', 3, 0, 1, '梁溪区', '梁溪', '梁溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320214', '新吴区', 3, 0, 1, '新吴区', '新吴', '新吴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320281', '江阴市', 3, 0, 1, '江阴市', '江阴', '江阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@无锡_3202_id, '320282', '宜兴市', 3, 0, 1, '宜兴市', '宜兴', '宜兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320302', '鼓楼区', 3, 0, 1, '鼓楼区', '鼓楼', '鼓楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320303', '云龙区', 3, 0, 1, '云龙区', '云龙', '云龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320305', '贾汪区', 3, 0, 1, '贾汪区', '贾汪', '贾汪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320311', '泉山区', 3, 0, 1, '泉山区', '泉山', '泉山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320312', '铜山区', 3, 0, 1, '铜山区', '铜山', '铜山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320321', '丰县', 3, 0, 1, '丰县', '丰', '丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320322', '沛县', 3, 0, 1, '沛县', '沛', '沛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320324', '睢宁县', 3, 0, 1, '睢宁县', '睢宁', '睢宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320371', '徐州经济技术开发区', 3, 0, 1, '徐州经济技术开发区', '徐州经济技术开发', '徐州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320381', '新沂市', 3, 0, 1, '新沂市', '新沂', '新沂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@徐州_3203_id, '320382', '邳州市', 3, 0, 1, '邳州市', '邳州', '邳州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320402', '天宁区', 3, 0, 1, '天宁区', '天宁', '天宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320404', '钟楼区', 3, 0, 1, '钟楼区', '钟楼', '钟楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320411', '新北区', 3, 0, 1, '新北区', '新北', '新北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320412', '武进区', 3, 0, 1, '武进区', '武进', '武进', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320413', '金坛区', 3, 0, 1, '金坛区', '金坛', '金坛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常州_3204_id, '320481', '溧阳市', 3, 0, 1, '溧阳市', '溧阳', '溧阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320505', '虎丘区', 3, 0, 1, '虎丘区', '虎丘', '虎丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320506', '吴中区', 3, 0, 1, '吴中区', '吴中', '吴中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320507', '相城区', 3, 0, 1, '相城区', '相城', '相城', '', NOW(), NOW(), 0);
-- 已插入 800/3056 条区级数据

-- 批次 9：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320508', '姑苏区', 3, 0, 1, '姑苏区', '姑苏', '姑苏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320509', '吴江区', 3, 0, 1, '吴江区', '吴江', '吴江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320576', '苏州工业园区', 3, 0, 1, '苏州工业园区', '苏州工业园', '苏州工业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320581', '常熟市', 3, 0, 1, '常熟市', '常熟', '常熟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320582', '张家港市', 3, 0, 1, '张家港市', '张家港', '张家港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320583', '昆山市', 3, 0, 1, '昆山市', '昆山', '昆山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@苏州_3205_id, '320585', '太仓市', 3, 0, 1, '太仓市', '太仓', '太仓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320612', '通州区', 3, 0, 1, '通州区', '通州', '通州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320613', '崇川区', 3, 0, 1, '崇川区', '崇川', '崇川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320614', '海门区', 3, 0, 1, '海门区', '海门', '海门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320623', '如东县', 3, 0, 1, '如东县', '如东', '如东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320671', '南通经济技术开发区', 3, 0, 1, '南通经济技术开发区', '南通经济技术开发', '南通经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320681', '启东市', 3, 0, 1, '启东市', '启东', '启东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320682', '如皋市', 3, 0, 1, '如皋市', '如皋', '如皋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南通_3206_id, '320685', '海安市', 3, 0, 1, '海安市', '海安', '海安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320703', '连云区', 3, 0, 1, '连云区', '连云', '连云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320706', '海州区', 3, 0, 1, '海州区', '海州', '海州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320707', '赣榆区', 3, 0, 1, '赣榆区', '赣榆', '赣榆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320722', '东海县', 3, 0, 1, '东海县', '东海', '东海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320723', '灌云县', 3, 0, 1, '灌云县', '灌云', '灌云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320724', '灌南县', 3, 0, 1, '灌南县', '灌南', '灌南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@连云港_3207_id, '320771', '连云港经济技术开发区', 3, 0, 1, '连云港经济技术开发区', '连云港经济技术开发', '连云港经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320803', '淮安区', 3, 0, 1, '淮安区', '淮安', '淮安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320804', '淮阴区', 3, 0, 1, '淮阴区', '淮阴', '淮阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320812', '清江浦区', 3, 0, 1, '清江浦区', '清江浦', '清江浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320813', '洪泽区', 3, 0, 1, '洪泽区', '洪泽', '洪泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320826', '涟水县', 3, 0, 1, '涟水县', '涟水', '涟水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320830', '盱眙县', 3, 0, 1, '盱眙县', '盱眙', '盱眙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320831', '金湖县', 3, 0, 1, '金湖县', '金湖', '金湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮安_3208_id, '320871', '淮安经济技术开发区', 3, 0, 1, '淮安经济技术开发区', '淮安经济技术开发', '淮安经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320902', '亭湖区', 3, 0, 1, '亭湖区', '亭湖', '亭湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320903', '盐都区', 3, 0, 1, '盐都区', '盐都', '盐都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320904', '大丰区', 3, 0, 1, '大丰区', '大丰', '大丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320921', '响水县', 3, 0, 1, '响水县', '响水', '响水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320922', '滨海县', 3, 0, 1, '滨海县', '滨海', '滨海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320923', '阜宁县', 3, 0, 1, '阜宁县', '阜宁', '阜宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320924', '射阳县', 3, 0, 1, '射阳县', '射阳', '射阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320925', '建湖县', 3, 0, 1, '建湖县', '建湖', '建湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320971', '盐城经济技术开发区', 3, 0, 1, '盐城经济技术开发区', '盐城经济技术开发', '盐城经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@盐城_3209_id, '320981', '东台市', 3, 0, 1, '东台市', '东台', '东台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321002', '广陵区', 3, 0, 1, '广陵区', '广陵', '广陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321003', '邗江区', 3, 0, 1, '邗江区', '邗江', '邗江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321012', '江都区', 3, 0, 1, '江都区', '江都', '江都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321023', '宝应县', 3, 0, 1, '宝应县', '宝应', '宝应', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321071', '扬州经济技术开发区', 3, 0, 1, '扬州经济技术开发区', '扬州经济技术开发', '扬州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321081', '仪征市', 3, 0, 1, '仪征市', '仪征', '仪征', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@扬州_3210_id, '321084', '高邮市', 3, 0, 1, '高邮市', '高邮', '高邮', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321102', '京口区', 3, 0, 1, '京口区', '京口', '京口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321111', '润州区', 3, 0, 1, '润州区', '润州', '润州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321112', '丹徒区', 3, 0, 1, '丹徒区', '丹徒', '丹徒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321171', '镇江新区', 3, 0, 1, '镇江新区', '镇江新', '镇江新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321181', '丹阳市', 3, 0, 1, '丹阳市', '丹阳', '丹阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321182', '扬中市', 3, 0, 1, '扬中市', '扬中', '扬中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@镇江_3211_id, '321183', '句容市', 3, 0, 1, '句容市', '句容', '句容', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321202', '海陵区', 3, 0, 1, '海陵区', '海陵', '海陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321203', '高港区', 3, 0, 1, '高港区', '高港', '高港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321204', '姜堰区', 3, 0, 1, '姜堰区', '姜堰', '姜堰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321281', '兴化市', 3, 0, 1, '兴化市', '兴化', '兴化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321282', '靖江市', 3, 0, 1, '靖江市', '靖江', '靖江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰州_3212_id, '321283', '泰兴市', 3, 0, 1, '泰兴市', '泰兴', '泰兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321302', '宿城区', 3, 0, 1, '宿城区', '宿城', '宿城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321311', '宿豫区', 3, 0, 1, '宿豫区', '宿豫', '宿豫', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321322', '沭阳县', 3, 0, 1, '沭阳县', '沭阳', '沭阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321323', '泗阳县', 3, 0, 1, '泗阳县', '泗阳', '泗阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321324', '泗洪县', 3, 0, 1, '泗洪县', '泗洪', '泗洪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿迁_3213_id, '321371', '宿迁经济技术开发区', 3, 0, 1, '宿迁经济技术开发区', '宿迁经济技术开发', '宿迁经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330102', '上城区', 3, 0, 1, '上城区', '上城', '上城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330105', '拱墅区', 3, 0, 1, '拱墅区', '拱墅', '拱墅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330106', '西湖区', 3, 0, 1, '西湖区', '西湖', '西湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330108', '滨江区', 3, 0, 1, '滨江区', '滨江', '滨江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330109', '萧山区', 3, 0, 1, '萧山区', '萧山', '萧山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330110', '余杭区', 3, 0, 1, '余杭区', '余杭', '余杭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330111', '富阳区', 3, 0, 1, '富阳区', '富阳', '富阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330112', '临安区', 3, 0, 1, '临安区', '临安', '临安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330113', '临平区', 3, 0, 1, '临平区', '临平', '临平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330114', '钱塘区', 3, 0, 1, '钱塘区', '钱塘', '钱塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330122', '桐庐县', 3, 0, 1, '桐庐县', '桐庐', '桐庐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330127', '淳安县', 3, 0, 1, '淳安县', '淳安', '淳安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@杭州_3301_id, '330182', '建德市', 3, 0, 1, '建德市', '建德', '建德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330203', '海曙区', 3, 0, 1, '海曙区', '海曙', '海曙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330205', '江北区', 3, 0, 1, '江北区', '江北', '江北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330206', '北仑区', 3, 0, 1, '北仑区', '北仑', '北仑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330211', '镇海区', 3, 0, 1, '镇海区', '镇海', '镇海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330212', '鄞州区', 3, 0, 1, '鄞州区', '鄞州', '鄞州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330213', '奉化区', 3, 0, 1, '奉化区', '奉化', '奉化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330225', '象山县', 3, 0, 1, '象山县', '象山', '象山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330226', '宁海县', 3, 0, 1, '宁海县', '宁海', '宁海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330281', '余姚市', 3, 0, 1, '余姚市', '余姚', '余姚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁波_3302_id, '330282', '慈溪市', 3, 0, 1, '慈溪市', '慈溪', '慈溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330302', '鹿城区', 3, 0, 1, '鹿城区', '鹿城', '鹿城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330303', '龙湾区', 3, 0, 1, '龙湾区', '龙湾', '龙湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330304', '瓯海区', 3, 0, 1, '瓯海区', '瓯海', '瓯海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330305', '洞头区', 3, 0, 1, '洞头区', '洞头', '洞头', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330324', '永嘉县', 3, 0, 1, '永嘉县', '永嘉', '永嘉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330326', '平阳县', 3, 0, 1, '平阳县', '平阳', '平阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330327', '苍南县', 3, 0, 1, '苍南县', '苍南', '苍南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330328', '文成县', 3, 0, 1, '文成县', '文成', '文成', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330329', '泰顺县', 3, 0, 1, '泰顺县', '泰顺', '泰顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330381', '瑞安市', 3, 0, 1, '瑞安市', '瑞安', '瑞安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330382', '乐清市', 3, 0, 1, '乐清市', '乐清', '乐清', '', NOW(), NOW(), 0);
-- 已插入 900/3056 条区级数据

-- 批次 10：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@温州_3303_id, '330383', '龙港市', 3, 0, 1, '龙港市', '龙港', '龙港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330402', '南湖区', 3, 0, 1, '南湖区', '南湖', '南湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330411', '秀洲区', 3, 0, 1, '秀洲区', '秀洲', '秀洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330421', '嘉善县', 3, 0, 1, '嘉善县', '嘉善', '嘉善', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330424', '海盐县', 3, 0, 1, '海盐县', '海盐', '海盐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330481', '海宁市', 3, 0, 1, '海宁市', '海宁', '海宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330482', '平湖市', 3, 0, 1, '平湖市', '平湖', '平湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉兴_3304_id, '330483', '桐乡市', 3, 0, 1, '桐乡市', '桐乡', '桐乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖州_3305_id, '330502', '吴兴区', 3, 0, 1, '吴兴区', '吴兴', '吴兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖州_3305_id, '330503', '南浔区', 3, 0, 1, '南浔区', '南浔', '南浔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖州_3305_id, '330521', '德清县', 3, 0, 1, '德清县', '德清', '德清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖州_3305_id, '330522', '长兴县', 3, 0, 1, '长兴县', '长兴', '长兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湖州_3305_id, '330523', '安吉县', 3, 0, 1, '安吉县', '安吉', '安吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330602', '越城区', 3, 0, 1, '越城区', '越城', '越城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330603', '柯桥区', 3, 0, 1, '柯桥区', '柯桥', '柯桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330604', '上虞区', 3, 0, 1, '上虞区', '上虞', '上虞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330624', '新昌县', 3, 0, 1, '新昌县', '新昌', '新昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330681', '诸暨市', 3, 0, 1, '诸暨市', '诸暨', '诸暨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绍兴_3306_id, '330683', '嵊州市', 3, 0, 1, '嵊州市', '嵊州', '嵊州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330702', '婺城区', 3, 0, 1, '婺城区', '婺城', '婺城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330703', '金东区', 3, 0, 1, '金东区', '金东', '金东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330723', '武义县', 3, 0, 1, '武义县', '武义', '武义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330726', '浦江县', 3, 0, 1, '浦江县', '浦江', '浦江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330727', '磐安县', 3, 0, 1, '磐安县', '磐安', '磐安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330781', '兰溪市', 3, 0, 1, '兰溪市', '兰溪', '兰溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330782', '义乌市', 3, 0, 1, '义乌市', '义乌', '义乌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330783', '东阳市', 3, 0, 1, '东阳市', '东阳', '东阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金华_3307_id, '330784', '永康市', 3, 0, 1, '永康市', '永康', '永康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330802', '柯城区', 3, 0, 1, '柯城区', '柯城', '柯城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330803', '衢江区', 3, 0, 1, '衢江区', '衢江', '衢江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330822', '常山县', 3, 0, 1, '常山县', '常山', '常山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330824', '开化县', 3, 0, 1, '开化县', '开化', '开化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330825', '龙游县', 3, 0, 1, '龙游县', '龙游', '龙游', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衢州_3308_id, '330881', '江山市', 3, 0, 1, '江山市', '江山', '江山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@舟山_3309_id, '330902', '定海区', 3, 0, 1, '定海区', '定海', '定海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@舟山_3309_id, '330903', '普陀区', 3, 0, 1, '普陀区', '普陀', '普陀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@舟山_3309_id, '330921', '岱山县', 3, 0, 1, '岱山县', '岱山', '岱山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@舟山_3309_id, '330922', '嵊泗县', 3, 0, 1, '嵊泗县', '嵊泗', '嵊泗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331002', '椒江区', 3, 0, 1, '椒江区', '椒江', '椒江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331003', '黄岩区', 3, 0, 1, '黄岩区', '黄岩', '黄岩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331004', '路桥区', 3, 0, 1, '路桥区', '路桥', '路桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331022', '三门县', 3, 0, 1, '三门县', '三门', '三门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331023', '天台县', 3, 0, 1, '天台县', '天台', '天台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331024', '仙居县', 3, 0, 1, '仙居县', '仙居', '仙居', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331081', '温岭市', 3, 0, 1, '温岭市', '温岭', '温岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331082', '临海市', 3, 0, 1, '临海市', '临海', '临海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@台州_3310_id, '331083', '玉环市', 3, 0, 1, '玉环市', '玉环', '玉环', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331102', '莲都区', 3, 0, 1, '莲都区', '莲都', '莲都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331121', '青田县', 3, 0, 1, '青田县', '青田', '青田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331122', '缙云县', 3, 0, 1, '缙云县', '缙云', '缙云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331123', '遂昌县', 3, 0, 1, '遂昌县', '遂昌', '遂昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331124', '松阳县', 3, 0, 1, '松阳县', '松阳', '松阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331125', '云和县', 3, 0, 1, '云和县', '云和', '云和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331126', '庆元县', 3, 0, 1, '庆元县', '庆元', '庆元', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331127', '景宁畲族自治县', 3, 0, 1, '景宁畲族自治县', '景宁畲族自治', '景宁畲族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽水_3311_id, '331181', '龙泉市', 3, 0, 1, '龙泉市', '龙泉', '龙泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340102', '瑶海区', 3, 0, 1, '瑶海区', '瑶海', '瑶海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340103', '庐阳区', 3, 0, 1, '庐阳区', '庐阳', '庐阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340104', '蜀山区', 3, 0, 1, '蜀山区', '蜀山', '蜀山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340111', '包河区', 3, 0, 1, '包河区', '包河', '包河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340121', '长丰县', 3, 0, 1, '长丰县', '长丰', '长丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340122', '肥东县', 3, 0, 1, '肥东县', '肥东', '肥东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340123', '肥西县', 3, 0, 1, '肥西县', '肥西', '肥西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340124', '庐江县', 3, 0, 1, '庐江县', '庐江', '庐江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340176', '合肥高新技术产业开发区', 3, 0, 1, '合肥高新技术产业开发区', '合肥高新技术产业开发', '合肥高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340177', '合肥经济技术开发区', 3, 0, 1, '合肥经济技术开发区', '合肥经济技术开发', '合肥经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340178', '合肥新站高新技术产业开发区', 3, 0, 1, '合肥新站高新技术产业开发区', '合肥新站高新技术产业开发', '合肥新站高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@合肥_3401_id, '340181', '巢湖市', 3, 0, 1, '巢湖市', '巢湖', '巢湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340202', '镜湖区', 3, 0, 1, '镜湖区', '镜湖', '镜湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340207', '鸠江区', 3, 0, 1, '鸠江区', '鸠江', '鸠江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340209', '弋江区', 3, 0, 1, '弋江区', '弋江', '弋江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340210', '湾沚区', 3, 0, 1, '湾沚区', '湾沚', '湾沚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340212', '繁昌区', 3, 0, 1, '繁昌区', '繁昌', '繁昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340223', '南陵县', 3, 0, 1, '南陵县', '南陵', '南陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340271', '芜湖经济技术开发区', 3, 0, 1, '芜湖经济技术开发区', '芜湖经济技术开发', '芜湖经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340272', '安徽芜湖三山经济开发区', 3, 0, 1, '安徽芜湖三山经济开发区', '安徽芜湖三山经济开发', '安徽芜湖三山经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@芜湖_3402_id, '340281', '无为市', 3, 0, 1, '无为市', '无为', '无为', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340302', '龙子湖区', 3, 0, 1, '龙子湖区', '龙子湖', '龙子湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340303', '蚌山区', 3, 0, 1, '蚌山区', '蚌山', '蚌山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340304', '禹会区', 3, 0, 1, '禹会区', '禹会', '禹会', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340311', '淮上区', 3, 0, 1, '淮上区', '淮上', '淮上', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340321', '怀远县', 3, 0, 1, '怀远县', '怀远', '怀远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340322', '五河县', 3, 0, 1, '五河县', '五河', '五河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340323', '固镇县', 3, 0, 1, '固镇县', '固镇', '固镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340371', '蚌埠市高新技术开发区', 3, 0, 1, '蚌埠市高新技术开发区', '蚌埠市高新技术开发', '蚌埠市高新技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@蚌埠_3403_id, '340372', '蚌埠市经济开发区', 3, 0, 1, '蚌埠市经济开发区', '蚌埠市经济开发', '蚌埠市经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340402', '大通区', 3, 0, 1, '大通区', '大通', '大通', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340403', '田家庵区', 3, 0, 1, '田家庵区', '田家庵', '田家庵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340404', '谢家集区', 3, 0, 1, '谢家集区', '谢家集', '谢家集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340405', '八公山区', 3, 0, 1, '八公山区', '八公山', '八公山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340406', '潘集区', 3, 0, 1, '潘集区', '潘集', '潘集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340421', '凤台县', 3, 0, 1, '凤台县', '凤台', '凤台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮南_3404_id, '340422', '寿县', 3, 0, 1, '寿县', '寿', '寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340503', '花山区', 3, 0, 1, '花山区', '花山', '花山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340504', '雨山区', 3, 0, 1, '雨山区', '雨山', '雨山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340506', '博望区', 3, 0, 1, '博望区', '博望', '博望', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340521', '当涂县', 3, 0, 1, '当涂县', '当涂', '当涂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340522', '含山县', 3, 0, 1, '含山县', '含山', '含山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@马鞍山_3405_id, '340523', '和县', 3, 0, 1, '和县', '和', '和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮北_3406_id, '340602', '杜集区', 3, 0, 1, '杜集区', '杜集', '杜集', '', NOW(), NOW(), 0);
-- 已插入 1000/3056 条区级数据

-- 批次 11：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮北_3406_id, '340603', '相山区', 3, 0, 1, '相山区', '相山', '相山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮北_3406_id, '340604', '烈山区', 3, 0, 1, '烈山区', '烈山', '烈山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淮北_3406_id, '340621', '濉溪县', 3, 0, 1, '濉溪县', '濉溪', '濉溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜陵_3407_id, '340705', '铜官区', 3, 0, 1, '铜官区', '铜官', '铜官', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜陵_3407_id, '340706', '义安区', 3, 0, 1, '义安区', '义安', '义安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜陵_3407_id, '340711', '郊区', 3, 0, 1, '郊区', '郊', '郊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜陵_3407_id, '340722', '枞阳县', 3, 0, 1, '枞阳县', '枞阳', '枞阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340802', '迎江区', 3, 0, 1, '迎江区', '迎江', '迎江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340803', '大观区', 3, 0, 1, '大观区', '大观', '大观', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340811', '宜秀区', 3, 0, 1, '宜秀区', '宜秀', '宜秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340822', '怀宁县', 3, 0, 1, '怀宁县', '怀宁', '怀宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340825', '太湖县', 3, 0, 1, '太湖县', '太湖', '太湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340826', '宿松县', 3, 0, 1, '宿松县', '宿松', '宿松', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340827', '望江县', 3, 0, 1, '望江县', '望江', '望江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340828', '岳西县', 3, 0, 1, '岳西县', '岳西', '岳西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340871', '安徽安庆经济开发区', 3, 0, 1, '安徽安庆经济开发区', '安徽安庆经济开发', '安徽安庆经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340881', '桐城市', 3, 0, 1, '桐城市', '桐城', '桐城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安庆_3408_id, '340882', '潜山市', 3, 0, 1, '潜山市', '潜山', '潜山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341002', '屯溪区', 3, 0, 1, '屯溪区', '屯溪', '屯溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341003', '黄山区', 3, 0, 1, '黄山区', '黄山', '黄山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341004', '徽州区', 3, 0, 1, '徽州区', '徽州', '徽州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341021', '歙县', 3, 0, 1, '歙县', '歙', '歙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341022', '休宁县', 3, 0, 1, '休宁县', '休宁', '休宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341023', '黟县', 3, 0, 1, '黟县', '黟', '黟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄山_3410_id, '341024', '祁门县', 3, 0, 1, '祁门县', '祁门', '祁门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341102', '琅琊区', 3, 0, 1, '琅琊区', '琅琊', '琅琊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341103', '南谯区', 3, 0, 1, '南谯区', '南谯', '南谯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341122', '来安县', 3, 0, 1, '来安县', '来安', '来安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341124', '全椒县', 3, 0, 1, '全椒县', '全椒', '全椒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341125', '定远县', 3, 0, 1, '定远县', '定远', '定远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341126', '凤阳县', 3, 0, 1, '凤阳县', '凤阳', '凤阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341171', '中新苏滁高新技术产业开发区', 3, 0, 1, '中新苏滁高新技术产业开发区', '中新苏滁高新技术产业开发', '中新苏滁高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341172', '滁州经济技术开发区', 3, 0, 1, '滁州经济技术开发区', '滁州经济技术开发', '滁州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341181', '天长市', 3, 0, 1, '天长市', '天长', '天长', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滁州_3411_id, '341182', '明光市', 3, 0, 1, '明光市', '明光', '明光', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341202', '颍州区', 3, 0, 1, '颍州区', '颍州', '颍州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341203', '颍东区', 3, 0, 1, '颍东区', '颍东', '颍东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341204', '颍泉区', 3, 0, 1, '颍泉区', '颍泉', '颍泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341221', '临泉县', 3, 0, 1, '临泉县', '临泉', '临泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341222', '太和县', 3, 0, 1, '太和县', '太和', '太和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341225', '阜南县', 3, 0, 1, '阜南县', '阜南', '阜南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341226', '颍上县', 3, 0, 1, '颍上县', '颍上', '颍上', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341271', '阜阳合肥现代产业园区', 3, 0, 1, '阜阳合肥现代产业园区', '阜阳合肥现代产业园', '阜阳合肥现代产业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341272', '阜阳经济技术开发区', 3, 0, 1, '阜阳经济技术开发区', '阜阳经济技术开发', '阜阳经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阜阳_3412_id, '341282', '界首市', 3, 0, 1, '界首市', '界首', '界首', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341302', '埇桥区', 3, 0, 1, '埇桥区', '埇桥', '埇桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341321', '砀山县', 3, 0, 1, '砀山县', '砀山', '砀山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341322', '萧县', 3, 0, 1, '萧县', '萧', '萧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341323', '灵璧县', 3, 0, 1, '灵璧县', '灵璧', '灵璧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341324', '泗县', 3, 0, 1, '泗县', '泗', '泗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341371', '宿州马鞍山现代产业园区', 3, 0, 1, '宿州马鞍山现代产业园区', '宿州马鞍山现代产业园', '宿州马鞍山现代产业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宿州_3413_id, '341372', '宿州经济技术开发区', 3, 0, 1, '宿州经济技术开发区', '宿州经济技术开发', '宿州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341502', '金安区', 3, 0, 1, '金安区', '金安', '金安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341503', '裕安区', 3, 0, 1, '裕安区', '裕安', '裕安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341504', '叶集区', 3, 0, 1, '叶集区', '叶集', '叶集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341522', '霍邱县', 3, 0, 1, '霍邱县', '霍邱', '霍邱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341523', '舒城县', 3, 0, 1, '舒城县', '舒城', '舒城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341524', '金寨县', 3, 0, 1, '金寨县', '金寨', '金寨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六安_3415_id, '341525', '霍山县', 3, 0, 1, '霍山县', '霍山', '霍山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@亳州_3416_id, '341602', '谯城区', 3, 0, 1, '谯城区', '谯城', '谯城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@亳州_3416_id, '341621', '涡阳县', 3, 0, 1, '涡阳县', '涡阳', '涡阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@亳州_3416_id, '341622', '蒙城县', 3, 0, 1, '蒙城县', '蒙城', '蒙城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@亳州_3416_id, '341623', '利辛县', 3, 0, 1, '利辛县', '利辛', '利辛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@池州_3417_id, '341702', '贵池区', 3, 0, 1, '贵池区', '贵池', '贵池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@池州_3417_id, '341721', '东至县', 3, 0, 1, '东至县', '东至', '东至', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@池州_3417_id, '341722', '石台县', 3, 0, 1, '石台县', '石台', '石台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@池州_3417_id, '341723', '青阳县', 3, 0, 1, '青阳县', '青阳', '青阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341802', '宣州区', 3, 0, 1, '宣州区', '宣州', '宣州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341821', '郎溪县', 3, 0, 1, '郎溪县', '郎溪', '郎溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341823', '泾县', 3, 0, 1, '泾县', '泾', '泾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341824', '绩溪县', 3, 0, 1, '绩溪县', '绩溪', '绩溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341825', '旌德县', 3, 0, 1, '旌德县', '旌德', '旌德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341871', '宣城市经济开发区', 3, 0, 1, '宣城市经济开发区', '宣城市经济开发', '宣城市经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341881', '宁国市', 3, 0, 1, '宁国市', '宁国', '宁国', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宣城_3418_id, '341882', '广德市', 3, 0, 1, '广德市', '广德', '广德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350102', '鼓楼区', 3, 0, 1, '鼓楼区', '鼓楼', '鼓楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350103', '台江区', 3, 0, 1, '台江区', '台江', '台江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350104', '仓山区', 3, 0, 1, '仓山区', '仓山', '仓山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350105', '马尾区', 3, 0, 1, '马尾区', '马尾', '马尾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350111', '晋安区', 3, 0, 1, '晋安区', '晋安', '晋安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350112', '长乐区', 3, 0, 1, '长乐区', '长乐', '长乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350121', '闽侯县', 3, 0, 1, '闽侯县', '闽侯', '闽侯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350122', '连江县', 3, 0, 1, '连江县', '连江', '连江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350123', '罗源县', 3, 0, 1, '罗源县', '罗源', '罗源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350124', '闽清县', 3, 0, 1, '闽清县', '闽清', '闽清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350125', '永泰县', 3, 0, 1, '永泰县', '永泰', '永泰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350128', '平潭县', 3, 0, 1, '平潭县', '平潭', '平潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@福州_3501_id, '350181', '福清市', 3, 0, 1, '福清市', '福清', '福清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350203', '思明区', 3, 0, 1, '思明区', '思明', '思明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350205', '海沧区', 3, 0, 1, '海沧区', '海沧', '海沧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350206', '湖里区', 3, 0, 1, '湖里区', '湖里', '湖里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350211', '集美区', 3, 0, 1, '集美区', '集美', '集美', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350212', '同安区', 3, 0, 1, '同安区', '同安', '同安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@厦门_3502_id, '350213', '翔安区', 3, 0, 1, '翔安区', '翔安', '翔安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@莆田_3503_id, '350302', '城厢区', 3, 0, 1, '城厢区', '城厢', '城厢', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@莆田_3503_id, '350303', '涵江区', 3, 0, 1, '涵江区', '涵江', '涵江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@莆田_3503_id, '350304', '荔城区', 3, 0, 1, '荔城区', '荔城', '荔城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@莆田_3503_id, '350305', '秀屿区', 3, 0, 1, '秀屿区', '秀屿', '秀屿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@莆田_3503_id, '350322', '仙游县', 3, 0, 1, '仙游县', '仙游', '仙游', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350404', '三元区', 3, 0, 1, '三元区', '三元', '三元', '', NOW(), NOW(), 0);
-- 已插入 1100/3056 条区级数据

-- 批次 12：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350405', '沙县区', 3, 0, 1, '沙县区', '沙县', '沙县', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350421', '明溪县', 3, 0, 1, '明溪县', '明溪', '明溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350423', '清流县', 3, 0, 1, '清流县', '清流', '清流', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350424', '宁化县', 3, 0, 1, '宁化县', '宁化', '宁化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350425', '大田县', 3, 0, 1, '大田县', '大田', '大田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350426', '尤溪县', 3, 0, 1, '尤溪县', '尤溪', '尤溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350428', '将乐县', 3, 0, 1, '将乐县', '将乐', '将乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350429', '泰宁县', 3, 0, 1, '泰宁县', '泰宁', '泰宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350430', '建宁县', 3, 0, 1, '建宁县', '建宁', '建宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三明_3504_id, '350481', '永安市', 3, 0, 1, '永安市', '永安', '永安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350502', '鲤城区', 3, 0, 1, '鲤城区', '鲤城', '鲤城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350503', '丰泽区', 3, 0, 1, '丰泽区', '丰泽', '丰泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350504', '洛江区', 3, 0, 1, '洛江区', '洛江', '洛江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350505', '泉港区', 3, 0, 1, '泉港区', '泉港', '泉港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350521', '惠安县', 3, 0, 1, '惠安县', '惠安', '惠安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350524', '安溪县', 3, 0, 1, '安溪县', '安溪', '安溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350525', '永春县', 3, 0, 1, '永春县', '永春', '永春', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350526', '德化县', 3, 0, 1, '德化县', '德化', '德化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350527', '金门县', 3, 0, 1, '金门县', '金门', '金门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350581', '石狮市', 3, 0, 1, '石狮市', '石狮', '石狮', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350582', '晋江市', 3, 0, 1, '晋江市', '晋江', '晋江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泉州_3505_id, '350583', '南安市', 3, 0, 1, '南安市', '南安', '南安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350602', '芗城区', 3, 0, 1, '芗城区', '芗城', '芗城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350603', '龙文区', 3, 0, 1, '龙文区', '龙文', '龙文', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350604', '龙海区', 3, 0, 1, '龙海区', '龙海', '龙海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350605', '长泰区', 3, 0, 1, '长泰区', '长泰', '长泰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350622', '云霄县', 3, 0, 1, '云霄县', '云霄', '云霄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350623', '漳浦县', 3, 0, 1, '漳浦县', '漳浦', '漳浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350624', '诏安县', 3, 0, 1, '诏安县', '诏安', '诏安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350626', '东山县', 3, 0, 1, '东山县', '东山', '东山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350627', '南靖县', 3, 0, 1, '南靖县', '南靖', '南靖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350628', '平和县', 3, 0, 1, '平和县', '平和', '平和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漳州_3506_id, '350629', '华安县', 3, 0, 1, '华安县', '华安', '华安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350702', '延平区', 3, 0, 1, '延平区', '延平', '延平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350703', '建阳区', 3, 0, 1, '建阳区', '建阳', '建阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350721', '顺昌县', 3, 0, 1, '顺昌县', '顺昌', '顺昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350722', '浦城县', 3, 0, 1, '浦城县', '浦城', '浦城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350723', '光泽县', 3, 0, 1, '光泽县', '光泽', '光泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350724', '松溪县', 3, 0, 1, '松溪县', '松溪', '松溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350725', '政和县', 3, 0, 1, '政和县', '政和', '政和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350781', '邵武市', 3, 0, 1, '邵武市', '邵武', '邵武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350782', '武夷山市', 3, 0, 1, '武夷山市', '武夷山', '武夷山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南平_3507_id, '350783', '建瓯市', 3, 0, 1, '建瓯市', '建瓯', '建瓯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350802', '新罗区', 3, 0, 1, '新罗区', '新罗', '新罗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350803', '永定区', 3, 0, 1, '永定区', '永定', '永定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350821', '长汀县', 3, 0, 1, '长汀县', '长汀', '长汀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350823', '上杭县', 3, 0, 1, '上杭县', '上杭', '上杭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350824', '武平县', 3, 0, 1, '武平县', '武平', '武平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350825', '连城县', 3, 0, 1, '连城县', '连城', '连城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@龙岩_3508_id, '350881', '漳平市', 3, 0, 1, '漳平市', '漳平', '漳平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350902', '蕉城区', 3, 0, 1, '蕉城区', '蕉城', '蕉城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350921', '霞浦县', 3, 0, 1, '霞浦县', '霞浦', '霞浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350922', '古田县', 3, 0, 1, '古田县', '古田', '古田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350923', '屏南县', 3, 0, 1, '屏南县', '屏南', '屏南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350924', '寿宁县', 3, 0, 1, '寿宁县', '寿宁', '寿宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350925', '周宁县', 3, 0, 1, '周宁县', '周宁', '周宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350926', '柘荣县', 3, 0, 1, '柘荣县', '柘荣', '柘荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350981', '福安市', 3, 0, 1, '福安市', '福安', '福安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宁德_3509_id, '350982', '福鼎市', 3, 0, 1, '福鼎市', '福鼎', '福鼎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360102', '东湖区', 3, 0, 1, '东湖区', '东湖', '东湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360103', '西湖区', 3, 0, 1, '西湖区', '西湖', '西湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360104', '青云谱区', 3, 0, 1, '青云谱区', '青云谱', '青云谱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360111', '青山湖区', 3, 0, 1, '青山湖区', '青山湖', '青山湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360112', '新建区', 3, 0, 1, '新建区', '新建', '新建', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360113', '红谷滩区', 3, 0, 1, '红谷滩区', '红谷滩', '红谷滩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360121', '南昌县', 3, 0, 1, '南昌县', '南昌', '南昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360123', '安义县', 3, 0, 1, '安义县', '安义', '安义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南昌_3601_id, '360124', '进贤县', 3, 0, 1, '进贤县', '进贤', '进贤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@景德镇_3602_id, '360202', '昌江区', 3, 0, 1, '昌江区', '昌江', '昌江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@景德镇_3602_id, '360203', '珠山区', 3, 0, 1, '珠山区', '珠山', '珠山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@景德镇_3602_id, '360222', '浮梁县', 3, 0, 1, '浮梁县', '浮梁', '浮梁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@景德镇_3602_id, '360281', '乐平市', 3, 0, 1, '乐平市', '乐平', '乐平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@萍乡_3603_id, '360302', '安源区', 3, 0, 1, '安源区', '安源', '安源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@萍乡_3603_id, '360313', '湘东区', 3, 0, 1, '湘东区', '湘东', '湘东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@萍乡_3603_id, '360321', '莲花县', 3, 0, 1, '莲花县', '莲花', '莲花', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@萍乡_3603_id, '360322', '上栗县', 3, 0, 1, '上栗县', '上栗', '上栗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@萍乡_3603_id, '360323', '芦溪县', 3, 0, 1, '芦溪县', '芦溪', '芦溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360402', '濂溪区', 3, 0, 1, '濂溪区', '濂溪', '濂溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360403', '浔阳区', 3, 0, 1, '浔阳区', '浔阳', '浔阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360404', '柴桑区', 3, 0, 1, '柴桑区', '柴桑', '柴桑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360423', '武宁县', 3, 0, 1, '武宁县', '武宁', '武宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360424', '修水县', 3, 0, 1, '修水县', '修水', '修水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360425', '永修县', 3, 0, 1, '永修县', '永修', '永修', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360426', '德安县', 3, 0, 1, '德安县', '德安', '德安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360428', '都昌县', 3, 0, 1, '都昌县', '都昌', '都昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360429', '湖口县', 3, 0, 1, '湖口县', '湖口', '湖口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360430', '彭泽县', 3, 0, 1, '彭泽县', '彭泽', '彭泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360481', '瑞昌市', 3, 0, 1, '瑞昌市', '瑞昌', '瑞昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360482', '共青城市', 3, 0, 1, '共青城市', '共青城', '共青城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@九江_3604_id, '360483', '庐山市', 3, 0, 1, '庐山市', '庐山', '庐山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新余_3605_id, '360502', '渝水区', 3, 0, 1, '渝水区', '渝水', '渝水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新余_3605_id, '360521', '分宜县', 3, 0, 1, '分宜县', '分宜', '分宜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹰潭_3606_id, '360602', '月湖区', 3, 0, 1, '月湖区', '月湖', '月湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹰潭_3606_id, '360603', '余江区', 3, 0, 1, '余江区', '余江', '余江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹰潭_3606_id, '360681', '贵溪市', 3, 0, 1, '贵溪市', '贵溪', '贵溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360702', '章贡区', 3, 0, 1, '章贡区', '章贡', '章贡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360703', '南康区', 3, 0, 1, '南康区', '南康', '南康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360704', '赣县区', 3, 0, 1, '赣县区', '赣县', '赣县', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360722', '信丰县', 3, 0, 1, '信丰县', '信丰', '信丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360723', '大余县', 3, 0, 1, '大余县', '大余', '大余', '', NOW(), NOW(), 0);
-- 已插入 1200/3056 条区级数据

-- 批次 13：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360724', '上犹县', 3, 0, 1, '上犹县', '上犹', '上犹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360725', '崇义县', 3, 0, 1, '崇义县', '崇义', '崇义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360726', '安远县', 3, 0, 1, '安远县', '安远', '安远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360728', '定南县', 3, 0, 1, '定南县', '定南', '定南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360729', '全南县', 3, 0, 1, '全南县', '全南', '全南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360730', '宁都县', 3, 0, 1, '宁都县', '宁都', '宁都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360731', '于都县', 3, 0, 1, '于都县', '于都', '于都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360732', '兴国县', 3, 0, 1, '兴国县', '兴国', '兴国', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360733', '会昌县', 3, 0, 1, '会昌县', '会昌', '会昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360734', '寻乌县', 3, 0, 1, '寻乌县', '寻乌', '寻乌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360735', '石城县', 3, 0, 1, '石城县', '石城', '石城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360781', '瑞金市', 3, 0, 1, '瑞金市', '瑞金', '瑞金', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@赣州_3607_id, '360783', '龙南市', 3, 0, 1, '龙南市', '龙南', '龙南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360802', '吉州区', 3, 0, 1, '吉州区', '吉州', '吉州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360803', '青原区', 3, 0, 1, '青原区', '青原', '青原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360821', '吉安县', 3, 0, 1, '吉安县', '吉安', '吉安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360822', '吉水县', 3, 0, 1, '吉水县', '吉水', '吉水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360823', '峡江县', 3, 0, 1, '峡江县', '峡江', '峡江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360824', '新干县', 3, 0, 1, '新干县', '新干', '新干', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360825', '永丰县', 3, 0, 1, '永丰县', '永丰', '永丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360826', '泰和县', 3, 0, 1, '泰和县', '泰和', '泰和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360827', '遂川县', 3, 0, 1, '遂川县', '遂川', '遂川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360828', '万安县', 3, 0, 1, '万安县', '万安', '万安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360829', '安福县', 3, 0, 1, '安福县', '安福', '安福', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360830', '永新县', 3, 0, 1, '永新县', '永新', '永新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吉安_3608_id, '360881', '井冈山市', 3, 0, 1, '井冈山市', '井冈山', '井冈山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360902', '袁州区', 3, 0, 1, '袁州区', '袁州', '袁州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360921', '奉新县', 3, 0, 1, '奉新县', '奉新', '奉新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360922', '万载县', 3, 0, 1, '万载县', '万载', '万载', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360923', '上高县', 3, 0, 1, '上高县', '上高', '上高', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360924', '宜丰县', 3, 0, 1, '宜丰县', '宜丰', '宜丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360925', '靖安县', 3, 0, 1, '靖安县', '靖安', '靖安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360926', '铜鼓县', 3, 0, 1, '铜鼓县', '铜鼓', '铜鼓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360981', '丰城市', 3, 0, 1, '丰城市', '丰城', '丰城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360982', '樟树市', 3, 0, 1, '樟树市', '樟树', '樟树', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜春_3609_id, '360983', '高安市', 3, 0, 1, '高安市', '高安', '高安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361002', '临川区', 3, 0, 1, '临川区', '临川', '临川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361003', '东乡区', 3, 0, 1, '东乡区', '东乡', '东乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361021', '南城县', 3, 0, 1, '南城县', '南城', '南城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361022', '黎川县', 3, 0, 1, '黎川县', '黎川', '黎川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361023', '南丰县', 3, 0, 1, '南丰县', '南丰', '南丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361024', '崇仁县', 3, 0, 1, '崇仁县', '崇仁', '崇仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361025', '乐安县', 3, 0, 1, '乐安县', '乐安', '乐安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361026', '宜黄县', 3, 0, 1, '宜黄县', '宜黄', '宜黄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361027', '金溪县', 3, 0, 1, '金溪县', '金溪', '金溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361028', '资溪县', 3, 0, 1, '资溪县', '资溪', '资溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@抚州_3610_id, '361030', '广昌县', 3, 0, 1, '广昌县', '广昌', '广昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361102', '信州区', 3, 0, 1, '信州区', '信州', '信州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361103', '广丰区', 3, 0, 1, '广丰区', '广丰', '广丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361104', '广信区', 3, 0, 1, '广信区', '广信', '广信', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361123', '玉山县', 3, 0, 1, '玉山县', '玉山', '玉山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361124', '铅山县', 3, 0, 1, '铅山县', '铅山', '铅山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361125', '横峰县', 3, 0, 1, '横峰县', '横峰', '横峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361126', '弋阳县', 3, 0, 1, '弋阳县', '弋阳', '弋阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361127', '余干县', 3, 0, 1, '余干县', '余干', '余干', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361128', '鄱阳县', 3, 0, 1, '鄱阳县', '鄱阳', '鄱阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361129', '万年县', 3, 0, 1, '万年县', '万年', '万年', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361130', '婺源县', 3, 0, 1, '婺源县', '婺源', '婺源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@上饶_3611_id, '361181', '德兴市', 3, 0, 1, '德兴市', '德兴', '德兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370102', '历下区', 3, 0, 1, '历下区', '历下', '历下', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370103', '市中区', 3, 0, 1, '市中区', '市中', '市中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370104', '槐荫区', 3, 0, 1, '槐荫区', '槐荫', '槐荫', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370105', '天桥区', 3, 0, 1, '天桥区', '天桥', '天桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370112', '历城区', 3, 0, 1, '历城区', '历城', '历城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370113', '长清区', 3, 0, 1, '长清区', '长清', '长清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370114', '章丘区', 3, 0, 1, '章丘区', '章丘', '章丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370115', '济阳区', 3, 0, 1, '济阳区', '济阳', '济阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370116', '莱芜区', 3, 0, 1, '莱芜区', '莱芜', '莱芜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370117', '钢城区', 3, 0, 1, '钢城区', '钢城', '钢城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370124', '平阴县', 3, 0, 1, '平阴县', '平阴', '平阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370126', '商河县', 3, 0, 1, '商河县', '商河', '商河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济南_3701_id, '370176', '济南高新技术产业开发区', 3, 0, 1, '济南高新技术产业开发区', '济南高新技术产业开发', '济南高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370202', '市南区', 3, 0, 1, '市南区', '市南', '市南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370203', '市北区', 3, 0, 1, '市北区', '市北', '市北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370211', '黄岛区', 3, 0, 1, '黄岛区', '黄岛', '黄岛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370212', '崂山区', 3, 0, 1, '崂山区', '崂山', '崂山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370213', '李沧区', 3, 0, 1, '李沧区', '李沧', '李沧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370214', '城阳区', 3, 0, 1, '城阳区', '城阳', '城阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370215', '即墨区', 3, 0, 1, '即墨区', '即墨', '即墨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370281', '胶州市', 3, 0, 1, '胶州市', '胶州', '胶州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370283', '平度市', 3, 0, 1, '平度市', '平度', '平度', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@青岛_3702_id, '370285', '莱西市', 3, 0, 1, '莱西市', '莱西', '莱西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370302', '淄川区', 3, 0, 1, '淄川区', '淄川', '淄川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370303', '张店区', 3, 0, 1, '张店区', '张店', '张店', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370304', '博山区', 3, 0, 1, '博山区', '博山', '博山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370305', '临淄区', 3, 0, 1, '临淄区', '临淄', '临淄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370306', '周村区', 3, 0, 1, '周村区', '周村', '周村', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370321', '桓台县', 3, 0, 1, '桓台县', '桓台', '桓台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370322', '高青县', 3, 0, 1, '高青县', '高青', '高青', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@淄博_3703_id, '370323', '沂源县', 3, 0, 1, '沂源县', '沂源', '沂源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370402', '市中区', 3, 0, 1, '市中区', '市中', '市中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370403', '薛城区', 3, 0, 1, '薛城区', '薛城', '薛城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370404', '峄城区', 3, 0, 1, '峄城区', '峄城', '峄城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370405', '台儿庄区', 3, 0, 1, '台儿庄区', '台儿庄', '台儿庄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370406', '山亭区', 3, 0, 1, '山亭区', '山亭', '山亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@枣庄_3704_id, '370481', '滕州市', 3, 0, 1, '滕州市', '滕州', '滕州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370502', '东营区', 3, 0, 1, '东营区', '东营', '东营', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370503', '河口区', 3, 0, 1, '河口区', '河口', '河口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370505', '垦利区', 3, 0, 1, '垦利区', '垦利', '垦利', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370522', '利津县', 3, 0, 1, '利津县', '利津', '利津', '', NOW(), NOW(), 0);
-- 已插入 1300/3056 条区级数据

-- 批次 14：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370523', '广饶县', 3, 0, 1, '广饶县', '广饶', '广饶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370571', '东营经济技术开发区', 3, 0, 1, '东营经济技术开发区', '东营经济技术开发', '东营经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东营_3705_id, '370572', '东营港经济开发区', 3, 0, 1, '东营港经济开发区', '东营港经济开发', '东营港经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370602', '芝罘区', 3, 0, 1, '芝罘区', '芝罘', '芝罘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370611', '福山区', 3, 0, 1, '福山区', '福山', '福山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370612', '牟平区', 3, 0, 1, '牟平区', '牟平', '牟平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370613', '莱山区', 3, 0, 1, '莱山区', '莱山', '莱山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370614', '蓬莱区', 3, 0, 1, '蓬莱区', '蓬莱', '蓬莱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370671', '烟台高新技术产业开发区', 3, 0, 1, '烟台高新技术产业开发区', '烟台高新技术产业开发', '烟台高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370676', '烟台经济技术开发区', 3, 0, 1, '烟台经济技术开发区', '烟台经济技术开发', '烟台经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370681', '龙口市', 3, 0, 1, '龙口市', '龙口', '龙口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370682', '莱阳市', 3, 0, 1, '莱阳市', '莱阳', '莱阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370683', '莱州市', 3, 0, 1, '莱州市', '莱州', '莱州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370685', '招远市', 3, 0, 1, '招远市', '招远', '招远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370686', '栖霞市', 3, 0, 1, '栖霞市', '栖霞', '栖霞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@烟台_3706_id, '370687', '海阳市', 3, 0, 1, '海阳市', '海阳', '海阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370702', '潍城区', 3, 0, 1, '潍城区', '潍城', '潍城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370703', '寒亭区', 3, 0, 1, '寒亭区', '寒亭', '寒亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370704', '坊子区', 3, 0, 1, '坊子区', '坊子', '坊子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370705', '奎文区', 3, 0, 1, '奎文区', '奎文', '奎文', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370724', '临朐县', 3, 0, 1, '临朐县', '临朐', '临朐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370725', '昌乐县', 3, 0, 1, '昌乐县', '昌乐', '昌乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370772', '潍坊滨海经济技术开发区', 3, 0, 1, '潍坊滨海经济技术开发区', '潍坊滨海经济技术开发', '潍坊滨海经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370781', '青州市', 3, 0, 1, '青州市', '青州', '青州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370782', '诸城市', 3, 0, 1, '诸城市', '诸城', '诸城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370783', '寿光市', 3, 0, 1, '寿光市', '寿光', '寿光', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370784', '安丘市', 3, 0, 1, '安丘市', '安丘', '安丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370785', '高密市', 3, 0, 1, '高密市', '高密', '高密', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潍坊_3707_id, '370786', '昌邑市', 3, 0, 1, '昌邑市', '昌邑', '昌邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370811', '任城区', 3, 0, 1, '任城区', '任城', '任城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370812', '兖州区', 3, 0, 1, '兖州区', '兖州', '兖州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370826', '微山县', 3, 0, 1, '微山县', '微山', '微山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370827', '鱼台县', 3, 0, 1, '鱼台县', '鱼台', '鱼台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370828', '金乡县', 3, 0, 1, '金乡县', '金乡', '金乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370829', '嘉祥县', 3, 0, 1, '嘉祥县', '嘉祥', '嘉祥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370830', '汶上县', 3, 0, 1, '汶上县', '汶上', '汶上', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370831', '泗水县', 3, 0, 1, '泗水县', '泗水', '泗水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370832', '梁山县', 3, 0, 1, '梁山县', '梁山', '梁山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370871', '济宁高新技术产业开发区', 3, 0, 1, '济宁高新技术产业开发区', '济宁高新技术产业开发', '济宁高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370881', '曲阜市', 3, 0, 1, '曲阜市', '曲阜', '曲阜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@济宁_3708_id, '370883', '邹城市', 3, 0, 1, '邹城市', '邹城', '邹城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370902', '泰山区', 3, 0, 1, '泰山区', '泰山', '泰山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370911', '岱岳区', 3, 0, 1, '岱岳区', '岱岳', '岱岳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370921', '宁阳县', 3, 0, 1, '宁阳县', '宁阳', '宁阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370923', '东平县', 3, 0, 1, '东平县', '东平', '东平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370982', '新泰市', 3, 0, 1, '新泰市', '新泰', '新泰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泰安_3709_id, '370983', '肥城市', 3, 0, 1, '肥城市', '肥城', '肥城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371002', '环翠区', 3, 0, 1, '环翠区', '环翠', '环翠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371003', '文登区', 3, 0, 1, '文登区', '文登', '文登', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371071', '威海火炬高技术产业开发区', 3, 0, 1, '威海火炬高技术产业开发区', '威海火炬高技术产业开发', '威海火炬高技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371072', '威海经济技术开发区', 3, 0, 1, '威海经济技术开发区', '威海经济技术开发', '威海经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371073', '威海临港经济技术开发区', 3, 0, 1, '威海临港经济技术开发区', '威海临港经济技术开发', '威海临港经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371082', '荣成市', 3, 0, 1, '荣成市', '荣成', '荣成', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@威海_3710_id, '371083', '乳山市', 3, 0, 1, '乳山市', '乳山', '乳山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日照_3711_id, '371102', '东港区', 3, 0, 1, '东港区', '东港', '东港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日照_3711_id, '371103', '岚山区', 3, 0, 1, '岚山区', '岚山', '岚山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日照_3711_id, '371121', '五莲县', 3, 0, 1, '五莲县', '五莲', '五莲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日照_3711_id, '371122', '莒县', 3, 0, 1, '莒县', '莒', '莒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日照_3711_id, '371171', '日照经济技术开发区', 3, 0, 1, '日照经济技术开发区', '日照经济技术开发', '日照经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371302', '兰山区', 3, 0, 1, '兰山区', '兰山', '兰山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371311', '罗庄区', 3, 0, 1, '罗庄区', '罗庄', '罗庄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371312', '河东区', 3, 0, 1, '河东区', '河东', '河东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371321', '沂南县', 3, 0, 1, '沂南县', '沂南', '沂南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371322', '郯城县', 3, 0, 1, '郯城县', '郯城', '郯城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371323', '沂水县', 3, 0, 1, '沂水县', '沂水', '沂水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371324', '兰陵县', 3, 0, 1, '兰陵县', '兰陵', '兰陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371325', '费县', 3, 0, 1, '费县', '费', '费', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371326', '平邑县', 3, 0, 1, '平邑县', '平邑', '平邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371327', '莒南县', 3, 0, 1, '莒南县', '莒南', '莒南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371328', '蒙阴县', 3, 0, 1, '蒙阴县', '蒙阴', '蒙阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371329', '临沭县', 3, 0, 1, '临沭县', '临沭', '临沭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沂_3713_id, '371371', '临沂高新技术产业开发区', 3, 0, 1, '临沂高新技术产业开发区', '临沂高新技术产业开发', '临沂高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371402', '德城区', 3, 0, 1, '德城区', '德城', '德城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371403', '陵城区', 3, 0, 1, '陵城区', '陵城', '陵城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371422', '宁津县', 3, 0, 1, '宁津县', '宁津', '宁津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371423', '庆云县', 3, 0, 1, '庆云县', '庆云', '庆云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371424', '临邑县', 3, 0, 1, '临邑县', '临邑', '临邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371425', '齐河县', 3, 0, 1, '齐河县', '齐河', '齐河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371426', '平原县', 3, 0, 1, '平原县', '平原', '平原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371427', '夏津县', 3, 0, 1, '夏津县', '夏津', '夏津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371428', '武城县', 3, 0, 1, '武城县', '武城', '武城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371471', '德州天衢新区', 3, 0, 1, '德州天衢新区', '德州天衢新', '德州天衢新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371481', '乐陵市', 3, 0, 1, '乐陵市', '乐陵', '乐陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德州_3714_id, '371482', '禹城市', 3, 0, 1, '禹城市', '禹城', '禹城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371502', '东昌府区', 3, 0, 1, '东昌府区', '东昌府', '东昌府', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371503', '茌平区', 3, 0, 1, '茌平区', '茌平', '茌平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371521', '阳谷县', 3, 0, 1, '阳谷县', '阳谷', '阳谷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371522', '莘县', 3, 0, 1, '莘县', '莘', '莘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371524', '东阿县', 3, 0, 1, '东阿县', '东阿', '东阿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371525', '冠县', 3, 0, 1, '冠县', '冠', '冠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371526', '高唐县', 3, 0, 1, '高唐县', '高唐', '高唐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@聊城_3715_id, '371581', '临清市', 3, 0, 1, '临清市', '临清', '临清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371602', '滨城区', 3, 0, 1, '滨城区', '滨城', '滨城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371603', '沾化区', 3, 0, 1, '沾化区', '沾化', '沾化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371621', '惠民县', 3, 0, 1, '惠民县', '惠民', '惠民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371622', '阳信县', 3, 0, 1, '阳信县', '阳信', '阳信', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371623', '无棣县', 3, 0, 1, '无棣县', '无棣', '无棣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371625', '博兴县', 3, 0, 1, '博兴县', '博兴', '博兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@滨州_3716_id, '371681', '邹平市', 3, 0, 1, '邹平市', '邹平', '邹平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371702', '牡丹区', 3, 0, 1, '牡丹区', '牡丹', '牡丹', '', NOW(), NOW(), 0);
-- 已插入 1400/3056 条区级数据

-- 批次 15：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371703', '定陶区', 3, 0, 1, '定陶区', '定陶', '定陶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371721', '曹县', 3, 0, 1, '曹县', '曹', '曹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371722', '单县', 3, 0, 1, '单县', '单', '单', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371723', '成武县', 3, 0, 1, '成武县', '成武', '成武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371724', '巨野县', 3, 0, 1, '巨野县', '巨野', '巨野', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371725', '郓城县', 3, 0, 1, '郓城县', '郓城', '郓城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371726', '鄄城县', 3, 0, 1, '鄄城县', '鄄城', '鄄城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371728', '东明县', 3, 0, 1, '东明县', '东明', '东明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371771', '菏泽经济技术开发区', 3, 0, 1, '菏泽经济技术开发区', '菏泽经济技术开发', '菏泽经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@菏泽_3717_id, '371772', '菏泽高新技术开发区', 3, 0, 1, '菏泽高新技术开发区', '菏泽高新技术开发', '菏泽高新技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410102', '中原区', 3, 0, 1, '中原区', '中原', '中原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410103', '二七区', 3, 0, 1, '二七区', '二七', '二七', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410104', '管城回族区', 3, 0, 1, '管城回族区', '管城回族', '管城回族', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410105', '金水区', 3, 0, 1, '金水区', '金水', '金水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410106', '上街区', 3, 0, 1, '上街区', '上街', '上街', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410108', '惠济区', 3, 0, 1, '惠济区', '惠济', '惠济', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410122', '中牟县', 3, 0, 1, '中牟县', '中牟', '中牟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410171', '郑州经济技术开发区', 3, 0, 1, '郑州经济技术开发区', '郑州经济技术开发', '郑州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410172', '郑州高新技术产业开发区', 3, 0, 1, '郑州高新技术产业开发区', '郑州高新技术产业开发', '郑州高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410173', '郑州航空港经济综合实验区', 3, 0, 1, '郑州航空港经济综合实验区', '郑州航空港经济综合实验', '郑州航空港经济综合实验', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410181', '巩义市', 3, 0, 1, '巩义市', '巩义', '巩义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410182', '荥阳市', 3, 0, 1, '荥阳市', '荥阳', '荥阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410183', '新密市', 3, 0, 1, '新密市', '新密', '新密', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410184', '新郑市', 3, 0, 1, '新郑市', '新郑', '新郑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郑州_4101_id, '410185', '登封市', 3, 0, 1, '登封市', '登封', '登封', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410202', '龙亭区', 3, 0, 1, '龙亭区', '龙亭', '龙亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410203', '顺河回族区', 3, 0, 1, '顺河回族区', '顺河回族', '顺河回族', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410204', '鼓楼区', 3, 0, 1, '鼓楼区', '鼓楼', '鼓楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410205', '禹王台区', 3, 0, 1, '禹王台区', '禹王台', '禹王台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410212', '祥符区', 3, 0, 1, '祥符区', '祥符', '祥符', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410221', '杞县', 3, 0, 1, '杞县', '杞', '杞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410222', '通许县', 3, 0, 1, '通许县', '通许', '通许', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410223', '尉氏县', 3, 0, 1, '尉氏县', '尉氏', '尉氏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@开封_4102_id, '410225', '兰考县', 3, 0, 1, '兰考县', '兰考', '兰考', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410302', '老城区', 3, 0, 1, '老城区', '老城', '老城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410303', '西工区', 3, 0, 1, '西工区', '西工', '西工', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410304', '瀍河回族区', 3, 0, 1, '瀍河回族区', '瀍河回族', '瀍河回族', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410305', '涧西区', 3, 0, 1, '涧西区', '涧西', '涧西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410307', '偃师区', 3, 0, 1, '偃师区', '偃师', '偃师', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410308', '孟津区', 3, 0, 1, '孟津区', '孟津', '孟津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410311', '洛龙区', 3, 0, 1, '洛龙区', '洛龙', '洛龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410323', '新安县', 3, 0, 1, '新安县', '新安', '新安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410324', '栾川县', 3, 0, 1, '栾川县', '栾川', '栾川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410325', '嵩县', 3, 0, 1, '嵩县', '嵩', '嵩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410326', '汝阳县', 3, 0, 1, '汝阳县', '汝阳', '汝阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410327', '宜阳县', 3, 0, 1, '宜阳县', '宜阳', '宜阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410328', '洛宁县', 3, 0, 1, '洛宁县', '洛宁', '洛宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410329', '伊川县', 3, 0, 1, '伊川县', '伊川', '伊川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@洛阳_4103_id, '410371', '洛阳高新技术产业开发区', 3, 0, 1, '洛阳高新技术产业开发区', '洛阳高新技术产业开发', '洛阳高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410402', '新华区', 3, 0, 1, '新华区', '新华', '新华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410403', '卫东区', 3, 0, 1, '卫东区', '卫东', '卫东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410404', '石龙区', 3, 0, 1, '石龙区', '石龙', '石龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410411', '湛河区', 3, 0, 1, '湛河区', '湛河', '湛河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410421', '宝丰县', 3, 0, 1, '宝丰县', '宝丰', '宝丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410422', '叶县', 3, 0, 1, '叶县', '叶', '叶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410423', '鲁山县', 3, 0, 1, '鲁山县', '鲁山', '鲁山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410425', '郏县', 3, 0, 1, '郏县', '郏', '郏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410471', '平顶山高新技术产业开发区', 3, 0, 1, '平顶山高新技术产业开发区', '平顶山高新技术产业开发', '平顶山高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410472', '平顶山市城乡一体化示范区', 3, 0, 1, '平顶山市城乡一体化示范区', '平顶山市城乡一体化示范', '平顶山市城乡一体化示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410481', '舞钢市', 3, 0, 1, '舞钢市', '舞钢', '舞钢', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平顶山_4104_id, '410482', '汝州市', 3, 0, 1, '汝州市', '汝州', '汝州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410502', '文峰区', 3, 0, 1, '文峰区', '文峰', '文峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410503', '北关区', 3, 0, 1, '北关区', '北关', '北关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410505', '殷都区', 3, 0, 1, '殷都区', '殷都', '殷都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410506', '龙安区', 3, 0, 1, '龙安区', '龙安', '龙安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410522', '安阳县', 3, 0, 1, '安阳县', '安阳', '安阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410523', '汤阴县', 3, 0, 1, '汤阴县', '汤阴', '汤阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410526', '滑县', 3, 0, 1, '滑县', '滑', '滑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410527', '内黄县', 3, 0, 1, '内黄县', '内黄', '内黄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410571', '安阳高新技术产业开发区', 3, 0, 1, '安阳高新技术产业开发区', '安阳高新技术产业开发', '安阳高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安阳_4105_id, '410581', '林州市', 3, 0, 1, '林州市', '林州', '林州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410602', '鹤山区', 3, 0, 1, '鹤山区', '鹤山', '鹤山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410603', '山城区', 3, 0, 1, '山城区', '山城', '山城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410611', '淇滨区', 3, 0, 1, '淇滨区', '淇滨', '淇滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410621', '浚县', 3, 0, 1, '浚县', '浚', '浚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410622', '淇县', 3, 0, 1, '淇县', '淇', '淇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鹤壁_4106_id, '410671', '鹤壁经济技术开发区', 3, 0, 1, '鹤壁经济技术开发区', '鹤壁经济技术开发', '鹤壁经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410702', '红旗区', 3, 0, 1, '红旗区', '红旗', '红旗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410703', '卫滨区', 3, 0, 1, '卫滨区', '卫滨', '卫滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410704', '凤泉区', 3, 0, 1, '凤泉区', '凤泉', '凤泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410711', '牧野区', 3, 0, 1, '牧野区', '牧野', '牧野', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410721', '新乡县', 3, 0, 1, '新乡县', '新乡', '新乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410724', '获嘉县', 3, 0, 1, '获嘉县', '获嘉', '获嘉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410725', '原阳县', 3, 0, 1, '原阳县', '原阳', '原阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410726', '延津县', 3, 0, 1, '延津县', '延津', '延津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410727', '封丘县', 3, 0, 1, '封丘县', '封丘', '封丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410771', '新乡高新技术产业开发区', 3, 0, 1, '新乡高新技术产业开发区', '新乡高新技术产业开发', '新乡高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410772', '新乡经济技术开发区', 3, 0, 1, '新乡经济技术开发区', '新乡经济技术开发', '新乡经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410773', '新乡市平原城乡一体化示范区', 3, 0, 1, '新乡市平原城乡一体化示范区', '新乡市平原城乡一体化示范', '新乡市平原城乡一体化示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410781', '卫辉市', 3, 0, 1, '卫辉市', '卫辉', '卫辉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410782', '辉县市', 3, 0, 1, '辉县市', '辉县', '辉县', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@新乡_4107_id, '410783', '长垣市', 3, 0, 1, '长垣市', '长垣', '长垣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410802', '解放区', 3, 0, 1, '解放区', '解放', '解放', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410803', '中站区', 3, 0, 1, '中站区', '中站', '中站', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410804', '马村区', 3, 0, 1, '马村区', '马村', '马村', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410811', '山阳区', 3, 0, 1, '山阳区', '山阳', '山阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410821', '修武县', 3, 0, 1, '修武县', '修武', '修武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410822', '博爱县', 3, 0, 1, '博爱县', '博爱', '博爱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410823', '武陟县', 3, 0, 1, '武陟县', '武陟', '武陟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410825', '温县', 3, 0, 1, '温县', '温', '温', '', NOW(), NOW(), 0);
-- 已插入 1500/3056 条区级数据

-- 批次 16：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410871', '焦作城乡一体化示范区', 3, 0, 1, '焦作城乡一体化示范区', '焦作城乡一体化示范', '焦作城乡一体化示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410882', '沁阳市', 3, 0, 1, '沁阳市', '沁阳', '沁阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@焦作_4108_id, '410883', '孟州市', 3, 0, 1, '孟州市', '孟州', '孟州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410902', '华龙区', 3, 0, 1, '华龙区', '华龙', '华龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410922', '清丰县', 3, 0, 1, '清丰县', '清丰', '清丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410923', '南乐县', 3, 0, 1, '南乐县', '南乐', '南乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410926', '范县', 3, 0, 1, '范县', '范', '范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410927', '台前县', 3, 0, 1, '台前县', '台前', '台前', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410928', '濮阳县', 3, 0, 1, '濮阳县', '濮阳', '濮阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410971', '河南濮阳工业园区', 3, 0, 1, '河南濮阳工业园区', '河南濮阳工业园', '河南濮阳工业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@濮阳_4109_id, '410972', '濮阳经济技术开发区', 3, 0, 1, '濮阳经济技术开发区', '濮阳经济技术开发', '濮阳经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411002', '魏都区', 3, 0, 1, '魏都区', '魏都', '魏都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411003', '建安区', 3, 0, 1, '建安区', '建安', '建安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411024', '鄢陵县', 3, 0, 1, '鄢陵县', '鄢陵', '鄢陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411025', '襄城县', 3, 0, 1, '襄城县', '襄城', '襄城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411071', '许昌经济技术开发区', 3, 0, 1, '许昌经济技术开发区', '许昌经济技术开发', '许昌经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411081', '禹州市', 3, 0, 1, '禹州市', '禹州', '禹州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@许昌_4110_id, '411082', '长葛市', 3, 0, 1, '长葛市', '长葛', '长葛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411102', '源汇区', 3, 0, 1, '源汇区', '源汇', '源汇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411103', '郾城区', 3, 0, 1, '郾城区', '郾城', '郾城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411104', '召陵区', 3, 0, 1, '召陵区', '召陵', '召陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411121', '舞阳县', 3, 0, 1, '舞阳县', '舞阳', '舞阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411122', '临颍县', 3, 0, 1, '临颍县', '临颍', '临颍', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@漯河_4111_id, '411171', '漯河经济技术开发区', 3, 0, 1, '漯河经济技术开发区', '漯河经济技术开发', '漯河经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411202', '湖滨区', 3, 0, 1, '湖滨区', '湖滨', '湖滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411203', '陕州区', 3, 0, 1, '陕州区', '陕州', '陕州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411221', '渑池县', 3, 0, 1, '渑池县', '渑池', '渑池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411224', '卢氏县', 3, 0, 1, '卢氏县', '卢氏', '卢氏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411271', '河南三门峡经济开发区', 3, 0, 1, '河南三门峡经济开发区', '河南三门峡经济开发', '河南三门峡经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411281', '义马市', 3, 0, 1, '义马市', '义马', '义马', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三门峡_4112_id, '411282', '灵宝市', 3, 0, 1, '灵宝市', '灵宝', '灵宝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411302', '宛城区', 3, 0, 1, '宛城区', '宛城', '宛城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411303', '卧龙区', 3, 0, 1, '卧龙区', '卧龙', '卧龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411321', '南召县', 3, 0, 1, '南召县', '南召', '南召', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411322', '方城县', 3, 0, 1, '方城县', '方城', '方城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411323', '西峡县', 3, 0, 1, '西峡县', '西峡', '西峡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411324', '镇平县', 3, 0, 1, '镇平县', '镇平', '镇平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411325', '内乡县', 3, 0, 1, '内乡县', '内乡', '内乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411326', '淅川县', 3, 0, 1, '淅川县', '淅川', '淅川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411327', '社旗县', 3, 0, 1, '社旗县', '社旗', '社旗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411328', '唐河县', 3, 0, 1, '唐河县', '唐河', '唐河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411329', '新野县', 3, 0, 1, '新野县', '新野', '新野', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411330', '桐柏县', 3, 0, 1, '桐柏县', '桐柏', '桐柏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411371', '南阳高新技术产业开发区', 3, 0, 1, '南阳高新技术产业开发区', '南阳高新技术产业开发', '南阳高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411372', '南阳市城乡一体化示范区', 3, 0, 1, '南阳市城乡一体化示范区', '南阳市城乡一体化示范', '南阳市城乡一体化示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南阳_4113_id, '411381', '邓州市', 3, 0, 1, '邓州市', '邓州', '邓州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411402', '梁园区', 3, 0, 1, '梁园区', '梁园', '梁园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411403', '睢阳区', 3, 0, 1, '睢阳区', '睢阳', '睢阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411421', '民权县', 3, 0, 1, '民权县', '民权', '民权', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411422', '睢县', 3, 0, 1, '睢县', '睢', '睢', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411423', '宁陵县', 3, 0, 1, '宁陵县', '宁陵', '宁陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411424', '柘城县', 3, 0, 1, '柘城县', '柘城', '柘城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411425', '虞城县', 3, 0, 1, '虞城县', '虞城', '虞城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411426', '夏邑县', 3, 0, 1, '夏邑县', '夏邑', '夏邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411471', '豫东综合物流产业聚集区', 3, 0, 1, '豫东综合物流产业聚集区', '豫东综合物流产业聚集', '豫东综合物流产业聚集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411472', '河南商丘经济开发区', 3, 0, 1, '河南商丘经济开发区', '河南商丘经济开发', '河南商丘经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商丘_4114_id, '411481', '永城市', 3, 0, 1, '永城市', '永城', '永城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411502', '浉河区', 3, 0, 1, '浉河区', '浉河', '浉河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411503', '平桥区', 3, 0, 1, '平桥区', '平桥', '平桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411521', '罗山县', 3, 0, 1, '罗山县', '罗山', '罗山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411522', '光山县', 3, 0, 1, '光山县', '光山', '光山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411523', '新县', 3, 0, 1, '新县', '新', '新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411524', '商城县', 3, 0, 1, '商城县', '商城', '商城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411525', '固始县', 3, 0, 1, '固始县', '固始', '固始', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411526', '潢川县', 3, 0, 1, '潢川县', '潢川', '潢川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411527', '淮滨县', 3, 0, 1, '淮滨县', '淮滨', '淮滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411528', '息县', 3, 0, 1, '息县', '息', '息', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@信阳_4115_id, '411571', '信阳高新技术产业开发区', 3, 0, 1, '信阳高新技术产业开发区', '信阳高新技术产业开发', '信阳高新技术产业开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411602', '川汇区', 3, 0, 1, '川汇区', '川汇', '川汇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411603', '淮阳区', 3, 0, 1, '淮阳区', '淮阳', '淮阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411621', '扶沟县', 3, 0, 1, '扶沟县', '扶沟', '扶沟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411622', '西华县', 3, 0, 1, '西华县', '西华', '西华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411623', '商水县', 3, 0, 1, '商水县', '商水', '商水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411624', '沈丘县', 3, 0, 1, '沈丘县', '沈丘', '沈丘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411625', '郸城县', 3, 0, 1, '郸城县', '郸城', '郸城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411627', '太康县', 3, 0, 1, '太康县', '太康', '太康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411628', '鹿邑县', 3, 0, 1, '鹿邑县', '鹿邑', '鹿邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411671', '周口临港开发区', 3, 0, 1, '周口临港开发区', '周口临港开发', '周口临港开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@周口_4116_id, '411681', '项城市', 3, 0, 1, '项城市', '项城', '项城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411702', '驿城区', 3, 0, 1, '驿城区', '驿城', '驿城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411721', '西平县', 3, 0, 1, '西平县', '西平', '西平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411722', '上蔡县', 3, 0, 1, '上蔡县', '上蔡', '上蔡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411723', '平舆县', 3, 0, 1, '平舆县', '平舆', '平舆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411724', '正阳县', 3, 0, 1, '正阳县', '正阳', '正阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411725', '确山县', 3, 0, 1, '确山县', '确山', '确山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411726', '泌阳县', 3, 0, 1, '泌阳县', '泌阳', '泌阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411727', '汝南县', 3, 0, 1, '汝南县', '汝南', '汝南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411728', '遂平县', 3, 0, 1, '遂平县', '遂平', '遂平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411729', '新蔡县', 3, 0, 1, '新蔡县', '新蔡', '新蔡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@驻马店_4117_id, '411771', '河南驻马店经济开发区', 3, 0, 1, '河南驻马店经济开发区', '河南驻马店经济开发', '河南驻马店经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4190_id, '419001', '济源市', 3, 0, 1, '济源市', '济源', '济源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420102', '江岸区', 3, 0, 1, '江岸区', '江岸', '江岸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420103', '江汉区', 3, 0, 1, '江汉区', '江汉', '江汉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420104', '硚口区', 3, 0, 1, '硚口区', '硚口', '硚口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420105', '汉阳区', 3, 0, 1, '汉阳区', '汉阳', '汉阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420106', '武昌区', 3, 0, 1, '武昌区', '武昌', '武昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420107', '青山区', 3, 0, 1, '青山区', '青山', '青山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420111', '洪山区', 3, 0, 1, '洪山区', '洪山', '洪山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420112', '东西湖区', 3, 0, 1, '东西湖区', '东西湖', '东西湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420113', '汉南区', 3, 0, 1, '汉南区', '汉南', '汉南', '', NOW(), NOW(), 0);
-- 已插入 1600/3056 条区级数据

-- 批次 17：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420114', '蔡甸区', 3, 0, 1, '蔡甸区', '蔡甸', '蔡甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420115', '江夏区', 3, 0, 1, '江夏区', '江夏', '江夏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420116', '黄陂区', 3, 0, 1, '黄陂区', '黄陂', '黄陂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武汉_4201_id, '420117', '新洲区', 3, 0, 1, '新洲区', '新洲', '新洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420202', '黄石港区', 3, 0, 1, '黄石港区', '黄石港', '黄石港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420203', '西塞山区', 3, 0, 1, '西塞山区', '西塞山', '西塞山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420204', '下陆区', 3, 0, 1, '下陆区', '下陆', '下陆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420205', '铁山区', 3, 0, 1, '铁山区', '铁山', '铁山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420222', '阳新县', 3, 0, 1, '阳新县', '阳新', '阳新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄石_4202_id, '420281', '大冶市', 3, 0, 1, '大冶市', '大冶', '大冶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420302', '茅箭区', 3, 0, 1, '茅箭区', '茅箭', '茅箭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420303', '张湾区', 3, 0, 1, '张湾区', '张湾', '张湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420304', '郧阳区', 3, 0, 1, '郧阳区', '郧阳', '郧阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420322', '郧西县', 3, 0, 1, '郧西县', '郧西', '郧西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420323', '竹山县', 3, 0, 1, '竹山县', '竹山', '竹山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420324', '竹溪县', 3, 0, 1, '竹溪县', '竹溪', '竹溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420325', '房县', 3, 0, 1, '房县', '房', '房', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@十堰_4203_id, '420381', '丹江口市', 3, 0, 1, '丹江口市', '丹江口', '丹江口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420502', '西陵区', 3, 0, 1, '西陵区', '西陵', '西陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420503', '伍家岗区', 3, 0, 1, '伍家岗区', '伍家岗', '伍家岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420504', '点军区', 3, 0, 1, '点军区', '点军', '点军', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420505', '猇亭区', 3, 0, 1, '猇亭区', '猇亭', '猇亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420506', '夷陵区', 3, 0, 1, '夷陵区', '夷陵', '夷陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420525', '远安县', 3, 0, 1, '远安县', '远安', '远安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420526', '兴山县', 3, 0, 1, '兴山县', '兴山', '兴山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420527', '秭归县', 3, 0, 1, '秭归县', '秭归', '秭归', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420528', '长阳土家族自治县', 3, 0, 1, '长阳土家族自治县', '长阳土家族自治', '长阳土家族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420529', '五峰土家族自治县', 3, 0, 1, '五峰土家族自治县', '五峰土家族自治', '五峰土家族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420581', '宜都市', 3, 0, 1, '宜都市', '宜都', '宜都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420582', '当阳市', 3, 0, 1, '当阳市', '当阳', '当阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜昌_4205_id, '420583', '枝江市', 3, 0, 1, '枝江市', '枝江', '枝江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420602', '襄城区', 3, 0, 1, '襄城区', '襄城', '襄城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420606', '樊城区', 3, 0, 1, '樊城区', '樊城', '樊城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420607', '襄州区', 3, 0, 1, '襄州区', '襄州', '襄州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420624', '南漳县', 3, 0, 1, '南漳县', '南漳', '南漳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420625', '谷城县', 3, 0, 1, '谷城县', '谷城', '谷城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420626', '保康县', 3, 0, 1, '保康县', '保康', '保康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420682', '老河口市', 3, 0, 1, '老河口市', '老河口', '老河口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420683', '枣阳市', 3, 0, 1, '枣阳市', '枣阳', '枣阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@襄阳_4206_id, '420684', '宜城市', 3, 0, 1, '宜城市', '宜城', '宜城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂州_4207_id, '420702', '梁子湖区', 3, 0, 1, '梁子湖区', '梁子湖', '梁子湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂州_4207_id, '420703', '华容区', 3, 0, 1, '华容区', '华容', '华容', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@鄂州_4207_id, '420704', '鄂城区', 3, 0, 1, '鄂城区', '鄂城', '鄂城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆门_4208_id, '420802', '东宝区', 3, 0, 1, '东宝区', '东宝', '东宝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆门_4208_id, '420804', '掇刀区', 3, 0, 1, '掇刀区', '掇刀', '掇刀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆门_4208_id, '420822', '沙洋县', 3, 0, 1, '沙洋县', '沙洋', '沙洋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆门_4208_id, '420881', '钟祥市', 3, 0, 1, '钟祥市', '钟祥', '钟祥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆门_4208_id, '420882', '京山市', 3, 0, 1, '京山市', '京山', '京山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420902', '孝南区', 3, 0, 1, '孝南区', '孝南', '孝南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420921', '孝昌县', 3, 0, 1, '孝昌县', '孝昌', '孝昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420922', '大悟县', 3, 0, 1, '大悟县', '大悟', '大悟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420923', '云梦县', 3, 0, 1, '云梦县', '云梦', '云梦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420981', '应城市', 3, 0, 1, '应城市', '应城', '应城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420982', '安陆市', 3, 0, 1, '安陆市', '安陆', '安陆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@孝感_4209_id, '420984', '汉川市', 3, 0, 1, '汉川市', '汉川', '汉川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421002', '沙市区', 3, 0, 1, '沙市区', '沙市', '沙市', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421003', '荆州区', 3, 0, 1, '荆州区', '荆州', '荆州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421022', '公安县', 3, 0, 1, '公安县', '公安', '公安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421024', '江陵县', 3, 0, 1, '江陵县', '江陵', '江陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421071', '荆州经济技术开发区', 3, 0, 1, '荆州经济技术开发区', '荆州经济技术开发', '荆州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421081', '石首市', 3, 0, 1, '石首市', '石首', '石首', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421083', '洪湖市', 3, 0, 1, '洪湖市', '洪湖', '洪湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421087', '松滋市', 3, 0, 1, '松滋市', '松滋', '松滋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@荆州_4210_id, '421088', '监利市', 3, 0, 1, '监利市', '监利', '监利', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421102', '黄州区', 3, 0, 1, '黄州区', '黄州', '黄州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421121', '团风县', 3, 0, 1, '团风县', '团风', '团风', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421122', '红安县', 3, 0, 1, '红安县', '红安', '红安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421123', '罗田县', 3, 0, 1, '罗田县', '罗田', '罗田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421124', '英山县', 3, 0, 1, '英山县', '英山', '英山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421125', '浠水县', 3, 0, 1, '浠水县', '浠水', '浠水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421126', '蕲春县', 3, 0, 1, '蕲春县', '蕲春', '蕲春', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421127', '黄梅县', 3, 0, 1, '黄梅县', '黄梅', '黄梅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421171', '龙感湖管理区', 3, 0, 1, '龙感湖管理区', '龙感湖管理', '龙感湖管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421181', '麻城市', 3, 0, 1, '麻城市', '麻城', '麻城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄冈_4211_id, '421182', '武穴市', 3, 0, 1, '武穴市', '武穴', '武穴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421202', '咸安区', 3, 0, 1, '咸安区', '咸安', '咸安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421221', '嘉鱼县', 3, 0, 1, '嘉鱼县', '嘉鱼', '嘉鱼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421222', '通城县', 3, 0, 1, '通城县', '通城', '通城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421223', '崇阳县', 3, 0, 1, '崇阳县', '崇阳', '崇阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421224', '通山县', 3, 0, 1, '通山县', '通山', '通山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸宁_4212_id, '421281', '赤壁市', 3, 0, 1, '赤壁市', '赤壁', '赤壁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@随州_4213_id, '421303', '曾都区', 3, 0, 1, '曾都区', '曾都', '曾都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@随州_4213_id, '421321', '随县', 3, 0, 1, '随县', '随', '随', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@随州_4213_id, '421381', '广水市', 3, 0, 1, '广水市', '广水', '广水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422801', '恩施市', 3, 0, 1, '恩施市', '恩施', '恩施', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422802', '利川市', 3, 0, 1, '利川市', '利川', '利川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422822', '建始县', 3, 0, 1, '建始县', '建始', '建始', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422823', '巴东县', 3, 0, 1, '巴东县', '巴东', '巴东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422825', '宣恩县', 3, 0, 1, '宣恩县', '宣恩', '宣恩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422826', '咸丰县', 3, 0, 1, '咸丰县', '咸丰', '咸丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422827', '来凤县', 3, 0, 1, '来凤县', '来凤', '来凤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@恩施土家族苗族_4228_id, '422828', '鹤峰县', 3, 0, 1, '鹤峰县', '鹤峰', '鹤峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4290_id, '429004', '仙桃市', 3, 0, 1, '仙桃市', '仙桃', '仙桃', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4290_id, '429005', '潜江市', 3, 0, 1, '潜江市', '潜江', '潜江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4290_id, '429006', '天门市', 3, 0, 1, '天门市', '天门', '天门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4290_id, '429021', '神农架林区', 3, 0, 1, '神农架林区', '神农架林', '神农架林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430102', '芙蓉区', 3, 0, 1, '芙蓉区', '芙蓉', '芙蓉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430103', '天心区', 3, 0, 1, '天心区', '天心', '天心', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430104', '岳麓区', 3, 0, 1, '岳麓区', '岳麓', '岳麓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430105', '开福区', 3, 0, 1, '开福区', '开福', '开福', '', NOW(), NOW(), 0);
-- 已插入 1700/3056 条区级数据

-- 批次 18：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430111', '雨花区', 3, 0, 1, '雨花区', '雨花', '雨花', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430112', '望城区', 3, 0, 1, '望城区', '望城', '望城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430121', '长沙县', 3, 0, 1, '长沙县', '长沙', '长沙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430181', '浏阳市', 3, 0, 1, '浏阳市', '浏阳', '浏阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@长沙_4301_id, '430182', '宁乡市', 3, 0, 1, '宁乡市', '宁乡', '宁乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430202', '荷塘区', 3, 0, 1, '荷塘区', '荷塘', '荷塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430203', '芦淞区', 3, 0, 1, '芦淞区', '芦淞', '芦淞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430204', '石峰区', 3, 0, 1, '石峰区', '石峰', '石峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430211', '天元区', 3, 0, 1, '天元区', '天元', '天元', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430212', '渌口区', 3, 0, 1, '渌口区', '渌口', '渌口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430223', '攸县', 3, 0, 1, '攸县', '攸', '攸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430224', '茶陵县', 3, 0, 1, '茶陵县', '茶陵', '茶陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430225', '炎陵县', 3, 0, 1, '炎陵县', '炎陵', '炎陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@株洲_4302_id, '430281', '醴陵市', 3, 0, 1, '醴陵市', '醴陵', '醴陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430302', '雨湖区', 3, 0, 1, '雨湖区', '雨湖', '雨湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430304', '岳塘区', 3, 0, 1, '岳塘区', '岳塘', '岳塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430321', '湘潭县', 3, 0, 1, '湘潭县', '湘潭', '湘潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430371', '湖南湘潭高新技术产业园区', 3, 0, 1, '湖南湘潭高新技术产业园区', '湖南湘潭高新技术产业园', '湖南湘潭高新技术产业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430372', '湘潭昭山示范区', 3, 0, 1, '湘潭昭山示范区', '湘潭昭山示范', '湘潭昭山示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430373', '湘潭九华示范区', 3, 0, 1, '湘潭九华示范区', '湘潭九华示范', '湘潭九华示范', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430381', '湘乡市', 3, 0, 1, '湘乡市', '湘乡', '湘乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘潭_4303_id, '430382', '韶山市', 3, 0, 1, '韶山市', '韶山', '韶山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430405', '珠晖区', 3, 0, 1, '珠晖区', '珠晖', '珠晖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430406', '雁峰区', 3, 0, 1, '雁峰区', '雁峰', '雁峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430407', '石鼓区', 3, 0, 1, '石鼓区', '石鼓', '石鼓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430408', '蒸湘区', 3, 0, 1, '蒸湘区', '蒸湘', '蒸湘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430412', '南岳区', 3, 0, 1, '南岳区', '南岳', '南岳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430421', '衡阳县', 3, 0, 1, '衡阳县', '衡阳', '衡阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430422', '衡南县', 3, 0, 1, '衡南县', '衡南', '衡南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430423', '衡山县', 3, 0, 1, '衡山县', '衡山', '衡山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430424', '衡东县', 3, 0, 1, '衡东县', '衡东', '衡东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430426', '祁东县', 3, 0, 1, '祁东县', '祁东', '祁东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430473', '湖南衡阳松木经济开发区', 3, 0, 1, '湖南衡阳松木经济开发区', '湖南衡阳松木经济开发', '湖南衡阳松木经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430476', '湖南衡阳高新技术产业园区', 3, 0, 1, '湖南衡阳高新技术产业园区', '湖南衡阳高新技术产业园', '湖南衡阳高新技术产业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430481', '耒阳市', 3, 0, 1, '耒阳市', '耒阳', '耒阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@衡阳_4304_id, '430482', '常宁市', 3, 0, 1, '常宁市', '常宁', '常宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430502', '双清区', 3, 0, 1, '双清区', '双清', '双清', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430503', '大祥区', 3, 0, 1, '大祥区', '大祥', '大祥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430511', '北塔区', 3, 0, 1, '北塔区', '北塔', '北塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430522', '新邵县', 3, 0, 1, '新邵县', '新邵', '新邵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430523', '邵阳县', 3, 0, 1, '邵阳县', '邵阳', '邵阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430524', '隆回县', 3, 0, 1, '隆回县', '隆回', '隆回', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430525', '洞口县', 3, 0, 1, '洞口县', '洞口', '洞口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430527', '绥宁县', 3, 0, 1, '绥宁县', '绥宁', '绥宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430528', '新宁县', 3, 0, 1, '新宁县', '新宁', '新宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430529', '城步苗族自治县', 3, 0, 1, '城步苗族自治县', '城步苗族自治', '城步苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430581', '武冈市', 3, 0, 1, '武冈市', '武冈', '武冈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@邵阳_4305_id, '430582', '邵东市', 3, 0, 1, '邵东市', '邵东', '邵东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430602', '岳阳楼区', 3, 0, 1, '岳阳楼区', '岳阳楼', '岳阳楼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430603', '云溪区', 3, 0, 1, '云溪区', '云溪', '云溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430611', '君山区', 3, 0, 1, '君山区', '君山', '君山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430621', '岳阳县', 3, 0, 1, '岳阳县', '岳阳', '岳阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430623', '华容县', 3, 0, 1, '华容县', '华容', '华容', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430624', '湘阴县', 3, 0, 1, '湘阴县', '湘阴', '湘阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430626', '平江县', 3, 0, 1, '平江县', '平江', '平江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430671', '岳阳市屈原管理区', 3, 0, 1, '岳阳市屈原管理区', '岳阳市屈原管理', '岳阳市屈原管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430681', '汨罗市', 3, 0, 1, '汨罗市', '汨罗', '汨罗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@岳阳_4306_id, '430682', '临湘市', 3, 0, 1, '临湘市', '临湘', '临湘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430702', '武陵区', 3, 0, 1, '武陵区', '武陵', '武陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430703', '鼎城区', 3, 0, 1, '鼎城区', '鼎城', '鼎城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430721', '安乡县', 3, 0, 1, '安乡县', '安乡', '安乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430722', '汉寿县', 3, 0, 1, '汉寿县', '汉寿', '汉寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430723', '澧县', 3, 0, 1, '澧县', '澧', '澧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430724', '临澧县', 3, 0, 1, '临澧县', '临澧', '临澧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430725', '桃源县', 3, 0, 1, '桃源县', '桃源', '桃源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430726', '石门县', 3, 0, 1, '石门县', '石门', '石门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430771', '常德市西洞庭管理区', 3, 0, 1, '常德市西洞庭管理区', '常德市西洞庭管理', '常德市西洞庭管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@常德_4307_id, '430781', '津市市', 3, 0, 1, '津市市', '津市', '津市', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家界_4308_id, '430802', '永定区', 3, 0, 1, '永定区', '永定', '永定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家界_4308_id, '430811', '武陵源区', 3, 0, 1, '武陵源区', '武陵源', '武陵源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家界_4308_id, '430821', '慈利县', 3, 0, 1, '慈利县', '慈利', '慈利', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张家界_4308_id, '430822', '桑植县', 3, 0, 1, '桑植县', '桑植', '桑植', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430902', '资阳区', 3, 0, 1, '资阳区', '资阳', '资阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430903', '赫山区', 3, 0, 1, '赫山区', '赫山', '赫山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430921', '南县', 3, 0, 1, '南县', '南', '南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430922', '桃江县', 3, 0, 1, '桃江县', '桃江', '桃江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430923', '安化县', 3, 0, 1, '安化县', '安化', '安化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430971', '益阳市大通湖管理区', 3, 0, 1, '益阳市大通湖管理区', '益阳市大通湖管理', '益阳市大通湖管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430972', '湖南益阳高新技术产业园区', 3, 0, 1, '湖南益阳高新技术产业园区', '湖南益阳高新技术产业园', '湖南益阳高新技术产业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@益阳_4309_id, '430981', '沅江市', 3, 0, 1, '沅江市', '沅江', '沅江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431002', '北湖区', 3, 0, 1, '北湖区', '北湖', '北湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431003', '苏仙区', 3, 0, 1, '苏仙区', '苏仙', '苏仙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431021', '桂阳县', 3, 0, 1, '桂阳县', '桂阳', '桂阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431022', '宜章县', 3, 0, 1, '宜章县', '宜章', '宜章', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431023', '永兴县', 3, 0, 1, '永兴县', '永兴', '永兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431024', '嘉禾县', 3, 0, 1, '嘉禾县', '嘉禾', '嘉禾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431025', '临武县', 3, 0, 1, '临武县', '临武', '临武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431026', '汝城县', 3, 0, 1, '汝城县', '汝城', '汝城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431027', '桂东县', 3, 0, 1, '桂东县', '桂东', '桂东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431028', '安仁县', 3, 0, 1, '安仁县', '安仁', '安仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@郴州_4310_id, '431081', '资兴市', 3, 0, 1, '资兴市', '资兴', '资兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431102', '零陵区', 3, 0, 1, '零陵区', '零陵', '零陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431103', '冷水滩区', 3, 0, 1, '冷水滩区', '冷水滩', '冷水滩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431122', '东安县', 3, 0, 1, '东安县', '东安', '东安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431123', '双牌县', 3, 0, 1, '双牌县', '双牌', '双牌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431124', '道县', 3, 0, 1, '道县', '道', '道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431125', '江永县', 3, 0, 1, '江永县', '江永', '江永', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431126', '宁远县', 3, 0, 1, '宁远县', '宁远', '宁远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431127', '蓝山县', 3, 0, 1, '蓝山县', '蓝山', '蓝山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431128', '新田县', 3, 0, 1, '新田县', '新田', '新田', '', NOW(), NOW(), 0);
-- 已插入 1800/3056 条区级数据

-- 批次 19：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431129', '江华瑶族自治县', 3, 0, 1, '江华瑶族自治县', '江华瑶族自治', '江华瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431171', '永州经济技术开发区', 3, 0, 1, '永州经济技术开发区', '永州经济技术开发', '永州经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431173', '永州市回龙圩管理区', 3, 0, 1, '永州市回龙圩管理区', '永州市回龙圩管理', '永州市回龙圩管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@永州_4311_id, '431181', '祁阳市', 3, 0, 1, '祁阳市', '祁阳', '祁阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431202', '鹤城区', 3, 0, 1, '鹤城区', '鹤城', '鹤城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431221', '中方县', 3, 0, 1, '中方县', '中方', '中方', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431222', '沅陵县', 3, 0, 1, '沅陵县', '沅陵', '沅陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431223', '辰溪县', 3, 0, 1, '辰溪县', '辰溪', '辰溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431224', '溆浦县', 3, 0, 1, '溆浦县', '溆浦', '溆浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431225', '会同县', 3, 0, 1, '会同县', '会同', '会同', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431226', '麻阳苗族自治县', 3, 0, 1, '麻阳苗族自治县', '麻阳苗族自治', '麻阳苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431227', '新晃侗族自治县', 3, 0, 1, '新晃侗族自治县', '新晃侗族自治', '新晃侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431228', '芷江侗族自治县', 3, 0, 1, '芷江侗族自治县', '芷江侗族自治', '芷江侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431229', '靖州苗族侗族自治县', 3, 0, 1, '靖州苗族侗族自治县', '靖州苗族侗族自治', '靖州苗族侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431230', '通道侗族自治县', 3, 0, 1, '通道侗族自治县', '通道侗族自治', '通道侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431271', '怀化市洪江管理区', 3, 0, 1, '怀化市洪江管理区', '怀化市洪江管理', '怀化市洪江管理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怀化_4312_id, '431281', '洪江市', 3, 0, 1, '洪江市', '洪江', '洪江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@娄底_4313_id, '431302', '娄星区', 3, 0, 1, '娄星区', '娄星', '娄星', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@娄底_4313_id, '431321', '双峰县', 3, 0, 1, '双峰县', '双峰', '双峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@娄底_4313_id, '431322', '新化县', 3, 0, 1, '新化县', '新化', '新化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@娄底_4313_id, '431381', '冷水江市', 3, 0, 1, '冷水江市', '冷水江', '冷水江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@娄底_4313_id, '431382', '涟源市', 3, 0, 1, '涟源市', '涟源', '涟源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433101', '吉首市', 3, 0, 1, '吉首市', '吉首', '吉首', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433122', '泸溪县', 3, 0, 1, '泸溪县', '泸溪', '泸溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433123', '凤凰县', 3, 0, 1, '凤凰县', '凤凰', '凤凰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433124', '花垣县', 3, 0, 1, '花垣县', '花垣', '花垣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433125', '保靖县', 3, 0, 1, '保靖县', '保靖', '保靖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433126', '古丈县', 3, 0, 1, '古丈县', '古丈', '古丈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433127', '永顺县', 3, 0, 1, '永顺县', '永顺', '永顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湘西土家族苗族_4331_id, '433130', '龙山县', 3, 0, 1, '龙山县', '龙山', '龙山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440103', '荔湾区', 3, 0, 1, '荔湾区', '荔湾', '荔湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440104', '越秀区', 3, 0, 1, '越秀区', '越秀', '越秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440105', '海珠区', 3, 0, 1, '海珠区', '海珠', '海珠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440106', '天河区', 3, 0, 1, '天河区', '天河', '天河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440111', '白云区', 3, 0, 1, '白云区', '白云', '白云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440112', '黄埔区', 3, 0, 1, '黄埔区', '黄埔', '黄埔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440113', '番禺区', 3, 0, 1, '番禺区', '番禺', '番禺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440114', '花都区', 3, 0, 1, '花都区', '花都', '花都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440115', '南沙区', 3, 0, 1, '南沙区', '南沙', '南沙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440117', '从化区', 3, 0, 1, '从化区', '从化', '从化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广州_4401_id, '440118', '增城区', 3, 0, 1, '增城区', '增城', '增城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440203', '武江区', 3, 0, 1, '武江区', '武江', '武江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440204', '浈江区', 3, 0, 1, '浈江区', '浈江', '浈江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440205', '曲江区', 3, 0, 1, '曲江区', '曲江', '曲江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440222', '始兴县', 3, 0, 1, '始兴县', '始兴', '始兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440224', '仁化县', 3, 0, 1, '仁化县', '仁化', '仁化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440229', '翁源县', 3, 0, 1, '翁源县', '翁源', '翁源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440232', '乳源瑶族自治县', 3, 0, 1, '乳源瑶族自治县', '乳源瑶族自治', '乳源瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440233', '新丰县', 3, 0, 1, '新丰县', '新丰', '新丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440281', '乐昌市', 3, 0, 1, '乐昌市', '乐昌', '乐昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@韶关_4402_id, '440282', '南雄市', 3, 0, 1, '南雄市', '南雄', '南雄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440303', '罗湖区', 3, 0, 1, '罗湖区', '罗湖', '罗湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440304', '福田区', 3, 0, 1, '福田区', '福田', '福田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440305', '南山区', 3, 0, 1, '南山区', '南山', '南山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440306', '宝安区', 3, 0, 1, '宝安区', '宝安', '宝安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440307', '龙岗区', 3, 0, 1, '龙岗区', '龙岗', '龙岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440308', '盐田区', 3, 0, 1, '盐田区', '盐田', '盐田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440309', '龙华区', 3, 0, 1, '龙华区', '龙华', '龙华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440310', '坪山区', 3, 0, 1, '坪山区', '坪山', '坪山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@深圳_4403_id, '440311', '光明区', 3, 0, 1, '光明区', '光明', '光明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@珠海_4404_id, '440402', '香洲区', 3, 0, 1, '香洲区', '香洲', '香洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@珠海_4404_id, '440403', '斗门区', 3, 0, 1, '斗门区', '斗门', '斗门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@珠海_4404_id, '440404', '金湾区', 3, 0, 1, '金湾区', '金湾', '金湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440507', '龙湖区', 3, 0, 1, '龙湖区', '龙湖', '龙湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440511', '金平区', 3, 0, 1, '金平区', '金平', '金平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440512', '濠江区', 3, 0, 1, '濠江区', '濠江', '濠江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440513', '潮阳区', 3, 0, 1, '潮阳区', '潮阳', '潮阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440514', '潮南区', 3, 0, 1, '潮南区', '潮南', '潮南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440515', '澄海区', 3, 0, 1, '澄海区', '澄海', '澄海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕头_4405_id, '440523', '南澳县', 3, 0, 1, '南澳县', '南澳', '南澳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佛山_4406_id, '440604', '禅城区', 3, 0, 1, '禅城区', '禅城', '禅城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佛山_4406_id, '440605', '南海区', 3, 0, 1, '南海区', '南海', '南海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佛山_4406_id, '440606', '顺德区', 3, 0, 1, '顺德区', '顺德', '顺德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佛山_4406_id, '440607', '三水区', 3, 0, 1, '三水区', '三水', '三水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@佛山_4406_id, '440608', '高明区', 3, 0, 1, '高明区', '高明', '高明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440703', '蓬江区', 3, 0, 1, '蓬江区', '蓬江', '蓬江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440704', '江海区', 3, 0, 1, '江海区', '江海', '江海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440705', '新会区', 3, 0, 1, '新会区', '新会', '新会', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440781', '台山市', 3, 0, 1, '台山市', '台山', '台山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440783', '开平市', 3, 0, 1, '开平市', '开平', '开平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440784', '鹤山市', 3, 0, 1, '鹤山市', '鹤山', '鹤山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@江门_4407_id, '440785', '恩平市', 3, 0, 1, '恩平市', '恩平', '恩平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440802', '赤坎区', 3, 0, 1, '赤坎区', '赤坎', '赤坎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440803', '霞山区', 3, 0, 1, '霞山区', '霞山', '霞山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440804', '坡头区', 3, 0, 1, '坡头区', '坡头', '坡头', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440811', '麻章区', 3, 0, 1, '麻章区', '麻章', '麻章', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440823', '遂溪县', 3, 0, 1, '遂溪县', '遂溪', '遂溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440825', '徐闻县', 3, 0, 1, '徐闻县', '徐闻', '徐闻', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440881', '廉江市', 3, 0, 1, '廉江市', '廉江', '廉江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440882', '雷州市', 3, 0, 1, '雷州市', '雷州', '雷州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@湛江_4408_id, '440883', '吴川市', 3, 0, 1, '吴川市', '吴川', '吴川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@茂名_4409_id, '440902', '茂南区', 3, 0, 1, '茂南区', '茂南', '茂南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@茂名_4409_id, '440904', '电白区', 3, 0, 1, '电白区', '电白', '电白', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@茂名_4409_id, '440981', '高州市', 3, 0, 1, '高州市', '高州', '高州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@茂名_4409_id, '440982', '化州市', 3, 0, 1, '化州市', '化州', '化州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@茂名_4409_id, '440983', '信宜市', 3, 0, 1, '信宜市', '信宜', '信宜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441202', '端州区', 3, 0, 1, '端州区', '端州', '端州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441203', '鼎湖区', 3, 0, 1, '鼎湖区', '鼎湖', '鼎湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441204', '高要区', 3, 0, 1, '高要区', '高要', '高要', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441223', '广宁县', 3, 0, 1, '广宁县', '广宁', '广宁', '', NOW(), NOW(), 0);
-- 已插入 1900/3056 条区级数据

-- 批次 20：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441224', '怀集县', 3, 0, 1, '怀集县', '怀集', '怀集', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441225', '封开县', 3, 0, 1, '封开县', '封开', '封开', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441226', '德庆县', 3, 0, 1, '德庆县', '德庆', '德庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@肇庆_4412_id, '441284', '四会市', 3, 0, 1, '四会市', '四会', '四会', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@惠州_4413_id, '441302', '惠城区', 3, 0, 1, '惠城区', '惠城', '惠城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@惠州_4413_id, '441303', '惠阳区', 3, 0, 1, '惠阳区', '惠阳', '惠阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@惠州_4413_id, '441322', '博罗县', 3, 0, 1, '博罗县', '博罗', '博罗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@惠州_4413_id, '441323', '惠东县', 3, 0, 1, '惠东县', '惠东', '惠东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@惠州_4413_id, '441324', '龙门县', 3, 0, 1, '龙门县', '龙门', '龙门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441402', '梅江区', 3, 0, 1, '梅江区', '梅江', '梅江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441403', '梅县区', 3, 0, 1, '梅县区', '梅县', '梅县', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441422', '大埔县', 3, 0, 1, '大埔县', '大埔', '大埔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441423', '丰顺县', 3, 0, 1, '丰顺县', '丰顺', '丰顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441424', '五华县', 3, 0, 1, '五华县', '五华', '五华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441426', '平远县', 3, 0, 1, '平远县', '平远', '平远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441427', '蕉岭县', 3, 0, 1, '蕉岭县', '蕉岭', '蕉岭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梅州_4414_id, '441481', '兴宁市', 3, 0, 1, '兴宁市', '兴宁', '兴宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕尾_4415_id, '441502', '城区', 3, 0, 1, '城区', '城', '城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕尾_4415_id, '441521', '海丰县', 3, 0, 1, '海丰县', '海丰', '海丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕尾_4415_id, '441523', '陆河县', 3, 0, 1, '陆河县', '陆河', '陆河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汕尾_4415_id, '441581', '陆丰市', 3, 0, 1, '陆丰市', '陆丰', '陆丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441602', '源城区', 3, 0, 1, '源城区', '源城', '源城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441621', '紫金县', 3, 0, 1, '紫金县', '紫金', '紫金', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441622', '龙川县', 3, 0, 1, '龙川县', '龙川', '龙川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441623', '连平县', 3, 0, 1, '连平县', '连平', '连平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441624', '和平县', 3, 0, 1, '和平县', '和平', '和平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河源_4416_id, '441625', '东源县', 3, 0, 1, '东源县', '东源', '东源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳江_4417_id, '441702', '江城区', 3, 0, 1, '江城区', '江城', '江城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳江_4417_id, '441704', '阳东区', 3, 0, 1, '阳东区', '阳东', '阳东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳江_4417_id, '441721', '阳西县', 3, 0, 1, '阳西县', '阳西', '阳西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阳江_4417_id, '441781', '阳春市', 3, 0, 1, '阳春市', '阳春', '阳春', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441802', '清城区', 3, 0, 1, '清城区', '清城', '清城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441803', '清新区', 3, 0, 1, '清新区', '清新', '清新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441821', '佛冈县', 3, 0, 1, '佛冈县', '佛冈', '佛冈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441823', '阳山县', 3, 0, 1, '阳山县', '阳山', '阳山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441825', '连山壮族瑶族自治县', 3, 0, 1, '连山壮族瑶族自治县', '连山壮族瑶族自治', '连山壮族瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441826', '连南瑶族自治县', 3, 0, 1, '连南瑶族自治县', '连南瑶族自治', '连南瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441881', '英德市', 3, 0, 1, '英德市', '英德', '英德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@清远_4418_id, '441882', '连州市', 3, 0, 1, '连州市', '连州', '连州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900003', '东城街道', 3, 0, 1, '东城街道', '东城街道', '东城街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900004', '南城街道', 3, 0, 1, '南城街道', '南城街道', '南城街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900005', '万江街道', 3, 0, 1, '万江街道', '万江街道', '万江街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900006', '莞城街道', 3, 0, 1, '莞城街道', '莞城街道', '莞城街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900101', '石碣镇', 3, 0, 1, '石碣镇', '石碣镇', '石碣镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900102', '石龙镇', 3, 0, 1, '石龙镇', '石龙镇', '石龙镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900103', '茶山镇', 3, 0, 1, '茶山镇', '茶山镇', '茶山镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900104', '石排镇', 3, 0, 1, '石排镇', '石排镇', '石排镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900105', '企石镇', 3, 0, 1, '企石镇', '企石镇', '企石镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900106', '横沥镇', 3, 0, 1, '横沥镇', '横沥镇', '横沥镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900107', '桥头镇', 3, 0, 1, '桥头镇', '桥头镇', '桥头镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900108', '谢岗镇', 3, 0, 1, '谢岗镇', '谢岗镇', '谢岗镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900109', '东坑镇', 3, 0, 1, '东坑镇', '东坑镇', '东坑镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900110', '常平镇', 3, 0, 1, '常平镇', '常平镇', '常平镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900111', '寮步镇', 3, 0, 1, '寮步镇', '寮步镇', '寮步镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900112', '樟木头镇', 3, 0, 1, '樟木头镇', '樟木头镇', '樟木头镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900113', '大朗镇', 3, 0, 1, '大朗镇', '大朗镇', '大朗镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900114', '黄江镇', 3, 0, 1, '黄江镇', '黄江镇', '黄江镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900115', '清溪镇', 3, 0, 1, '清溪镇', '清溪镇', '清溪镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900116', '塘厦镇', 3, 0, 1, '塘厦镇', '塘厦镇', '塘厦镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900117', '凤岗镇', 3, 0, 1, '凤岗镇', '凤岗镇', '凤岗镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900118', '大岭山镇', 3, 0, 1, '大岭山镇', '大岭山镇', '大岭山镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900119', '长安镇', 3, 0, 1, '长安镇', '长安镇', '长安镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900121', '虎门镇', 3, 0, 1, '虎门镇', '虎门镇', '虎门镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900122', '厚街镇', 3, 0, 1, '厚街镇', '厚街镇', '厚街镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900123', '沙田镇', 3, 0, 1, '沙田镇', '沙田镇', '沙田镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900124', '道滘镇', 3, 0, 1, '道滘镇', '道滘镇', '道滘镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900125', '洪梅镇', 3, 0, 1, '洪梅镇', '洪梅镇', '洪梅镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900126', '麻涌镇', 3, 0, 1, '麻涌镇', '麻涌镇', '麻涌镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900127', '望牛墩镇', 3, 0, 1, '望牛墩镇', '望牛墩镇', '望牛墩镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900128', '中堂镇', 3, 0, 1, '中堂镇', '中堂镇', '中堂镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900129', '高埗镇', 3, 0, 1, '高埗镇', '高埗镇', '高埗镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900401', '松山湖', 3, 0, 1, '松山湖', '松山湖', '松山湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900402', '东莞港', 3, 0, 1, '东莞港', '东莞港', '东莞港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900403', '东莞生态园', 3, 0, 1, '东莞生态园', '东莞生态园', '东莞生态园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@东莞_4419_id, '441900404', '东莞滨海湾新区', 3, 0, 1, '东莞滨海湾新区', '东莞滨海湾新', '东莞滨海湾新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000001', '石岐街道', 3, 0, 1, '石岐街道', '石岐街道', '石岐街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000002', '东区街道', 3, 0, 1, '东区街道', '东区街道', '东区街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000003', '中山港街道', 3, 0, 1, '中山港街道', '中山港街道', '中山港街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000004', '西区街道', 3, 0, 1, '西区街道', '西区街道', '西区街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000005', '南区街道', 3, 0, 1, '南区街道', '南区街道', '南区街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000006', '五桂山街道', 3, 0, 1, '五桂山街道', '五桂山街道', '五桂山街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000007', '民众街道', 3, 0, 1, '民众街道', '民众街道', '民众街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000008', '南朗街道', 3, 0, 1, '南朗街道', '南朗街道', '南朗街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000101', '黄圃镇', 3, 0, 1, '黄圃镇', '黄圃镇', '黄圃镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000103', '东凤镇', 3, 0, 1, '东凤镇', '东凤镇', '东凤镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000105', '古镇镇', 3, 0, 1, '古镇镇', '古镇镇', '古镇镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000106', '沙溪镇', 3, 0, 1, '沙溪镇', '沙溪镇', '沙溪镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000107', '坦洲镇', 3, 0, 1, '坦洲镇', '坦洲镇', '坦洲镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000108', '港口镇', 3, 0, 1, '港口镇', '港口镇', '港口镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000109', '三角镇', 3, 0, 1, '三角镇', '三角镇', '三角镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000110', '横栏镇', 3, 0, 1, '横栏镇', '横栏镇', '横栏镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000111', '南头镇', 3, 0, 1, '南头镇', '南头镇', '南头镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000112', '阜沙镇', 3, 0, 1, '阜沙镇', '阜沙镇', '阜沙镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000114', '三乡镇', 3, 0, 1, '三乡镇', '三乡镇', '三乡镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000115', '板芙镇', 3, 0, 1, '板芙镇', '板芙镇', '板芙镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000116', '大涌镇', 3, 0, 1, '大涌镇', '大涌镇', '大涌镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000117', '神湾镇', 3, 0, 1, '神湾镇', '神湾镇', '神湾镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中山_4420_id, '442000118', '小榄镇', 3, 0, 1, '小榄镇', '小榄镇', '小榄镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潮州_4451_id, '445102', '湘桥区', 3, 0, 1, '湘桥区', '湘桥', '湘桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潮州_4451_id, '445103', '潮安区', 3, 0, 1, '潮安区', '潮安', '潮安', '', NOW(), NOW(), 0);
-- 已插入 2000/3056 条区级数据

-- 批次 21：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@潮州_4451_id, '445122', '饶平县', 3, 0, 1, '饶平县', '饶平', '饶平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@揭阳_4452_id, '445202', '榕城区', 3, 0, 1, '榕城区', '榕城', '榕城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@揭阳_4452_id, '445203', '揭东区', 3, 0, 1, '揭东区', '揭东', '揭东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@揭阳_4452_id, '445222', '揭西县', 3, 0, 1, '揭西县', '揭西', '揭西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@揭阳_4452_id, '445224', '惠来县', 3, 0, 1, '惠来县', '惠来', '惠来', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@揭阳_4452_id, '445281', '普宁市', 3, 0, 1, '普宁市', '普宁', '普宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云浮_4453_id, '445302', '云城区', 3, 0, 1, '云城区', '云城', '云城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云浮_4453_id, '445303', '云安区', 3, 0, 1, '云安区', '云安', '云安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云浮_4453_id, '445321', '新兴县', 3, 0, 1, '新兴县', '新兴', '新兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云浮_4453_id, '445322', '郁南县', 3, 0, 1, '郁南县', '郁南', '郁南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@云浮_4453_id, '445381', '罗定市', 3, 0, 1, '罗定市', '罗定', '罗定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450102', '兴宁区', 3, 0, 1, '兴宁区', '兴宁', '兴宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450103', '青秀区', 3, 0, 1, '青秀区', '青秀', '青秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450105', '江南区', 3, 0, 1, '江南区', '江南', '江南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450107', '西乡塘区', 3, 0, 1, '西乡塘区', '西乡塘', '西乡塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450108', '良庆区', 3, 0, 1, '良庆区', '良庆', '良庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450109', '邕宁区', 3, 0, 1, '邕宁区', '邕宁', '邕宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450110', '武鸣区', 3, 0, 1, '武鸣区', '武鸣', '武鸣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450123', '隆安县', 3, 0, 1, '隆安县', '隆安', '隆安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450124', '马山县', 3, 0, 1, '马山县', '马山', '马山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450125', '上林县', 3, 0, 1, '上林县', '上林', '上林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450126', '宾阳县', 3, 0, 1, '宾阳县', '宾阳', '宾阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南宁_4501_id, '450181', '横州市', 3, 0, 1, '横州市', '横州', '横州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450202', '城中区', 3, 0, 1, '城中区', '城中', '城中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450203', '鱼峰区', 3, 0, 1, '鱼峰区', '鱼峰', '鱼峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450204', '柳南区', 3, 0, 1, '柳南区', '柳南', '柳南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450205', '柳北区', 3, 0, 1, '柳北区', '柳北', '柳北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450206', '柳江区', 3, 0, 1, '柳江区', '柳江', '柳江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450222', '柳城县', 3, 0, 1, '柳城县', '柳城', '柳城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450223', '鹿寨县', 3, 0, 1, '鹿寨县', '鹿寨', '鹿寨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450224', '融安县', 3, 0, 1, '融安县', '融安', '融安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450225', '融水苗族自治县', 3, 0, 1, '融水苗族自治县', '融水苗族自治', '融水苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@柳州_4502_id, '450226', '三江侗族自治县', 3, 0, 1, '三江侗族自治县', '三江侗族自治', '三江侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450302', '秀峰区', 3, 0, 1, '秀峰区', '秀峰', '秀峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450303', '叠彩区', 3, 0, 1, '叠彩区', '叠彩', '叠彩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450304', '象山区', 3, 0, 1, '象山区', '象山', '象山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450305', '七星区', 3, 0, 1, '七星区', '七星', '七星', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450311', '雁山区', 3, 0, 1, '雁山区', '雁山', '雁山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450312', '临桂区', 3, 0, 1, '临桂区', '临桂', '临桂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450321', '阳朔县', 3, 0, 1, '阳朔县', '阳朔', '阳朔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450323', '灵川县', 3, 0, 1, '灵川县', '灵川', '灵川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450324', '全州县', 3, 0, 1, '全州县', '全州', '全州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450325', '兴安县', 3, 0, 1, '兴安县', '兴安', '兴安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450326', '永福县', 3, 0, 1, '永福县', '永福', '永福', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450327', '灌阳县', 3, 0, 1, '灌阳县', '灌阳', '灌阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450328', '龙胜各族自治县', 3, 0, 1, '龙胜各族自治县', '龙胜各族自治', '龙胜各族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450329', '资源县', 3, 0, 1, '资源县', '资源', '资源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450330', '平乐县', 3, 0, 1, '平乐县', '平乐', '平乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450332', '恭城瑶族自治县', 3, 0, 1, '恭城瑶族自治县', '恭城瑶族自治', '恭城瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@桂林_4503_id, '450381', '荔浦市', 3, 0, 1, '荔浦市', '荔浦', '荔浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450403', '万秀区', 3, 0, 1, '万秀区', '万秀', '万秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450405', '长洲区', 3, 0, 1, '长洲区', '长洲', '长洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450406', '龙圩区', 3, 0, 1, '龙圩区', '龙圩', '龙圩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450421', '苍梧县', 3, 0, 1, '苍梧县', '苍梧', '苍梧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450422', '藤县', 3, 0, 1, '藤县', '藤', '藤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450423', '蒙山县', 3, 0, 1, '蒙山县', '蒙山', '蒙山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@梧州_4504_id, '450481', '岑溪市', 3, 0, 1, '岑溪市', '岑溪', '岑溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北海_4505_id, '450502', '海城区', 3, 0, 1, '海城区', '海城', '海城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北海_4505_id, '450503', '银海区', 3, 0, 1, '银海区', '银海', '银海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北海_4505_id, '450512', '铁山港区', 3, 0, 1, '铁山港区', '铁山港', '铁山港', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@北海_4505_id, '450521', '合浦县', 3, 0, 1, '合浦县', '合浦', '合浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@防城港_4506_id, '450602', '港口区', 3, 0, 1, '港口区', '港口', '港口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@防城港_4506_id, '450603', '防城区', 3, 0, 1, '防城区', '防城', '防城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@防城港_4506_id, '450621', '上思县', 3, 0, 1, '上思县', '上思', '上思', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@防城港_4506_id, '450681', '东兴市', 3, 0, 1, '东兴市', '东兴', '东兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@钦州_4507_id, '450702', '钦南区', 3, 0, 1, '钦南区', '钦南', '钦南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@钦州_4507_id, '450703', '钦北区', 3, 0, 1, '钦北区', '钦北', '钦北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@钦州_4507_id, '450721', '灵山县', 3, 0, 1, '灵山县', '灵山', '灵山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@钦州_4507_id, '450722', '浦北县', 3, 0, 1, '浦北县', '浦北', '浦北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵港_4508_id, '450802', '港北区', 3, 0, 1, '港北区', '港北', '港北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵港_4508_id, '450803', '港南区', 3, 0, 1, '港南区', '港南', '港南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵港_4508_id, '450804', '覃塘区', 3, 0, 1, '覃塘区', '覃塘', '覃塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵港_4508_id, '450821', '平南县', 3, 0, 1, '平南县', '平南', '平南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵港_4508_id, '450881', '桂平市', 3, 0, 1, '桂平市', '桂平', '桂平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450902', '玉州区', 3, 0, 1, '玉州区', '玉州', '玉州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450903', '福绵区', 3, 0, 1, '福绵区', '福绵', '福绵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450921', '容县', 3, 0, 1, '容县', '容', '容', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450922', '陆川县', 3, 0, 1, '陆川县', '陆川', '陆川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450923', '博白县', 3, 0, 1, '博白县', '博白', '博白', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450924', '兴业县', 3, 0, 1, '兴业县', '兴业', '兴业', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉林_4509_id, '450981', '北流市', 3, 0, 1, '北流市', '北流', '北流', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451002', '右江区', 3, 0, 1, '右江区', '右江', '右江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451003', '田阳区', 3, 0, 1, '田阳区', '田阳', '田阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451022', '田东县', 3, 0, 1, '田东县', '田东', '田东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451024', '德保县', 3, 0, 1, '德保县', '德保', '德保', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451026', '那坡县', 3, 0, 1, '那坡县', '那坡', '那坡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451027', '凌云县', 3, 0, 1, '凌云县', '凌云', '凌云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451028', '乐业县', 3, 0, 1, '乐业县', '乐业', '乐业', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451029', '田林县', 3, 0, 1, '田林县', '田林', '田林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451030', '西林县', 3, 0, 1, '西林县', '西林', '西林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451031', '隆林各族自治县', 3, 0, 1, '隆林各族自治县', '隆林各族自治', '隆林各族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451081', '靖西市', 3, 0, 1, '靖西市', '靖西', '靖西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@百色_4510_id, '451082', '平果市', 3, 0, 1, '平果市', '平果', '平果', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贺州_4511_id, '451102', '八步区', 3, 0, 1, '八步区', '八步', '八步', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贺州_4511_id, '451103', '平桂区', 3, 0, 1, '平桂区', '平桂', '平桂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贺州_4511_id, '451121', '昭平县', 3, 0, 1, '昭平县', '昭平', '昭平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贺州_4511_id, '451122', '钟山县', 3, 0, 1, '钟山县', '钟山', '钟山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贺州_4511_id, '451123', '富川瑶族自治县', 3, 0, 1, '富川瑶族自治县', '富川瑶族自治', '富川瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451202', '金城江区', 3, 0, 1, '金城江区', '金城江', '金城江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451203', '宜州区', 3, 0, 1, '宜州区', '宜州', '宜州', '', NOW(), NOW(), 0);
-- 已插入 2100/3056 条区级数据

-- 批次 22：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451221', '南丹县', 3, 0, 1, '南丹县', '南丹', '南丹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451222', '天峨县', 3, 0, 1, '天峨县', '天峨', '天峨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451223', '凤山县', 3, 0, 1, '凤山县', '凤山', '凤山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451224', '东兰县', 3, 0, 1, '东兰县', '东兰', '东兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451225', '罗城仫佬族自治县', 3, 0, 1, '罗城仫佬族自治县', '罗城仫佬族自治', '罗城仫佬族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451226', '环江毛南族自治县', 3, 0, 1, '环江毛南族自治县', '环江毛南族自治', '环江毛南族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451227', '巴马瑶族自治县', 3, 0, 1, '巴马瑶族自治县', '巴马瑶族自治', '巴马瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451228', '都安瑶族自治县', 3, 0, 1, '都安瑶族自治县', '都安瑶族自治', '都安瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@河池_4512_id, '451229', '大化瑶族自治县', 3, 0, 1, '大化瑶族自治县', '大化瑶族自治', '大化瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451302', '兴宾区', 3, 0, 1, '兴宾区', '兴宾', '兴宾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451321', '忻城县', 3, 0, 1, '忻城县', '忻城', '忻城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451322', '象州县', 3, 0, 1, '象州县', '象州', '象州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451323', '武宣县', 3, 0, 1, '武宣县', '武宣', '武宣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451324', '金秀瑶族自治县', 3, 0, 1, '金秀瑶族自治县', '金秀瑶族自治', '金秀瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@来宾_4513_id, '451381', '合山市', 3, 0, 1, '合山市', '合山', '合山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451402', '江州区', 3, 0, 1, '江州区', '江州', '江州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451421', '扶绥县', 3, 0, 1, '扶绥县', '扶绥', '扶绥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451422', '宁明县', 3, 0, 1, '宁明县', '宁明', '宁明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451423', '龙州县', 3, 0, 1, '龙州县', '龙州', '龙州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451424', '大新县', 3, 0, 1, '大新县', '大新', '大新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451425', '天等县', 3, 0, 1, '天等县', '天等', '天等', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@崇左_4514_id, '451481', '凭祥市', 3, 0, 1, '凭祥市', '凭祥', '凭祥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海口_4601_id, '460105', '秀英区', 3, 0, 1, '秀英区', '秀英', '秀英', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海口_4601_id, '460106', '龙华区', 3, 0, 1, '龙华区', '龙华', '龙华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海口_4601_id, '460107', '琼山区', 3, 0, 1, '琼山区', '琼山', '琼山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海口_4601_id, '460108', '美兰区', 3, 0, 1, '美兰区', '美兰', '美兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三亚_4602_id, '460202', '海棠区', 3, 0, 1, '海棠区', '海棠', '海棠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三亚_4602_id, '460203', '吉阳区', 3, 0, 1, '吉阳区', '吉阳', '吉阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三亚_4602_id, '460204', '天涯区', 3, 0, 1, '天涯区', '天涯', '天涯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三亚_4602_id, '460205', '崖州区', 3, 0, 1, '崖州区', '崖州', '崖州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三沙_4603_id, '460321', '西沙群岛', 3, 0, 1, '西沙群岛', '西沙群岛', '西沙群岛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三沙_4603_id, '460322', '南沙群岛', 3, 0, 1, '南沙群岛', '南沙群岛', '南沙群岛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@三沙_4603_id, '460323', '中沙群岛的岛礁及其海域', 3, 0, 1, '中沙群岛的岛礁及其海域', '中沙群岛的岛礁及其海域', '中沙群岛的岛礁及其海域', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400100', '那大镇', 3, 0, 1, '那大镇', '那大镇', '那大镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400101', '和庆镇', 3, 0, 1, '和庆镇', '和庆镇', '和庆镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400102', '南丰镇', 3, 0, 1, '南丰镇', '南丰镇', '南丰镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400103', '大成镇', 3, 0, 1, '大成镇', '大成镇', '大成镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400104', '雅星镇', 3, 0, 1, '雅星镇', '雅星镇', '雅星镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400105', '兰洋镇', 3, 0, 1, '兰洋镇', '兰洋镇', '兰洋镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400106', '光村镇', 3, 0, 1, '光村镇', '光村镇', '光村镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400107', '木棠镇', 3, 0, 1, '木棠镇', '木棠镇', '木棠镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400108', '海头镇', 3, 0, 1, '海头镇', '海头镇', '海头镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400109', '峨蔓镇', 3, 0, 1, '峨蔓镇', '峨蔓镇', '峨蔓镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400111', '王五镇', 3, 0, 1, '王五镇', '王五镇', '王五镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400112', '白马井镇', 3, 0, 1, '白马井镇', '白马井镇', '白马井镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400113', '中和镇', 3, 0, 1, '中和镇', '中和镇', '中和镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400114', '排浦镇', 3, 0, 1, '排浦镇', '排浦镇', '排浦镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400115', '东成镇', 3, 0, 1, '东成镇', '东成镇', '东成镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400116', '新州镇', 3, 0, 1, '新州镇', '新州镇', '新州镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400499', '洋浦经济开发区', 3, 0, 1, '洋浦经济开发区', '洋浦经济开发', '洋浦经济开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@儋州_4604_id, '460400500', '华南热作学院', 3, 0, 1, '华南热作学院', '华南热作学院', '华南热作学院', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469001', '五指山市', 3, 0, 1, '五指山市', '五指山', '五指山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469002', '琼海市', 3, 0, 1, '琼海市', '琼海', '琼海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469005', '文昌市', 3, 0, 1, '文昌市', '文昌', '文昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469006', '万宁市', 3, 0, 1, '万宁市', '万宁', '万宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469007', '东方市', 3, 0, 1, '东方市', '东方', '东方', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469021', '定安县', 3, 0, 1, '定安县', '定安', '定安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469022', '屯昌县', 3, 0, 1, '屯昌县', '屯昌', '屯昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469023', '澄迈县', 3, 0, 1, '澄迈县', '澄迈', '澄迈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469024', '临高县', 3, 0, 1, '临高县', '临高', '临高', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469025', '白沙黎族自治县', 3, 0, 1, '白沙黎族自治县', '白沙黎族自治', '白沙黎族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469026', '昌江黎族自治县', 3, 0, 1, '昌江黎族自治县', '昌江黎族自治', '昌江黎族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469027', '乐东黎族自治县', 3, 0, 1, '乐东黎族自治县', '乐东黎族自治', '乐东黎族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469028', '陵水黎族自治县', 3, 0, 1, '陵水黎族自治县', '陵水黎族自治', '陵水黎族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469029', '保亭黎族苗族自治县', 3, 0, 1, '保亭黎族苗族自治县', '保亭黎族苗族自治', '保亭黎族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@省直辖县级行政区划_4690_id, '469030', '琼中黎族苗族自治县', 3, 0, 1, '琼中黎族苗族自治县', '琼中黎族苗族自治', '琼中黎族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500101', '万州区', 3, 0, 1, '万州区', '万州', '万州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500102', '涪陵区', 3, 0, 1, '涪陵区', '涪陵', '涪陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500103', '渝中区', 3, 0, 1, '渝中区', '渝中', '渝中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500104', '大渡口区', 3, 0, 1, '大渡口区', '大渡口', '大渡口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500105', '江北区', 3, 0, 1, '江北区', '江北', '江北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500106', '沙坪坝区', 3, 0, 1, '沙坪坝区', '沙坪坝', '沙坪坝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500107', '九龙坡区', 3, 0, 1, '九龙坡区', '九龙坡', '九龙坡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500108', '南岸区', 3, 0, 1, '南岸区', '南岸', '南岸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500109', '北碚区', 3, 0, 1, '北碚区', '北碚', '北碚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500110', '綦江区', 3, 0, 1, '綦江区', '綦江', '綦江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500111', '大足区', 3, 0, 1, '大足区', '大足', '大足', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500112', '渝北区', 3, 0, 1, '渝北区', '渝北', '渝北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500113', '巴南区', 3, 0, 1, '巴南区', '巴南', '巴南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500114', '黔江区', 3, 0, 1, '黔江区', '黔江', '黔江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500115', '长寿区', 3, 0, 1, '长寿区', '长寿', '长寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500116', '江津区', 3, 0, 1, '江津区', '江津', '江津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500117', '合川区', 3, 0, 1, '合川区', '合川', '合川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500118', '永川区', 3, 0, 1, '永川区', '永川', '永川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500119', '南川区', 3, 0, 1, '南川区', '南川', '南川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500120', '璧山区', 3, 0, 1, '璧山区', '璧山', '璧山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500151', '铜梁区', 3, 0, 1, '铜梁区', '铜梁', '铜梁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500152', '潼南区', 3, 0, 1, '潼南区', '潼南', '潼南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500153', '荣昌区', 3, 0, 1, '荣昌区', '荣昌', '荣昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500154', '开州区', 3, 0, 1, '开州区', '开州', '开州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500155', '梁平区', 3, 0, 1, '梁平区', '梁平', '梁平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500156', '武隆区', 3, 0, 1, '武隆区', '武隆', '武隆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500229', '城口县', 3, 0, 1, '城口县', '城口', '城口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500230', '丰都县', 3, 0, 1, '丰都县', '丰都', '丰都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500231', '垫江县', 3, 0, 1, '垫江县', '垫江', '垫江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500233', '忠县', 3, 0, 1, '忠县', '忠', '忠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500235', '云阳县', 3, 0, 1, '云阳县', '云阳', '云阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500236', '奉节县', 3, 0, 1, '奉节县', '奉节', '奉节', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500237', '巫山县', 3, 0, 1, '巫山县', '巫山', '巫山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500238', '巫溪县', 3, 0, 1, '巫溪县', '巫溪', '巫溪', '', NOW(), NOW(), 0);
-- 已插入 2200/3056 条区级数据

-- 批次 23：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500240', '石柱土家族自治县', 3, 0, 1, '石柱土家族自治县', '石柱土家族自治', '石柱土家族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500241', '秀山土家族苗族自治县', 3, 0, 1, '秀山土家族苗族自治县', '秀山土家族苗族自治', '秀山土家族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500242', '酉阳土家族苗族自治县', 3, 0, 1, '酉阳土家族苗族自治县', '酉阳土家族苗族自治', '酉阳土家族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@重庆_50_id, '500243', '彭水苗族土家族自治县', 3, 0, 1, '彭水苗族土家族自治县', '彭水苗族土家族自治', '彭水苗族土家族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510104', '锦江区', 3, 0, 1, '锦江区', '锦江', '锦江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510105', '青羊区', 3, 0, 1, '青羊区', '青羊', '青羊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510106', '金牛区', 3, 0, 1, '金牛区', '金牛', '金牛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510107', '武侯区', 3, 0, 1, '武侯区', '武侯', '武侯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510108', '成华区', 3, 0, 1, '成华区', '成华', '成华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510112', '龙泉驿区', 3, 0, 1, '龙泉驿区', '龙泉驿', '龙泉驿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510113', '青白江区', 3, 0, 1, '青白江区', '青白江', '青白江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510114', '新都区', 3, 0, 1, '新都区', '新都', '新都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510115', '温江区', 3, 0, 1, '温江区', '温江', '温江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510116', '双流区', 3, 0, 1, '双流区', '双流', '双流', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510117', '郫都区', 3, 0, 1, '郫都区', '郫都', '郫都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510118', '新津区', 3, 0, 1, '新津区', '新津', '新津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510121', '金堂县', 3, 0, 1, '金堂县', '金堂', '金堂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510129', '大邑县', 3, 0, 1, '大邑县', '大邑', '大邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510131', '蒲江县', 3, 0, 1, '蒲江县', '蒲江', '蒲江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510181', '都江堰市', 3, 0, 1, '都江堰市', '都江堰', '都江堰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510182', '彭州市', 3, 0, 1, '彭州市', '彭州', '彭州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510183', '邛崃市', 3, 0, 1, '邛崃市', '邛崃', '邛崃', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510184', '崇州市', 3, 0, 1, '崇州市', '崇州', '崇州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@成都_5101_id, '510185', '简阳市', 3, 0, 1, '简阳市', '简阳', '简阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510302', '自流井区', 3, 0, 1, '自流井区', '自流井', '自流井', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510303', '贡井区', 3, 0, 1, '贡井区', '贡井', '贡井', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510304', '大安区', 3, 0, 1, '大安区', '大安', '大安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510311', '沿滩区', 3, 0, 1, '沿滩区', '沿滩', '沿滩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510321', '荣县', 3, 0, 1, '荣县', '荣', '荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自贡_5103_id, '510322', '富顺县', 3, 0, 1, '富顺县', '富顺', '富顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@攀枝花_5104_id, '510402', '东区', 3, 0, 1, '东区', '东', '东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@攀枝花_5104_id, '510403', '西区', 3, 0, 1, '西区', '西', '西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@攀枝花_5104_id, '510411', '仁和区', 3, 0, 1, '仁和区', '仁和', '仁和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@攀枝花_5104_id, '510421', '米易县', 3, 0, 1, '米易县', '米易', '米易', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@攀枝花_5104_id, '510422', '盐边县', 3, 0, 1, '盐边县', '盐边', '盐边', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510502', '江阳区', 3, 0, 1, '江阳区', '江阳', '江阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510503', '纳溪区', 3, 0, 1, '纳溪区', '纳溪', '纳溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510504', '龙马潭区', 3, 0, 1, '龙马潭区', '龙马潭', '龙马潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510521', '泸县', 3, 0, 1, '泸县', '泸', '泸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510522', '合江县', 3, 0, 1, '合江县', '合江', '合江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510524', '叙永县', 3, 0, 1, '叙永县', '叙永', '叙永', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@泸州_5105_id, '510525', '古蔺县', 3, 0, 1, '古蔺县', '古蔺', '古蔺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510603', '旌阳区', 3, 0, 1, '旌阳区', '旌阳', '旌阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510604', '罗江区', 3, 0, 1, '罗江区', '罗江', '罗江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510623', '中江县', 3, 0, 1, '中江县', '中江', '中江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510681', '广汉市', 3, 0, 1, '广汉市', '广汉', '广汉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510682', '什邡市', 3, 0, 1, '什邡市', '什邡', '什邡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德阳_5106_id, '510683', '绵竹市', 3, 0, 1, '绵竹市', '绵竹', '绵竹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510703', '涪城区', 3, 0, 1, '涪城区', '涪城', '涪城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510704', '游仙区', 3, 0, 1, '游仙区', '游仙', '游仙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510705', '安州区', 3, 0, 1, '安州区', '安州', '安州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510722', '三台县', 3, 0, 1, '三台县', '三台', '三台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510723', '盐亭县', 3, 0, 1, '盐亭县', '盐亭', '盐亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510725', '梓潼县', 3, 0, 1, '梓潼县', '梓潼', '梓潼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510726', '北川羌族自治县', 3, 0, 1, '北川羌族自治县', '北川羌族自治', '北川羌族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510727', '平武县', 3, 0, 1, '平武县', '平武', '平武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@绵阳_5107_id, '510781', '江油市', 3, 0, 1, '江油市', '江油', '江油', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510802', '利州区', 3, 0, 1, '利州区', '利州', '利州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510811', '昭化区', 3, 0, 1, '昭化区', '昭化', '昭化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510812', '朝天区', 3, 0, 1, '朝天区', '朝天', '朝天', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510821', '旺苍县', 3, 0, 1, '旺苍县', '旺苍', '旺苍', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510822', '青川县', 3, 0, 1, '青川县', '青川', '青川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510823', '剑阁县', 3, 0, 1, '剑阁县', '剑阁', '剑阁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广元_5108_id, '510824', '苍溪县', 3, 0, 1, '苍溪县', '苍溪', '苍溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遂宁_5109_id, '510903', '船山区', 3, 0, 1, '船山区', '船山', '船山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遂宁_5109_id, '510904', '安居区', 3, 0, 1, '安居区', '安居', '安居', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遂宁_5109_id, '510921', '蓬溪县', 3, 0, 1, '蓬溪县', '蓬溪', '蓬溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遂宁_5109_id, '510923', '大英县', 3, 0, 1, '大英县', '大英', '大英', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遂宁_5109_id, '510981', '射洪市', 3, 0, 1, '射洪市', '射洪', '射洪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内江_5110_id, '511002', '市中区', 3, 0, 1, '市中区', '市中', '市中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内江_5110_id, '511011', '东兴区', 3, 0, 1, '东兴区', '东兴', '东兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内江_5110_id, '511024', '威远县', 3, 0, 1, '威远县', '威远', '威远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内江_5110_id, '511025', '资中县', 3, 0, 1, '资中县', '资中', '资中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@内江_5110_id, '511083', '隆昌市', 3, 0, 1, '隆昌市', '隆昌', '隆昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511102', '市中区', 3, 0, 1, '市中区', '市中', '市中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511111', '沙湾区', 3, 0, 1, '沙湾区', '沙湾', '沙湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511112', '五通桥区', 3, 0, 1, '五通桥区', '五通桥', '五通桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511113', '金口河区', 3, 0, 1, '金口河区', '金口河', '金口河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511123', '犍为县', 3, 0, 1, '犍为县', '犍为', '犍为', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511124', '井研县', 3, 0, 1, '井研县', '井研', '井研', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511126', '夹江县', 3, 0, 1, '夹江县', '夹江', '夹江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511129', '沐川县', 3, 0, 1, '沐川县', '沐川', '沐川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511132', '峨边彝族自治县', 3, 0, 1, '峨边彝族自治县', '峨边彝族自治', '峨边彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511133', '马边彝族自治县', 3, 0, 1, '马边彝族自治县', '马边彝族自治', '马边彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乐山_5111_id, '511181', '峨眉山市', 3, 0, 1, '峨眉山市', '峨眉山', '峨眉山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511302', '顺庆区', 3, 0, 1, '顺庆区', '顺庆', '顺庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511303', '高坪区', 3, 0, 1, '高坪区', '高坪', '高坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511304', '嘉陵区', 3, 0, 1, '嘉陵区', '嘉陵', '嘉陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511321', '南部县', 3, 0, 1, '南部县', '南部', '南部', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511322', '营山县', 3, 0, 1, '营山县', '营山', '营山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511323', '蓬安县', 3, 0, 1, '蓬安县', '蓬安', '蓬安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511324', '仪陇县', 3, 0, 1, '仪陇县', '仪陇', '仪陇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511325', '西充县', 3, 0, 1, '西充县', '西充', '西充', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@南充_5113_id, '511381', '阆中市', 3, 0, 1, '阆中市', '阆中', '阆中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511402', '东坡区', 3, 0, 1, '东坡区', '东坡', '东坡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511403', '彭山区', 3, 0, 1, '彭山区', '彭山', '彭山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511421', '仁寿县', 3, 0, 1, '仁寿县', '仁寿', '仁寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511423', '洪雅县', 3, 0, 1, '洪雅县', '洪雅', '洪雅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511424', '丹棱县', 3, 0, 1, '丹棱县', '丹棱', '丹棱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@眉山_5114_id, '511425', '青神县', 3, 0, 1, '青神县', '青神', '青神', '', NOW(), NOW(), 0);
-- 已插入 2300/3056 条区级数据

-- 批次 24：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511502', '翠屏区', 3, 0, 1, '翠屏区', '翠屏', '翠屏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511503', '南溪区', 3, 0, 1, '南溪区', '南溪', '南溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511504', '叙州区', 3, 0, 1, '叙州区', '叙州', '叙州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511523', '江安县', 3, 0, 1, '江安县', '江安', '江安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511524', '长宁县', 3, 0, 1, '长宁县', '长宁', '长宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511525', '高县', 3, 0, 1, '高县', '高', '高', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511526', '珙县', 3, 0, 1, '珙县', '珙', '珙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511527', '筠连县', 3, 0, 1, '筠连县', '筠连', '筠连', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511528', '兴文县', 3, 0, 1, '兴文县', '兴文', '兴文', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宜宾_5115_id, '511529', '屏山县', 3, 0, 1, '屏山县', '屏山', '屏山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511602', '广安区', 3, 0, 1, '广安区', '广安', '广安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511603', '前锋区', 3, 0, 1, '前锋区', '前锋', '前锋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511621', '岳池县', 3, 0, 1, '岳池县', '岳池', '岳池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511622', '武胜县', 3, 0, 1, '武胜县', '武胜', '武胜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511623', '邻水县', 3, 0, 1, '邻水县', '邻水', '邻水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@广安_5116_id, '511681', '华蓥市', 3, 0, 1, '华蓥市', '华蓥', '华蓥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511702', '通川区', 3, 0, 1, '通川区', '通川', '通川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511703', '达川区', 3, 0, 1, '达川区', '达川', '达川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511722', '宣汉县', 3, 0, 1, '宣汉县', '宣汉', '宣汉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511723', '开江县', 3, 0, 1, '开江县', '开江', '开江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511724', '大竹县', 3, 0, 1, '大竹县', '大竹', '大竹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511725', '渠县', 3, 0, 1, '渠县', '渠', '渠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@达州_5117_id, '511781', '万源市', 3, 0, 1, '万源市', '万源', '万源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511802', '雨城区', 3, 0, 1, '雨城区', '雨城', '雨城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511803', '名山区', 3, 0, 1, '名山区', '名山', '名山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511822', '荥经县', 3, 0, 1, '荥经县', '荥经', '荥经', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511823', '汉源县', 3, 0, 1, '汉源县', '汉源', '汉源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511824', '石棉县', 3, 0, 1, '石棉县', '石棉', '石棉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511825', '天全县', 3, 0, 1, '天全县', '天全', '天全', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511826', '芦山县', 3, 0, 1, '芦山县', '芦山', '芦山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@雅安_5118_id, '511827', '宝兴县', 3, 0, 1, '宝兴县', '宝兴', '宝兴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴中_5119_id, '511902', '巴州区', 3, 0, 1, '巴州区', '巴州', '巴州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴中_5119_id, '511903', '恩阳区', 3, 0, 1, '恩阳区', '恩阳', '恩阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴中_5119_id, '511921', '通江县', 3, 0, 1, '通江县', '通江', '通江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴中_5119_id, '511922', '南江县', 3, 0, 1, '南江县', '南江', '南江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴中_5119_id, '511923', '平昌县', 3, 0, 1, '平昌县', '平昌', '平昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@资阳_5120_id, '512002', '雁江区', 3, 0, 1, '雁江区', '雁江', '雁江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@资阳_5120_id, '512021', '安岳县', 3, 0, 1, '安岳县', '安岳', '安岳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@资阳_5120_id, '512022', '乐至县', 3, 0, 1, '乐至县', '乐至', '乐至', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513201', '马尔康市', 3, 0, 1, '马尔康市', '马尔康', '马尔康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513221', '汶川县', 3, 0, 1, '汶川县', '汶川', '汶川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513222', '理县', 3, 0, 1, '理县', '理', '理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513223', '茂县', 3, 0, 1, '茂县', '茂', '茂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513224', '松潘县', 3, 0, 1, '松潘县', '松潘', '松潘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513225', '九寨沟县', 3, 0, 1, '九寨沟县', '九寨沟', '九寨沟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513226', '金川县', 3, 0, 1, '金川县', '金川', '金川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513227', '小金县', 3, 0, 1, '小金县', '小金', '小金', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513228', '黑水县', 3, 0, 1, '黑水县', '黑水', '黑水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513230', '壤塘县', 3, 0, 1, '壤塘县', '壤塘', '壤塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513231', '阿坝县', 3, 0, 1, '阿坝县', '阿坝', '阿坝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513232', '若尔盖县', 3, 0, 1, '若尔盖县', '若尔盖', '若尔盖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿坝藏族羌族_5132_id, '513233', '红原县', 3, 0, 1, '红原县', '红原', '红原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513301', '康定市', 3, 0, 1, '康定市', '康定', '康定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513322', '泸定县', 3, 0, 1, '泸定县', '泸定', '泸定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513323', '丹巴县', 3, 0, 1, '丹巴县', '丹巴', '丹巴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513324', '九龙县', 3, 0, 1, '九龙县', '九龙', '九龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513325', '雅江县', 3, 0, 1, '雅江县', '雅江', '雅江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513326', '道孚县', 3, 0, 1, '道孚县', '道孚', '道孚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513327', '炉霍县', 3, 0, 1, '炉霍县', '炉霍', '炉霍', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513328', '甘孜县', 3, 0, 1, '甘孜县', '甘孜', '甘孜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513329', '新龙县', 3, 0, 1, '新龙县', '新龙', '新龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513330', '德格县', 3, 0, 1, '德格县', '德格', '德格', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513331', '白玉县', 3, 0, 1, '白玉县', '白玉', '白玉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513332', '石渠县', 3, 0, 1, '石渠县', '石渠', '石渠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513333', '色达县', 3, 0, 1, '色达县', '色达', '色达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513334', '理塘县', 3, 0, 1, '理塘县', '理塘', '理塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513335', '巴塘县', 3, 0, 1, '巴塘县', '巴塘', '巴塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513336', '乡城县', 3, 0, 1, '乡城县', '乡城', '乡城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513337', '稻城县', 3, 0, 1, '稻城县', '稻城', '稻城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘孜藏族_5133_id, '513338', '得荣县', 3, 0, 1, '得荣县', '得荣', '得荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513401', '西昌市', 3, 0, 1, '西昌市', '西昌', '西昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513402', '会理市', 3, 0, 1, '会理市', '会理', '会理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513422', '木里藏族自治县', 3, 0, 1, '木里藏族自治县', '木里藏族自治', '木里藏族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513423', '盐源县', 3, 0, 1, '盐源县', '盐源', '盐源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513424', '德昌县', 3, 0, 1, '德昌县', '德昌', '德昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513426', '会东县', 3, 0, 1, '会东县', '会东', '会东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513427', '宁南县', 3, 0, 1, '宁南县', '宁南', '宁南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513428', '普格县', 3, 0, 1, '普格县', '普格', '普格', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513429', '布拖县', 3, 0, 1, '布拖县', '布拖', '布拖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513430', '金阳县', 3, 0, 1, '金阳县', '金阳', '金阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513431', '昭觉县', 3, 0, 1, '昭觉县', '昭觉', '昭觉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513432', '喜德县', 3, 0, 1, '喜德县', '喜德', '喜德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513433', '冕宁县', 3, 0, 1, '冕宁县', '冕宁', '冕宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513434', '越西县', 3, 0, 1, '越西县', '越西', '越西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513435', '甘洛县', 3, 0, 1, '甘洛县', '甘洛', '甘洛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513436', '美姑县', 3, 0, 1, '美姑县', '美姑', '美姑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@凉山彝族_5134_id, '513437', '雷波县', 3, 0, 1, '雷波县', '雷波', '雷波', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520102', '南明区', 3, 0, 1, '南明区', '南明', '南明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520103', '云岩区', 3, 0, 1, '云岩区', '云岩', '云岩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520111', '花溪区', 3, 0, 1, '花溪区', '花溪', '花溪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520112', '乌当区', 3, 0, 1, '乌当区', '乌当', '乌当', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520113', '白云区', 3, 0, 1, '白云区', '白云', '白云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520115', '观山湖区', 3, 0, 1, '观山湖区', '观山湖', '观山湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520121', '开阳县', 3, 0, 1, '开阳县', '开阳', '开阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520122', '息烽县', 3, 0, 1, '息烽县', '息烽', '息烽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520123', '修文县', 3, 0, 1, '修文县', '修文', '修文', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@贵阳_5201_id, '520181', '清镇市', 3, 0, 1, '清镇市', '清镇', '清镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六盘水_5202_id, '520201', '钟山区', 3, 0, 1, '钟山区', '钟山', '钟山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六盘水_5202_id, '520203', '六枝特区', 3, 0, 1, '六枝特区', '六枝特', '六枝特', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六盘水_5202_id, '520204', '水城区', 3, 0, 1, '水城区', '水城', '水城', '', NOW(), NOW(), 0);
-- 已插入 2400/3056 条区级数据

-- 批次 25：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@六盘水_5202_id, '520281', '盘州市', 3, 0, 1, '盘州市', '盘州', '盘州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520302', '红花岗区', 3, 0, 1, '红花岗区', '红花岗', '红花岗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520303', '汇川区', 3, 0, 1, '汇川区', '汇川', '汇川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520304', '播州区', 3, 0, 1, '播州区', '播州', '播州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520322', '桐梓县', 3, 0, 1, '桐梓县', '桐梓', '桐梓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520323', '绥阳县', 3, 0, 1, '绥阳县', '绥阳', '绥阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520324', '正安县', 3, 0, 1, '正安县', '正安', '正安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520325', '道真仡佬族苗族自治县', 3, 0, 1, '道真仡佬族苗族自治县', '道真仡佬族苗族自治', '道真仡佬族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520326', '务川仡佬族苗族自治县', 3, 0, 1, '务川仡佬族苗族自治县', '务川仡佬族苗族自治', '务川仡佬族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520327', '凤冈县', 3, 0, 1, '凤冈县', '凤冈', '凤冈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520328', '湄潭县', 3, 0, 1, '湄潭县', '湄潭', '湄潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520329', '余庆县', 3, 0, 1, '余庆县', '余庆', '余庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520330', '习水县', 3, 0, 1, '习水县', '习水', '习水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520381', '赤水市', 3, 0, 1, '赤水市', '赤水', '赤水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@遵义_5203_id, '520382', '仁怀市', 3, 0, 1, '仁怀市', '仁怀', '仁怀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520402', '西秀区', 3, 0, 1, '西秀区', '西秀', '西秀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520403', '平坝区', 3, 0, 1, '平坝区', '平坝', '平坝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520422', '普定县', 3, 0, 1, '普定县', '普定', '普定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520423', '镇宁布依族苗族自治县', 3, 0, 1, '镇宁布依族苗族自治县', '镇宁布依族苗族自治', '镇宁布依族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520424', '关岭布依族苗族自治县', 3, 0, 1, '关岭布依族苗族自治县', '关岭布依族苗族自治', '关岭布依族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安顺_5204_id, '520425', '紫云苗族布依族自治县', 3, 0, 1, '紫云苗族布依族自治县', '紫云苗族布依族自治', '紫云苗族布依族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520502', '七星关区', 3, 0, 1, '七星关区', '七星关', '七星关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520521', '大方县', 3, 0, 1, '大方县', '大方', '大方', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520523', '金沙县', 3, 0, 1, '金沙县', '金沙', '金沙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520524', '织金县', 3, 0, 1, '织金县', '织金', '织金', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520525', '纳雍县', 3, 0, 1, '纳雍县', '纳雍', '纳雍', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520526', '威宁彝族回族苗族自治县', 3, 0, 1, '威宁彝族回族苗族自治县', '威宁彝族回族苗族自治', '威宁彝族回族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520527', '赫章县', 3, 0, 1, '赫章县', '赫章', '赫章', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@毕节_5205_id, '520581', '黔西市', 3, 0, 1, '黔西市', '黔西', '黔西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520602', '碧江区', 3, 0, 1, '碧江区', '碧江', '碧江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520603', '万山区', 3, 0, 1, '万山区', '万山', '万山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520621', '江口县', 3, 0, 1, '江口县', '江口', '江口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520622', '玉屏侗族自治县', 3, 0, 1, '玉屏侗族自治县', '玉屏侗族自治', '玉屏侗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520623', '石阡县', 3, 0, 1, '石阡县', '石阡', '石阡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520624', '思南县', 3, 0, 1, '思南县', '思南', '思南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520625', '印江土家族苗族自治县', 3, 0, 1, '印江土家族苗族自治县', '印江土家族苗族自治', '印江土家族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520626', '德江县', 3, 0, 1, '德江县', '德江', '德江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520627', '沿河土家族自治县', 3, 0, 1, '沿河土家族自治县', '沿河土家族自治', '沿河土家族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜仁_5206_id, '520628', '松桃苗族自治县', 3, 0, 1, '松桃苗族自治县', '松桃苗族自治', '松桃苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522301', '兴义市', 3, 0, 1, '兴义市', '兴义', '兴义', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522302', '兴仁市', 3, 0, 1, '兴仁市', '兴仁', '兴仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522323', '普安县', 3, 0, 1, '普安县', '普安', '普安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522324', '晴隆县', 3, 0, 1, '晴隆县', '晴隆', '晴隆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522325', '贞丰县', 3, 0, 1, '贞丰县', '贞丰', '贞丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522326', '望谟县', 3, 0, 1, '望谟县', '望谟', '望谟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522327', '册亨县', 3, 0, 1, '册亨县', '册亨', '册亨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔西南布依族苗族_5223_id, '522328', '安龙县', 3, 0, 1, '安龙县', '安龙', '安龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522601', '凯里市', 3, 0, 1, '凯里市', '凯里', '凯里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522622', '黄平县', 3, 0, 1, '黄平县', '黄平', '黄平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522623', '施秉县', 3, 0, 1, '施秉县', '施秉', '施秉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522624', '三穗县', 3, 0, 1, '三穗县', '三穗', '三穗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522625', '镇远县', 3, 0, 1, '镇远县', '镇远', '镇远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522626', '岑巩县', 3, 0, 1, '岑巩县', '岑巩', '岑巩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522627', '天柱县', 3, 0, 1, '天柱县', '天柱', '天柱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522628', '锦屏县', 3, 0, 1, '锦屏县', '锦屏', '锦屏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522629', '剑河县', 3, 0, 1, '剑河县', '剑河', '剑河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522630', '台江县', 3, 0, 1, '台江县', '台江', '台江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522631', '黎平县', 3, 0, 1, '黎平县', '黎平', '黎平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522632', '榕江县', 3, 0, 1, '榕江县', '榕江', '榕江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522633', '从江县', 3, 0, 1, '从江县', '从江', '从江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522634', '雷山县', 3, 0, 1, '雷山县', '雷山', '雷山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522635', '麻江县', 3, 0, 1, '麻江县', '麻江', '麻江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔东南苗族侗族_5226_id, '522636', '丹寨县', 3, 0, 1, '丹寨县', '丹寨', '丹寨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522701', '都匀市', 3, 0, 1, '都匀市', '都匀', '都匀', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522702', '福泉市', 3, 0, 1, '福泉市', '福泉', '福泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522722', '荔波县', 3, 0, 1, '荔波县', '荔波', '荔波', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522723', '贵定县', 3, 0, 1, '贵定县', '贵定', '贵定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522725', '瓮安县', 3, 0, 1, '瓮安县', '瓮安', '瓮安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522726', '独山县', 3, 0, 1, '独山县', '独山', '独山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522727', '平塘县', 3, 0, 1, '平塘县', '平塘', '平塘', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522728', '罗甸县', 3, 0, 1, '罗甸县', '罗甸', '罗甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522729', '长顺县', 3, 0, 1, '长顺县', '长顺', '长顺', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522730', '龙里县', 3, 0, 1, '龙里县', '龙里', '龙里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522731', '惠水县', 3, 0, 1, '惠水县', '惠水', '惠水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黔南布依族苗族_5227_id, '522732', '三都水族自治县', 3, 0, 1, '三都水族自治县', '三都水族自治', '三都水族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530102', '五华区', 3, 0, 1, '五华区', '五华', '五华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530103', '盘龙区', 3, 0, 1, '盘龙区', '盘龙', '盘龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530111', '官渡区', 3, 0, 1, '官渡区', '官渡', '官渡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530112', '西山区', 3, 0, 1, '西山区', '西山', '西山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530113', '东川区', 3, 0, 1, '东川区', '东川', '东川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530114', '呈贡区', 3, 0, 1, '呈贡区', '呈贡', '呈贡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530115', '晋宁区', 3, 0, 1, '晋宁区', '晋宁', '晋宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530124', '富民县', 3, 0, 1, '富民县', '富民', '富民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530125', '宜良县', 3, 0, 1, '宜良县', '宜良', '宜良', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530126', '石林彝族自治县', 3, 0, 1, '石林彝族自治县', '石林彝族自治', '石林彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530127', '嵩明县', 3, 0, 1, '嵩明县', '嵩明', '嵩明', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530128', '禄劝彝族苗族自治县', 3, 0, 1, '禄劝彝族苗族自治县', '禄劝彝族苗族自治', '禄劝彝族苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530129', '寻甸回族彝族自治县', 3, 0, 1, '寻甸回族彝族自治县', '寻甸回族彝族自治', '寻甸回族彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昆明_5301_id, '530181', '安宁市', 3, 0, 1, '安宁市', '安宁', '安宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530302', '麒麟区', 3, 0, 1, '麒麟区', '麒麟', '麒麟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530303', '沾益区', 3, 0, 1, '沾益区', '沾益', '沾益', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530304', '马龙区', 3, 0, 1, '马龙区', '马龙', '马龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530322', '陆良县', 3, 0, 1, '陆良县', '陆良', '陆良', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530323', '师宗县', 3, 0, 1, '师宗县', '师宗', '师宗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530324', '罗平县', 3, 0, 1, '罗平县', '罗平', '罗平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530325', '富源县', 3, 0, 1, '富源县', '富源', '富源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530326', '会泽县', 3, 0, 1, '会泽县', '会泽', '会泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@曲靖_5303_id, '530381', '宣威市', 3, 0, 1, '宣威市', '宣威', '宣威', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530402', '红塔区', 3, 0, 1, '红塔区', '红塔', '红塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530403', '江川区', 3, 0, 1, '江川区', '江川', '江川', '', NOW(), NOW(), 0);
-- 已插入 2500/3056 条区级数据

-- 批次 26：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530423', '通海县', 3, 0, 1, '通海县', '通海', '通海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530424', '华宁县', 3, 0, 1, '华宁县', '华宁', '华宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530425', '易门县', 3, 0, 1, '易门县', '易门', '易门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530426', '峨山彝族自治县', 3, 0, 1, '峨山彝族自治县', '峨山彝族自治', '峨山彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530427', '新平彝族傣族自治县', 3, 0, 1, '新平彝族傣族自治县', '新平彝族傣族自治', '新平彝族傣族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530428', '元江哈尼族彝族傣族自治县', 3, 0, 1, '元江哈尼族彝族傣族自治县', '元江哈尼族彝族傣族自治', '元江哈尼族彝族傣族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉溪_5304_id, '530481', '澄江市', 3, 0, 1, '澄江市', '澄江', '澄江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保山_5305_id, '530502', '隆阳区', 3, 0, 1, '隆阳区', '隆阳', '隆阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保山_5305_id, '530521', '施甸县', 3, 0, 1, '施甸县', '施甸', '施甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保山_5305_id, '530523', '龙陵县', 3, 0, 1, '龙陵县', '龙陵', '龙陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保山_5305_id, '530524', '昌宁县', 3, 0, 1, '昌宁县', '昌宁', '昌宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@保山_5305_id, '530581', '腾冲市', 3, 0, 1, '腾冲市', '腾冲', '腾冲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530602', '昭阳区', 3, 0, 1, '昭阳区', '昭阳', '昭阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530621', '鲁甸县', 3, 0, 1, '鲁甸县', '鲁甸', '鲁甸', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530622', '巧家县', 3, 0, 1, '巧家县', '巧家', '巧家', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530623', '盐津县', 3, 0, 1, '盐津县', '盐津', '盐津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530624', '大关县', 3, 0, 1, '大关县', '大关', '大关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530625', '永善县', 3, 0, 1, '永善县', '永善', '永善', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530626', '绥江县', 3, 0, 1, '绥江县', '绥江', '绥江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530627', '镇雄县', 3, 0, 1, '镇雄县', '镇雄', '镇雄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530628', '彝良县', 3, 0, 1, '彝良县', '彝良', '彝良', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530629', '威信县', 3, 0, 1, '威信县', '威信', '威信', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昭通_5306_id, '530681', '水富市', 3, 0, 1, '水富市', '水富', '水富', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽江_5307_id, '530702', '古城区', 3, 0, 1, '古城区', '古城', '古城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽江_5307_id, '530721', '玉龙纳西族自治县', 3, 0, 1, '玉龙纳西族自治县', '玉龙纳西族自治', '玉龙纳西族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽江_5307_id, '530722', '永胜县', 3, 0, 1, '永胜县', '永胜', '永胜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽江_5307_id, '530723', '华坪县', 3, 0, 1, '华坪县', '华坪', '华坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@丽江_5307_id, '530724', '宁蒗彝族自治县', 3, 0, 1, '宁蒗彝族自治县', '宁蒗彝族自治', '宁蒗彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530802', '思茅区', 3, 0, 1, '思茅区', '思茅', '思茅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530821', '宁洱哈尼族彝族自治县', 3, 0, 1, '宁洱哈尼族彝族自治县', '宁洱哈尼族彝族自治', '宁洱哈尼族彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530822', '墨江哈尼族自治县', 3, 0, 1, '墨江哈尼族自治县', '墨江哈尼族自治', '墨江哈尼族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530823', '景东彝族自治县', 3, 0, 1, '景东彝族自治县', '景东彝族自治', '景东彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530824', '景谷傣族彝族自治县', 3, 0, 1, '景谷傣族彝族自治县', '景谷傣族彝族自治', '景谷傣族彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530825', '镇沅彝族哈尼族拉祜族自治县', 3, 0, 1, '镇沅彝族哈尼族拉祜族自治县', '镇沅彝族哈尼族拉祜族自治', '镇沅彝族哈尼族拉祜族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530826', '江城哈尼族彝族自治县', 3, 0, 1, '江城哈尼族彝族自治县', '江城哈尼族彝族自治', '江城哈尼族彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530827', '孟连傣族拉祜族佤族自治县', 3, 0, 1, '孟连傣族拉祜族佤族自治县', '孟连傣族拉祜族佤族自治', '孟连傣族拉祜族佤族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530828', '澜沧拉祜族自治县', 3, 0, 1, '澜沧拉祜族自治县', '澜沧拉祜族自治', '澜沧拉祜族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@普洱_5308_id, '530829', '西盟佤族自治县', 3, 0, 1, '西盟佤族自治县', '西盟佤族自治', '西盟佤族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530902', '临翔区', 3, 0, 1, '临翔区', '临翔', '临翔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530921', '凤庆县', 3, 0, 1, '凤庆县', '凤庆', '凤庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530922', '云县', 3, 0, 1, '云县', '云', '云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530923', '永德县', 3, 0, 1, '永德县', '永德', '永德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530924', '镇康县', 3, 0, 1, '镇康县', '镇康', '镇康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530925', '双江拉祜族佤族布朗族傣族自治县', 3, 0, 1, '双江拉祜族佤族布朗族傣族自治县', '双江拉祜族佤族布朗族傣族自治', '双江拉祜族佤族布朗族傣族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530926', '耿马傣族佤族自治县', 3, 0, 1, '耿马傣族佤族自治县', '耿马傣族佤族自治', '耿马傣族佤族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临沧_5309_id, '530927', '沧源佤族自治县', 3, 0, 1, '沧源佤族自治县', '沧源佤族自治', '沧源佤族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532301', '楚雄市', 3, 0, 1, '楚雄市', '楚雄', '楚雄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532302', '禄丰市', 3, 0, 1, '禄丰市', '禄丰', '禄丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532322', '双柏县', 3, 0, 1, '双柏县', '双柏', '双柏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532323', '牟定县', 3, 0, 1, '牟定县', '牟定', '牟定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532324', '南华县', 3, 0, 1, '南华县', '南华', '南华', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532325', '姚安县', 3, 0, 1, '姚安县', '姚安', '姚安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532326', '大姚县', 3, 0, 1, '大姚县', '大姚', '大姚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532327', '永仁县', 3, 0, 1, '永仁县', '永仁', '永仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532328', '元谋县', 3, 0, 1, '元谋县', '元谋', '元谋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@楚雄彝族_5323_id, '532329', '武定县', 3, 0, 1, '武定县', '武定', '武定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532501', '个旧市', 3, 0, 1, '个旧市', '个旧', '个旧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532502', '开远市', 3, 0, 1, '开远市', '开远', '开远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532503', '蒙自市', 3, 0, 1, '蒙自市', '蒙自', '蒙自', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532504', '弥勒市', 3, 0, 1, '弥勒市', '弥勒', '弥勒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532523', '屏边苗族自治县', 3, 0, 1, '屏边苗族自治县', '屏边苗族自治', '屏边苗族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532524', '建水县', 3, 0, 1, '建水县', '建水', '建水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532525', '石屏县', 3, 0, 1, '石屏县', '石屏', '石屏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532527', '泸西县', 3, 0, 1, '泸西县', '泸西', '泸西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532528', '元阳县', 3, 0, 1, '元阳县', '元阳', '元阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532529', '红河县', 3, 0, 1, '红河县', '红河', '红河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532530', '金平苗族瑶族傣族自治县', 3, 0, 1, '金平苗族瑶族傣族自治县', '金平苗族瑶族傣族自治', '金平苗族瑶族傣族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532531', '绿春县', 3, 0, 1, '绿春县', '绿春', '绿春', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@红河哈尼族彝族_5325_id, '532532', '河口瑶族自治县', 3, 0, 1, '河口瑶族自治县', '河口瑶族自治', '河口瑶族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532601', '文山市', 3, 0, 1, '文山市', '文山', '文山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532622', '砚山县', 3, 0, 1, '砚山县', '砚山', '砚山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532623', '西畴县', 3, 0, 1, '西畴县', '西畴', '西畴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532624', '麻栗坡县', 3, 0, 1, '麻栗坡县', '麻栗坡', '麻栗坡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532625', '马关县', 3, 0, 1, '马关县', '马关', '马关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532626', '丘北县', 3, 0, 1, '丘北县', '丘北', '丘北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532627', '广南县', 3, 0, 1, '广南县', '广南', '广南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@文山壮族苗族_5326_id, '532628', '富宁县', 3, 0, 1, '富宁县', '富宁', '富宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西双版纳傣族_5328_id, '532801', '景洪市', 3, 0, 1, '景洪市', '景洪', '景洪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西双版纳傣族_5328_id, '532822', '勐海县', 3, 0, 1, '勐海县', '勐海', '勐海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西双版纳傣族_5328_id, '532823', '勐腊县', 3, 0, 1, '勐腊县', '勐腊', '勐腊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532901', '大理市', 3, 0, 1, '大理市', '大理', '大理', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532922', '漾濞彝族自治县', 3, 0, 1, '漾濞彝族自治县', '漾濞彝族自治', '漾濞彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532923', '祥云县', 3, 0, 1, '祥云县', '祥云', '祥云', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532924', '宾川县', 3, 0, 1, '宾川县', '宾川', '宾川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532925', '弥渡县', 3, 0, 1, '弥渡县', '弥渡', '弥渡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532926', '南涧彝族自治县', 3, 0, 1, '南涧彝族自治县', '南涧彝族自治', '南涧彝族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532927', '巍山彝族回族自治县', 3, 0, 1, '巍山彝族回族自治县', '巍山彝族回族自治', '巍山彝族回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532928', '永平县', 3, 0, 1, '永平县', '永平', '永平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532929', '云龙县', 3, 0, 1, '云龙县', '云龙', '云龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532930', '洱源县', 3, 0, 1, '洱源县', '洱源', '洱源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532931', '剑川县', 3, 0, 1, '剑川县', '剑川', '剑川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@大理白族_5329_id, '532932', '鹤庆县', 3, 0, 1, '鹤庆县', '鹤庆', '鹤庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德宏傣族景颇族_5331_id, '533102', '瑞丽市', 3, 0, 1, '瑞丽市', '瑞丽', '瑞丽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德宏傣族景颇族_5331_id, '533103', '芒市', 3, 0, 1, '芒市', '芒', '芒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德宏傣族景颇族_5331_id, '533122', '梁河县', 3, 0, 1, '梁河县', '梁河', '梁河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德宏傣族景颇族_5331_id, '533123', '盈江县', 3, 0, 1, '盈江县', '盈江', '盈江', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@德宏傣族景颇族_5331_id, '533124', '陇川县', 3, 0, 1, '陇川县', '陇川', '陇川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怒江傈僳族_5333_id, '533301', '泸水市', 3, 0, 1, '泸水市', '泸水', '泸水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怒江傈僳族_5333_id, '533323', '福贡县', 3, 0, 1, '福贡县', '福贡', '福贡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怒江傈僳族_5333_id, '533324', '贡山独龙族怒族自治县', 3, 0, 1, '贡山独龙族怒族自治县', '贡山独龙族怒族自治', '贡山独龙族怒族自治', '', NOW(), NOW(), 0);
-- 已插入 2600/3056 条区级数据

-- 批次 27：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@怒江傈僳族_5333_id, '533325', '兰坪白族普米族自治县', 3, 0, 1, '兰坪白族普米族自治县', '兰坪白族普米族自治', '兰坪白族普米族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@迪庆藏族_5334_id, '533401', '香格里拉市', 3, 0, 1, '香格里拉市', '香格里拉', '香格里拉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@迪庆藏族_5334_id, '533422', '德钦县', 3, 0, 1, '德钦县', '德钦', '德钦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@迪庆藏族_5334_id, '533423', '维西傈僳族自治县', 3, 0, 1, '维西傈僳族自治县', '维西傈僳族自治', '维西傈僳族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540102', '城关区', 3, 0, 1, '城关区', '城关', '城关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540103', '堆龙德庆区', 3, 0, 1, '堆龙德庆区', '堆龙德庆', '堆龙德庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540104', '达孜区', 3, 0, 1, '达孜区', '达孜', '达孜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540121', '林周县', 3, 0, 1, '林周县', '林周', '林周', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540122', '当雄县', 3, 0, 1, '当雄县', '当雄', '当雄', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540123', '尼木县', 3, 0, 1, '尼木县', '尼木', '尼木', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540124', '曲水县', 3, 0, 1, '曲水县', '曲水', '曲水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540127', '墨竹工卡县', 3, 0, 1, '墨竹工卡县', '墨竹工卡', '墨竹工卡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540171', '格尔木藏青工业园区', 3, 0, 1, '格尔木藏青工业园区', '格尔木藏青工业园', '格尔木藏青工业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540172', '拉萨经济技术开发区', 3, 0, 1, '拉萨经济技术开发区', '拉萨经济技术开发', '拉萨经济技术开发', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540173', '西藏文化旅游创意园区', 3, 0, 1, '西藏文化旅游创意园区', '西藏文化旅游创意园', '西藏文化旅游创意园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@拉萨_5401_id, '540174', '达孜工业园区', 3, 0, 1, '达孜工业园区', '达孜工业园', '达孜工业园', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540202', '桑珠孜区', 3, 0, 1, '桑珠孜区', '桑珠孜', '桑珠孜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540221', '南木林县', 3, 0, 1, '南木林县', '南木林', '南木林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540222', '江孜县', 3, 0, 1, '江孜县', '江孜', '江孜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540223', '定日县', 3, 0, 1, '定日县', '定日', '定日', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540224', '萨迦县', 3, 0, 1, '萨迦县', '萨迦', '萨迦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540225', '拉孜县', 3, 0, 1, '拉孜县', '拉孜', '拉孜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540226', '昂仁县', 3, 0, 1, '昂仁县', '昂仁', '昂仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540227', '谢通门县', 3, 0, 1, '谢通门县', '谢通门', '谢通门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540228', '白朗县', 3, 0, 1, '白朗县', '白朗', '白朗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540229', '仁布县', 3, 0, 1, '仁布县', '仁布', '仁布', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540230', '康马县', 3, 0, 1, '康马县', '康马', '康马', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540231', '定结县', 3, 0, 1, '定结县', '定结', '定结', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540232', '仲巴县', 3, 0, 1, '仲巴县', '仲巴', '仲巴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540233', '亚东县', 3, 0, 1, '亚东县', '亚东', '亚东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540234', '吉隆县', 3, 0, 1, '吉隆县', '吉隆', '吉隆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540235', '聂拉木县', 3, 0, 1, '聂拉木县', '聂拉木', '聂拉木', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540236', '萨嘎县', 3, 0, 1, '萨嘎县', '萨嘎', '萨嘎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@日喀则_5402_id, '540237', '岗巴县', 3, 0, 1, '岗巴县', '岗巴', '岗巴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540302', '卡若区', 3, 0, 1, '卡若区', '卡若', '卡若', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540321', '江达县', 3, 0, 1, '江达县', '江达', '江达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540322', '贡觉县', 3, 0, 1, '贡觉县', '贡觉', '贡觉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540323', '类乌齐县', 3, 0, 1, '类乌齐县', '类乌齐', '类乌齐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540324', '丁青县', 3, 0, 1, '丁青县', '丁青', '丁青', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540325', '察雅县', 3, 0, 1, '察雅县', '察雅', '察雅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540326', '八宿县', 3, 0, 1, '八宿县', '八宿', '八宿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540327', '左贡县', 3, 0, 1, '左贡县', '左贡', '左贡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540328', '芒康县', 3, 0, 1, '芒康县', '芒康', '芒康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540329', '洛隆县', 3, 0, 1, '洛隆县', '洛隆', '洛隆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌都_5403_id, '540330', '边坝县', 3, 0, 1, '边坝县', '边坝', '边坝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540402', '巴宜区', 3, 0, 1, '巴宜区', '巴宜', '巴宜', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540421', '工布江达县', 3, 0, 1, '工布江达县', '工布江达', '工布江达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540423', '墨脱县', 3, 0, 1, '墨脱县', '墨脱', '墨脱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540424', '波密县', 3, 0, 1, '波密县', '波密', '波密', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540425', '察隅县', 3, 0, 1, '察隅县', '察隅', '察隅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540426', '朗县', 3, 0, 1, '朗县', '朗', '朗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@林芝_5404_id, '540481', '米林市', 3, 0, 1, '米林市', '米林', '米林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540502', '乃东区', 3, 0, 1, '乃东区', '乃东', '乃东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540521', '扎囊县', 3, 0, 1, '扎囊县', '扎囊', '扎囊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540522', '贡嘎县', 3, 0, 1, '贡嘎县', '贡嘎', '贡嘎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540523', '桑日县', 3, 0, 1, '桑日县', '桑日', '桑日', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540524', '琼结县', 3, 0, 1, '琼结县', '琼结', '琼结', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540525', '曲松县', 3, 0, 1, '曲松县', '曲松', '曲松', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540526', '措美县', 3, 0, 1, '措美县', '措美', '措美', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540527', '洛扎县', 3, 0, 1, '洛扎县', '洛扎', '洛扎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540528', '加查县', 3, 0, 1, '加查县', '加查', '加查', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540529', '隆子县', 3, 0, 1, '隆子县', '隆子', '隆子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540531', '浪卡子县', 3, 0, 1, '浪卡子县', '浪卡子', '浪卡子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@山南_5405_id, '540581', '错那市', 3, 0, 1, '错那市', '错那', '错那', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540602', '色尼区', 3, 0, 1, '色尼区', '色尼', '色尼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540621', '嘉黎县', 3, 0, 1, '嘉黎县', '嘉黎', '嘉黎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540622', '比如县', 3, 0, 1, '比如县', '比如', '比如', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540623', '聂荣县', 3, 0, 1, '聂荣县', '聂荣', '聂荣', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540624', '安多县', 3, 0, 1, '安多县', '安多', '安多', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540625', '申扎县', 3, 0, 1, '申扎县', '申扎', '申扎', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540626', '索县', 3, 0, 1, '索县', '索', '索', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540627', '班戈县', 3, 0, 1, '班戈县', '班戈', '班戈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540628', '巴青县', 3, 0, 1, '巴青县', '巴青', '巴青', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540629', '尼玛县', 3, 0, 1, '尼玛县', '尼玛', '尼玛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@那曲_5406_id, '540630', '双湖县', 3, 0, 1, '双湖县', '双湖', '双湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542521', '普兰县', 3, 0, 1, '普兰县', '普兰', '普兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542522', '札达县', 3, 0, 1, '札达县', '札达', '札达', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542523', '噶尔县', 3, 0, 1, '噶尔县', '噶尔', '噶尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542524', '日土县', 3, 0, 1, '日土县', '日土', '日土', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542525', '革吉县', 3, 0, 1, '革吉县', '革吉', '革吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542526', '改则县', 3, 0, 1, '改则县', '改则', '改则', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿里地_5425_id, '542527', '措勤县', 3, 0, 1, '措勤县', '措勤', '措勤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610102', '新城区', 3, 0, 1, '新城区', '新城', '新城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610103', '碑林区', 3, 0, 1, '碑林区', '碑林', '碑林', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610104', '莲湖区', 3, 0, 1, '莲湖区', '莲湖', '莲湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610111', '灞桥区', 3, 0, 1, '灞桥区', '灞桥', '灞桥', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610112', '未央区', 3, 0, 1, '未央区', '未央', '未央', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610113', '雁塔区', 3, 0, 1, '雁塔区', '雁塔', '雁塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610114', '阎良区', 3, 0, 1, '阎良区', '阎良', '阎良', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610115', '临潼区', 3, 0, 1, '临潼区', '临潼', '临潼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610116', '长安区', 3, 0, 1, '长安区', '长安', '长安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610117', '高陵区', 3, 0, 1, '高陵区', '高陵', '高陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610118', '鄠邑区', 3, 0, 1, '鄠邑区', '鄠邑', '鄠邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610122', '蓝田县', 3, 0, 1, '蓝田县', '蓝田', '蓝田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西安_6101_id, '610124', '周至县', 3, 0, 1, '周至县', '周至', '周至', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜川_6102_id, '610202', '王益区', 3, 0, 1, '王益区', '王益', '王益', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜川_6102_id, '610203', '印台区', 3, 0, 1, '印台区', '印台', '印台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜川_6102_id, '610204', '耀州区', 3, 0, 1, '耀州区', '耀州', '耀州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@铜川_6102_id, '610222', '宜君县', 3, 0, 1, '宜君县', '宜君', '宜君', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610302', '渭滨区', 3, 0, 1, '渭滨区', '渭滨', '渭滨', '', NOW(), NOW(), 0);
-- 已插入 2700/3056 条区级数据

-- 批次 28：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610303', '金台区', 3, 0, 1, '金台区', '金台', '金台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610304', '陈仓区', 3, 0, 1, '陈仓区', '陈仓', '陈仓', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610305', '凤翔区', 3, 0, 1, '凤翔区', '凤翔', '凤翔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610323', '岐山县', 3, 0, 1, '岐山县', '岐山', '岐山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610324', '扶风县', 3, 0, 1, '扶风县', '扶风', '扶风', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610326', '眉县', 3, 0, 1, '眉县', '眉', '眉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610327', '陇县', 3, 0, 1, '陇县', '陇', '陇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610328', '千阳县', 3, 0, 1, '千阳县', '千阳', '千阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610329', '麟游县', 3, 0, 1, '麟游县', '麟游', '麟游', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610330', '凤县', 3, 0, 1, '凤县', '凤', '凤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@宝鸡_6103_id, '610331', '太白县', 3, 0, 1, '太白县', '太白', '太白', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610402', '秦都区', 3, 0, 1, '秦都区', '秦都', '秦都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610403', '杨陵区', 3, 0, 1, '杨陵区', '杨陵', '杨陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610404', '渭城区', 3, 0, 1, '渭城区', '渭城', '渭城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610422', '三原县', 3, 0, 1, '三原县', '三原', '三原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610423', '泾阳县', 3, 0, 1, '泾阳县', '泾阳', '泾阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610424', '乾县', 3, 0, 1, '乾县', '乾', '乾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610425', '礼泉县', 3, 0, 1, '礼泉县', '礼泉', '礼泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610426', '永寿县', 3, 0, 1, '永寿县', '永寿', '永寿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610428', '长武县', 3, 0, 1, '长武县', '长武', '长武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610429', '旬邑县', 3, 0, 1, '旬邑县', '旬邑', '旬邑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610430', '淳化县', 3, 0, 1, '淳化县', '淳化', '淳化', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610431', '武功县', 3, 0, 1, '武功县', '武功', '武功', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610481', '兴平市', 3, 0, 1, '兴平市', '兴平', '兴平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@咸阳_6104_id, '610482', '彬州市', 3, 0, 1, '彬州市', '彬州', '彬州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610502', '临渭区', 3, 0, 1, '临渭区', '临渭', '临渭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610503', '华州区', 3, 0, 1, '华州区', '华州', '华州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610522', '潼关县', 3, 0, 1, '潼关县', '潼关', '潼关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610523', '大荔县', 3, 0, 1, '大荔县', '大荔', '大荔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610524', '合阳县', 3, 0, 1, '合阳县', '合阳', '合阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610525', '澄城县', 3, 0, 1, '澄城县', '澄城', '澄城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610526', '蒲城县', 3, 0, 1, '蒲城县', '蒲城', '蒲城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610527', '白水县', 3, 0, 1, '白水县', '白水', '白水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610528', '富平县', 3, 0, 1, '富平县', '富平', '富平', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610581', '韩城市', 3, 0, 1, '韩城市', '韩城', '韩城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@渭南_6105_id, '610582', '华阴市', 3, 0, 1, '华阴市', '华阴', '华阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610602', '宝塔区', 3, 0, 1, '宝塔区', '宝塔', '宝塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610603', '安塞区', 3, 0, 1, '安塞区', '安塞', '安塞', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610621', '延长县', 3, 0, 1, '延长县', '延长', '延长', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610622', '延川县', 3, 0, 1, '延川县', '延川', '延川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610625', '志丹县', 3, 0, 1, '志丹县', '志丹', '志丹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610626', '吴起县', 3, 0, 1, '吴起县', '吴起', '吴起', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610627', '甘泉县', 3, 0, 1, '甘泉县', '甘泉', '甘泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610628', '富县', 3, 0, 1, '富县', '富', '富', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610629', '洛川县', 3, 0, 1, '洛川县', '洛川', '洛川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610630', '宜川县', 3, 0, 1, '宜川县', '宜川', '宜川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610631', '黄龙县', 3, 0, 1, '黄龙县', '黄龙', '黄龙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610632', '黄陵县', 3, 0, 1, '黄陵县', '黄陵', '黄陵', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@延安_6106_id, '610681', '子长市', 3, 0, 1, '子长市', '子长', '子长', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610702', '汉台区', 3, 0, 1, '汉台区', '汉台', '汉台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610703', '南郑区', 3, 0, 1, '南郑区', '南郑', '南郑', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610722', '城固县', 3, 0, 1, '城固县', '城固', '城固', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610723', '洋县', 3, 0, 1, '洋县', '洋', '洋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610724', '西乡县', 3, 0, 1, '西乡县', '西乡', '西乡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610725', '勉县', 3, 0, 1, '勉县', '勉', '勉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610726', '宁强县', 3, 0, 1, '宁强县', '宁强', '宁强', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610727', '略阳县', 3, 0, 1, '略阳县', '略阳', '略阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610728', '镇巴县', 3, 0, 1, '镇巴县', '镇巴', '镇巴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610729', '留坝县', 3, 0, 1, '留坝县', '留坝', '留坝', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@汉中_6107_id, '610730', '佛坪县', 3, 0, 1, '佛坪县', '佛坪', '佛坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610802', '榆阳区', 3, 0, 1, '榆阳区', '榆阳', '榆阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610803', '横山区', 3, 0, 1, '横山区', '横山', '横山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610822', '府谷县', 3, 0, 1, '府谷县', '府谷', '府谷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610824', '靖边县', 3, 0, 1, '靖边县', '靖边', '靖边', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610825', '定边县', 3, 0, 1, '定边县', '定边', '定边', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610826', '绥德县', 3, 0, 1, '绥德县', '绥德', '绥德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610827', '米脂县', 3, 0, 1, '米脂县', '米脂', '米脂', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610828', '佳县', 3, 0, 1, '佳县', '佳', '佳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610829', '吴堡县', 3, 0, 1, '吴堡县', '吴堡', '吴堡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610830', '清涧县', 3, 0, 1, '清涧县', '清涧', '清涧', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610831', '子洲县', 3, 0, 1, '子洲县', '子洲', '子洲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@榆林_6108_id, '610881', '神木市', 3, 0, 1, '神木市', '神木', '神木', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610902', '汉滨区', 3, 0, 1, '汉滨区', '汉滨', '汉滨', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610921', '汉阴县', 3, 0, 1, '汉阴县', '汉阴', '汉阴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610922', '石泉县', 3, 0, 1, '石泉县', '石泉', '石泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610923', '宁陕县', 3, 0, 1, '宁陕县', '宁陕', '宁陕', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610924', '紫阳县', 3, 0, 1, '紫阳县', '紫阳', '紫阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610925', '岚皋县', 3, 0, 1, '岚皋县', '岚皋', '岚皋', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610926', '平利县', 3, 0, 1, '平利县', '平利', '平利', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610927', '镇坪县', 3, 0, 1, '镇坪县', '镇坪', '镇坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610929', '白河县', 3, 0, 1, '白河县', '白河', '白河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@安康_6109_id, '610981', '旬阳市', 3, 0, 1, '旬阳市', '旬阳', '旬阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611002', '商州区', 3, 0, 1, '商州区', '商州', '商州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611021', '洛南县', 3, 0, 1, '洛南县', '洛南', '洛南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611022', '丹凤县', 3, 0, 1, '丹凤县', '丹凤', '丹凤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611023', '商南县', 3, 0, 1, '商南县', '商南', '商南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611024', '山阳县', 3, 0, 1, '山阳县', '山阳', '山阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611025', '镇安县', 3, 0, 1, '镇安县', '镇安', '镇安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@商洛_6110_id, '611026', '柞水县', 3, 0, 1, '柞水县', '柞水', '柞水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620102', '城关区', 3, 0, 1, '城关区', '城关', '城关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620103', '七里河区', 3, 0, 1, '七里河区', '七里河', '七里河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620104', '西固区', 3, 0, 1, '西固区', '西固', '西固', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620105', '安宁区', 3, 0, 1, '安宁区', '安宁', '安宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620111', '红古区', 3, 0, 1, '红古区', '红古', '红古', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620121', '永登县', 3, 0, 1, '永登县', '永登', '永登', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620122', '皋兰县', 3, 0, 1, '皋兰县', '皋兰', '皋兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620123', '榆中县', 3, 0, 1, '榆中县', '榆中', '榆中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@兰州_6201_id, '620171', '兰州新区', 3, 0, 1, '兰州新区', '兰州新', '兰州新', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉峪关_6202_id, '620201001', '雄关街道', 3, 0, 1, '雄关街道', '雄关街道', '雄关街道', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉峪关_6202_id, '620201002', '钢城街道', 3, 0, 1, '钢城街道', '钢城街道', '钢城街道', '', NOW(), NOW(), 0);
-- 已插入 2800/3056 条区级数据

-- 批次 29：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉峪关_6202_id, '620201100', '新城镇', 3, 0, 1, '新城镇', '新城镇', '新城镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉峪关_6202_id, '620201101', '峪泉镇', 3, 0, 1, '峪泉镇', '峪泉镇', '峪泉镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@嘉峪关_6202_id, '620201102', '文殊镇', 3, 0, 1, '文殊镇', '文殊镇', '文殊镇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金昌_6203_id, '620302', '金川区', 3, 0, 1, '金川区', '金川', '金川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@金昌_6203_id, '620321', '永昌县', 3, 0, 1, '永昌县', '永昌', '永昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白银_6204_id, '620402', '白银区', 3, 0, 1, '白银区', '白银', '白银', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白银_6204_id, '620403', '平川区', 3, 0, 1, '平川区', '平川', '平川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白银_6204_id, '620421', '靖远县', 3, 0, 1, '靖远县', '靖远', '靖远', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白银_6204_id, '620422', '会宁县', 3, 0, 1, '会宁县', '会宁', '会宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@白银_6204_id, '620423', '景泰县', 3, 0, 1, '景泰县', '景泰', '景泰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620502', '秦州区', 3, 0, 1, '秦州区', '秦州', '秦州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620503', '麦积区', 3, 0, 1, '麦积区', '麦积', '麦积', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620521', '清水县', 3, 0, 1, '清水县', '清水', '清水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620522', '秦安县', 3, 0, 1, '秦安县', '秦安', '秦安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620523', '甘谷县', 3, 0, 1, '甘谷县', '甘谷', '甘谷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620524', '武山县', 3, 0, 1, '武山县', '武山', '武山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@天水_6205_id, '620525', '张家川回族自治县', 3, 0, 1, '张家川回族自治县', '张家川回族自治', '张家川回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武威_6206_id, '620602', '凉州区', 3, 0, 1, '凉州区', '凉州', '凉州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武威_6206_id, '620621', '民勤县', 3, 0, 1, '民勤县', '民勤', '民勤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武威_6206_id, '620622', '古浪县', 3, 0, 1, '古浪县', '古浪', '古浪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@武威_6206_id, '620623', '天祝藏族自治县', 3, 0, 1, '天祝藏族自治县', '天祝藏族自治', '天祝藏族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620702', '甘州区', 3, 0, 1, '甘州区', '甘州', '甘州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620721', '肃南裕固族自治县', 3, 0, 1, '肃南裕固族自治县', '肃南裕固族自治', '肃南裕固族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620722', '民乐县', 3, 0, 1, '民乐县', '民乐', '民乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620723', '临泽县', 3, 0, 1, '临泽县', '临泽', '临泽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620724', '高台县', 3, 0, 1, '高台县', '高台', '高台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@张掖_6207_id, '620725', '山丹县', 3, 0, 1, '山丹县', '山丹', '山丹', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620802', '崆峒区', 3, 0, 1, '崆峒区', '崆峒', '崆峒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620821', '泾川县', 3, 0, 1, '泾川县', '泾川', '泾川', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620822', '灵台县', 3, 0, 1, '灵台县', '灵台', '灵台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620823', '崇信县', 3, 0, 1, '崇信县', '崇信', '崇信', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620825', '庄浪县', 3, 0, 1, '庄浪县', '庄浪', '庄浪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620826', '静宁县', 3, 0, 1, '静宁县', '静宁', '静宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@平凉_6208_id, '620881', '华亭市', 3, 0, 1, '华亭市', '华亭', '华亭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620902', '肃州区', 3, 0, 1, '肃州区', '肃州', '肃州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620921', '金塔县', 3, 0, 1, '金塔县', '金塔', '金塔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620922', '瓜州县', 3, 0, 1, '瓜州县', '瓜州', '瓜州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620923', '肃北蒙古族自治县', 3, 0, 1, '肃北蒙古族自治县', '肃北蒙古族自治', '肃北蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620924', '阿克塞哈萨克族自治县', 3, 0, 1, '阿克塞哈萨克族自治县', '阿克塞哈萨克族自治', '阿克塞哈萨克族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620981', '玉门市', 3, 0, 1, '玉门市', '玉门', '玉门', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@酒泉_6209_id, '620982', '敦煌市', 3, 0, 1, '敦煌市', '敦煌', '敦煌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621002', '西峰区', 3, 0, 1, '西峰区', '西峰', '西峰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621021', '庆城县', 3, 0, 1, '庆城县', '庆城', '庆城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621022', '环县', 3, 0, 1, '环县', '环', '环', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621023', '华池县', 3, 0, 1, '华池县', '华池', '华池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621024', '合水县', 3, 0, 1, '合水县', '合水', '合水', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621025', '正宁县', 3, 0, 1, '正宁县', '正宁', '正宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621026', '宁县', 3, 0, 1, '宁县', '宁', '宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@庆阳_6210_id, '621027', '镇原县', 3, 0, 1, '镇原县', '镇原', '镇原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621102', '安定区', 3, 0, 1, '安定区', '安定', '安定', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621121', '通渭县', 3, 0, 1, '通渭县', '通渭', '通渭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621122', '陇西县', 3, 0, 1, '陇西县', '陇西', '陇西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621123', '渭源县', 3, 0, 1, '渭源县', '渭源', '渭源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621124', '临洮县', 3, 0, 1, '临洮县', '临洮', '临洮', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621125', '漳县', 3, 0, 1, '漳县', '漳', '漳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@定西_6211_id, '621126', '岷县', 3, 0, 1, '岷县', '岷', '岷', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621202', '武都区', 3, 0, 1, '武都区', '武都', '武都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621221', '成县', 3, 0, 1, '成县', '成', '成', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621222', '文县', 3, 0, 1, '文县', '文', '文', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621223', '宕昌县', 3, 0, 1, '宕昌县', '宕昌', '宕昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621224', '康县', 3, 0, 1, '康县', '康', '康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621225', '西和县', 3, 0, 1, '西和县', '西和', '西和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621226', '礼县', 3, 0, 1, '礼县', '礼', '礼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621227', '徽县', 3, 0, 1, '徽县', '徽', '徽', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@陇南_6212_id, '621228', '两当县', 3, 0, 1, '两当县', '两当', '两当', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622901', '临夏市', 3, 0, 1, '临夏市', '临夏', '临夏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622921', '临夏县', 3, 0, 1, '临夏县', '临夏', '临夏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622922', '康乐县', 3, 0, 1, '康乐县', '康乐', '康乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622923', '永靖县', 3, 0, 1, '永靖县', '永靖', '永靖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622924', '广河县', 3, 0, 1, '广河县', '广河', '广河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622925', '和政县', 3, 0, 1, '和政县', '和政', '和政', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622926', '东乡族自治县', 3, 0, 1, '东乡族自治县', '东乡族自治', '东乡族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@临夏回族_6229_id, '622927', '积石山保安族东乡族撒拉族自治县', 3, 0, 1, '积石山保安族东乡族撒拉族自治县', '积石山保安族东乡族撒拉族自治', '积石山保安族东乡族撒拉族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623001', '合作市', 3, 0, 1, '合作市', '合作', '合作', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623021', '临潭县', 3, 0, 1, '临潭县', '临潭', '临潭', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623022', '卓尼县', 3, 0, 1, '卓尼县', '卓尼', '卓尼', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623023', '舟曲县', 3, 0, 1, '舟曲县', '舟曲', '舟曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623024', '迭部县', 3, 0, 1, '迭部县', '迭部', '迭部', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623025', '玛曲县', 3, 0, 1, '玛曲县', '玛曲', '玛曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623026', '碌曲县', 3, 0, 1, '碌曲县', '碌曲', '碌曲', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@甘南藏族_6230_id, '623027', '夏河县', 3, 0, 1, '夏河县', '夏河', '夏河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630102', '城东区', 3, 0, 1, '城东区', '城东', '城东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630103', '城中区', 3, 0, 1, '城中区', '城中', '城中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630104', '城西区', 3, 0, 1, '城西区', '城西', '城西', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630105', '城北区', 3, 0, 1, '城北区', '城北', '城北', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630106', '湟中区', 3, 0, 1, '湟中区', '湟中', '湟中', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630121', '大通回族土族自治县', 3, 0, 1, '大通回族土族自治县', '大通回族土族自治', '大通回族土族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@西宁_6301_id, '630123', '湟源县', 3, 0, 1, '湟源县', '湟源', '湟源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630202', '乐都区', 3, 0, 1, '乐都区', '乐都', '乐都', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630203', '平安区', 3, 0, 1, '平安区', '平安', '平安', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630222', '民和回族土族自治县', 3, 0, 1, '民和回族土族自治县', '民和回族土族自治', '民和回族土族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630223', '互助土族自治县', 3, 0, 1, '互助土族自治县', '互助土族自治', '互助土族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630224', '化隆回族自治县', 3, 0, 1, '化隆回族自治县', '化隆回族自治', '化隆回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海东_6302_id, '630225', '循化撒拉族自治县', 3, 0, 1, '循化撒拉族自治县', '循化撒拉族自治', '循化撒拉族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海北藏族_6322_id, '632221', '门源回族自治县', 3, 0, 1, '门源回族自治县', '门源回族自治', '门源回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海北藏族_6322_id, '632222', '祁连县', 3, 0, 1, '祁连县', '祁连', '祁连', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海北藏族_6322_id, '632223', '海晏县', 3, 0, 1, '海晏县', '海晏', '海晏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海北藏族_6322_id, '632224', '刚察县', 3, 0, 1, '刚察县', '刚察', '刚察', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄南藏族_6323_id, '632301', '同仁市', 3, 0, 1, '同仁市', '同仁', '同仁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄南藏族_6323_id, '632322', '尖扎县', 3, 0, 1, '尖扎县', '尖扎', '尖扎', '', NOW(), NOW(), 0);
-- 已插入 2900/3056 条区级数据

-- 批次 30：插入 100 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄南藏族_6323_id, '632323', '泽库县', 3, 0, 1, '泽库县', '泽库', '泽库', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@黄南藏族_6323_id, '632324', '河南蒙古族自治县', 3, 0, 1, '河南蒙古族自治县', '河南蒙古族自治', '河南蒙古族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南藏族_6325_id, '632521', '共和县', 3, 0, 1, '共和县', '共和', '共和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南藏族_6325_id, '632522', '同德县', 3, 0, 1, '同德县', '同德', '同德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南藏族_6325_id, '632523', '贵德县', 3, 0, 1, '贵德县', '贵德', '贵德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南藏族_6325_id, '632524', '兴海县', 3, 0, 1, '兴海县', '兴海', '兴海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海南藏族_6325_id, '632525', '贵南县', 3, 0, 1, '贵南县', '贵南', '贵南', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632621', '玛沁县', 3, 0, 1, '玛沁县', '玛沁', '玛沁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632622', '班玛县', 3, 0, 1, '班玛县', '班玛', '班玛', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632623', '甘德县', 3, 0, 1, '甘德县', '甘德', '甘德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632624', '达日县', 3, 0, 1, '达日县', '达日', '达日', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632625', '久治县', 3, 0, 1, '久治县', '久治', '久治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@果洛藏族_6326_id, '632626', '玛多县', 3, 0, 1, '玛多县', '玛多', '玛多', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632701', '玉树市', 3, 0, 1, '玉树市', '玉树', '玉树', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632722', '杂多县', 3, 0, 1, '杂多县', '杂多', '杂多', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632723', '称多县', 3, 0, 1, '称多县', '称多', '称多', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632724', '治多县', 3, 0, 1, '治多县', '治多', '治多', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632725', '囊谦县', 3, 0, 1, '囊谦县', '囊谦', '囊谦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@玉树藏族_6327_id, '632726', '曲麻莱县', 3, 0, 1, '曲麻莱县', '曲麻莱', '曲麻莱', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632801', '格尔木市', 3, 0, 1, '格尔木市', '格尔木', '格尔木', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632802', '德令哈市', 3, 0, 1, '德令哈市', '德令哈', '德令哈', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632803', '茫崖市', 3, 0, 1, '茫崖市', '茫崖', '茫崖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632821', '乌兰县', 3, 0, 1, '乌兰县', '乌兰', '乌兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632822', '都兰县', 3, 0, 1, '都兰县', '都兰', '都兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632823', '天峻县', 3, 0, 1, '天峻县', '天峻', '天峻', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@海西蒙古族藏族_6328_id, '632857', '大柴旦行政委员会', 3, 0, 1, '大柴旦行政委员会', '大柴旦行政委员会', '大柴旦行政委员会', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640104', '兴庆区', 3, 0, 1, '兴庆区', '兴庆', '兴庆', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640105', '西夏区', 3, 0, 1, '西夏区', '西夏', '西夏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640106', '金凤区', 3, 0, 1, '金凤区', '金凤', '金凤', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640121', '永宁县', 3, 0, 1, '永宁县', '永宁', '永宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640122', '贺兰县', 3, 0, 1, '贺兰县', '贺兰', '贺兰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@银川_6401_id, '640181', '灵武市', 3, 0, 1, '灵武市', '灵武', '灵武', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石嘴山_6402_id, '640202', '大武口区', 3, 0, 1, '大武口区', '大武口', '大武口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石嘴山_6402_id, '640205', '惠农区', 3, 0, 1, '惠农区', '惠农', '惠农', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@石嘴山_6402_id, '640221', '平罗县', 3, 0, 1, '平罗县', '平罗', '平罗', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吴忠_6403_id, '640302', '利通区', 3, 0, 1, '利通区', '利通', '利通', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吴忠_6403_id, '640303', '红寺堡区', 3, 0, 1, '红寺堡区', '红寺堡', '红寺堡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吴忠_6403_id, '640323', '盐池县', 3, 0, 1, '盐池县', '盐池', '盐池', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吴忠_6403_id, '640324', '同心县', 3, 0, 1, '同心县', '同心', '同心', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吴忠_6403_id, '640381', '青铜峡市', 3, 0, 1, '青铜峡市', '青铜峡', '青铜峡', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@固原_6404_id, '640402', '原州区', 3, 0, 1, '原州区', '原州', '原州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@固原_6404_id, '640422', '西吉县', 3, 0, 1, '西吉县', '西吉', '西吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@固原_6404_id, '640423', '隆德县', 3, 0, 1, '隆德县', '隆德', '隆德', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@固原_6404_id, '640424', '泾源县', 3, 0, 1, '泾源县', '泾源', '泾源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@固原_6404_id, '640425', '彭阳县', 3, 0, 1, '彭阳县', '彭阳', '彭阳', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中卫_6405_id, '640502', '沙坡头区', 3, 0, 1, '沙坡头区', '沙坡头', '沙坡头', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中卫_6405_id, '640521', '中宁县', 3, 0, 1, '中宁县', '中宁', '中宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@中卫_6405_id, '640522', '海原县', 3, 0, 1, '海原县', '海原', '海原', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650102', '天山区', 3, 0, 1, '天山区', '天山', '天山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650103', '沙依巴克区', 3, 0, 1, '沙依巴克区', '沙依巴克', '沙依巴克', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650104', '新市区', 3, 0, 1, '新市区', '新市', '新市', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650105', '水磨沟区', 3, 0, 1, '水磨沟区', '水磨沟', '水磨沟', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650106', '头屯河区', 3, 0, 1, '头屯河区', '头屯河', '头屯河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650107', '达坂城区', 3, 0, 1, '达坂城区', '达坂城', '达坂城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650109', '米东区', 3, 0, 1, '米东区', '米东', '米东', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@乌鲁木齐_6501_id, '650121', '乌鲁木齐县', 3, 0, 1, '乌鲁木齐县', '乌鲁木齐', '乌鲁木齐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克拉玛依_6502_id, '650202', '独山子区', 3, 0, 1, '独山子区', '独山子', '独山子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克拉玛依_6502_id, '650203', '克拉玛依区', 3, 0, 1, '克拉玛依区', '克拉玛依', '克拉玛依', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克拉玛依_6502_id, '650204', '白碱滩区', 3, 0, 1, '白碱滩区', '白碱滩', '白碱滩', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克拉玛依_6502_id, '650205', '乌尔禾区', 3, 0, 1, '乌尔禾区', '乌尔禾', '乌尔禾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吐鲁番_6504_id, '650402', '高昌区', 3, 0, 1, '高昌区', '高昌', '高昌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吐鲁番_6504_id, '650421', '鄯善县', 3, 0, 1, '鄯善县', '鄯善', '鄯善', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@吐鲁番_6504_id, '650422', '托克逊县', 3, 0, 1, '托克逊县', '托克逊', '托克逊', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈密_6505_id, '650502', '伊州区', 3, 0, 1, '伊州区', '伊州', '伊州', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈密_6505_id, '650521', '巴里坤哈萨克自治县', 3, 0, 1, '巴里坤哈萨克自治县', '巴里坤哈萨克自治', '巴里坤哈萨克自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@哈密_6505_id, '650522', '伊吾县', 3, 0, 1, '伊吾县', '伊吾', '伊吾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652301', '昌吉市', 3, 0, 1, '昌吉市', '昌吉', '昌吉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652302', '阜康市', 3, 0, 1, '阜康市', '阜康', '阜康', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652323', '呼图壁县', 3, 0, 1, '呼图壁县', '呼图壁', '呼图壁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652324', '玛纳斯县', 3, 0, 1, '玛纳斯县', '玛纳斯', '玛纳斯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652325', '奇台县', 3, 0, 1, '奇台县', '奇台', '奇台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652327', '吉木萨尔县', 3, 0, 1, '吉木萨尔县', '吉木萨尔', '吉木萨尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@昌吉回族_6523_id, '652328', '木垒哈萨克自治县', 3, 0, 1, '木垒哈萨克自治县', '木垒哈萨克自治', '木垒哈萨克自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@博尔塔拉蒙古_6527_id, '652701', '博乐市', 3, 0, 1, '博乐市', '博乐', '博乐', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@博尔塔拉蒙古_6527_id, '652702', '阿拉山口市', 3, 0, 1, '阿拉山口市', '阿拉山口', '阿拉山口', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@博尔塔拉蒙古_6527_id, '652722', '精河县', 3, 0, 1, '精河县', '精河', '精河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@博尔塔拉蒙古_6527_id, '652723', '温泉县', 3, 0, 1, '温泉县', '温泉', '温泉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652801', '库尔勒市', 3, 0, 1, '库尔勒市', '库尔勒', '库尔勒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652822', '轮台县', 3, 0, 1, '轮台县', '轮台', '轮台', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652823', '尉犁县', 3, 0, 1, '尉犁县', '尉犁', '尉犁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652824', '若羌县', 3, 0, 1, '若羌县', '若羌', '若羌', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652825', '且末县', 3, 0, 1, '且末县', '且末', '且末', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652826', '焉耆回族自治县', 3, 0, 1, '焉耆回族自治县', '焉耆回族自治', '焉耆回族自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652827', '和静县', 3, 0, 1, '和静县', '和静', '和静', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652828', '和硕县', 3, 0, 1, '和硕县', '和硕', '和硕', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@巴音郭楞蒙古_6528_id, '652829', '博湖县', 3, 0, 1, '博湖县', '博湖', '博湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652901', '阿克苏市', 3, 0, 1, '阿克苏市', '阿克苏', '阿克苏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652902', '库车市', 3, 0, 1, '库车市', '库车', '库车', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652922', '温宿县', 3, 0, 1, '温宿县', '温宿', '温宿', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652924', '沙雅县', 3, 0, 1, '沙雅县', '沙雅', '沙雅', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652925', '新和县', 3, 0, 1, '新和县', '新和', '新和', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652926', '拜城县', 3, 0, 1, '拜城县', '拜城', '拜城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652927', '乌什县', 3, 0, 1, '乌什县', '乌什', '乌什', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652928', '阿瓦提县', 3, 0, 1, '阿瓦提县', '阿瓦提', '阿瓦提', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿克苏地_6529_id, '652929', '柯坪县', 3, 0, 1, '柯坪县', '柯坪', '柯坪', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克孜勒苏柯尔克孜_6530_id, '653001', '阿图什市', 3, 0, 1, '阿图什市', '阿图什', '阿图什', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克孜勒苏柯尔克孜_6530_id, '653022', '阿克陶县', 3, 0, 1, '阿克陶县', '阿克陶', '阿克陶', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克孜勒苏柯尔克孜_6530_id, '653023', '阿合奇县', 3, 0, 1, '阿合奇县', '阿合奇', '阿合奇', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@克孜勒苏柯尔克孜_6530_id, '653024', '乌恰县', 3, 0, 1, '乌恰县', '乌恰', '乌恰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653101', '喀什市', 3, 0, 1, '喀什市', '喀什', '喀什', '', NOW(), NOW(), 0);
-- 已插入 3000/3056 条区级数据

-- 批次 31：插入 56 条区级数据
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653121', '疏附县', 3, 0, 1, '疏附县', '疏附', '疏附', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653122', '疏勒县', 3, 0, 1, '疏勒县', '疏勒', '疏勒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653123', '英吉沙县', 3, 0, 1, '英吉沙县', '英吉沙', '英吉沙', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653124', '泽普县', 3, 0, 1, '泽普县', '泽普', '泽普', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653125', '莎车县', 3, 0, 1, '莎车县', '莎车', '莎车', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653126', '叶城县', 3, 0, 1, '叶城县', '叶城', '叶城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653127', '麦盖提县', 3, 0, 1, '麦盖提县', '麦盖提', '麦盖提', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653128', '岳普湖县', 3, 0, 1, '岳普湖县', '岳普湖', '岳普湖', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653129', '伽师县', 3, 0, 1, '伽师县', '伽师', '伽师', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653130', '巴楚县', 3, 0, 1, '巴楚县', '巴楚', '巴楚', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@喀什地_6531_id, '653131', '塔什库尔干塔吉克自治县', 3, 0, 1, '塔什库尔干塔吉克自治县', '塔什库尔干塔吉克自治', '塔什库尔干塔吉克自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653201', '和田市', 3, 0, 1, '和田市', '和田', '和田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653221', '和田县', 3, 0, 1, '和田县', '和田', '和田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653222', '墨玉县', 3, 0, 1, '墨玉县', '墨玉', '墨玉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653223', '皮山县', 3, 0, 1, '皮山县', '皮山', '皮山', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653224', '洛浦县', 3, 0, 1, '洛浦县', '洛浦', '洛浦', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653225', '策勒县', 3, 0, 1, '策勒县', '策勒', '策勒', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653226', '于田县', 3, 0, 1, '于田县', '于田', '于田', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@和田地_6532_id, '653227', '民丰县', 3, 0, 1, '民丰县', '民丰', '民丰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654002', '伊宁市', 3, 0, 1, '伊宁市', '伊宁', '伊宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654003', '奎屯市', 3, 0, 1, '奎屯市', '奎屯', '奎屯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654004', '霍尔果斯市', 3, 0, 1, '霍尔果斯市', '霍尔果斯', '霍尔果斯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654021', '伊宁县', 3, 0, 1, '伊宁县', '伊宁', '伊宁', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654022', '察布查尔锡伯自治县', 3, 0, 1, '察布查尔锡伯自治县', '察布查尔锡伯自治', '察布查尔锡伯自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654023', '霍城县', 3, 0, 1, '霍城县', '霍城', '霍城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654024', '巩留县', 3, 0, 1, '巩留县', '巩留', '巩留', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654025', '新源县', 3, 0, 1, '新源县', '新源', '新源', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654026', '昭苏县', 3, 0, 1, '昭苏县', '昭苏', '昭苏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654027', '特克斯县', 3, 0, 1, '特克斯县', '特克斯', '特克斯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@伊犁哈萨克_6540_id, '654028', '尼勒克县', 3, 0, 1, '尼勒克县', '尼勒克', '尼勒克', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654201', '塔城市', 3, 0, 1, '塔城市', '塔城', '塔城', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654202', '乌苏市', 3, 0, 1, '乌苏市', '乌苏', '乌苏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654203', '沙湾市', 3, 0, 1, '沙湾市', '沙湾', '沙湾', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654221', '额敏县', 3, 0, 1, '额敏县', '额敏', '额敏', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654224', '托里县', 3, 0, 1, '托里县', '托里', '托里', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654225', '裕民县', 3, 0, 1, '裕民县', '裕民', '裕民', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@塔城地_6542_id, '654226', '和布克赛尔蒙古自治县', 3, 0, 1, '和布克赛尔蒙古自治县', '和布克赛尔蒙古自治', '和布克赛尔蒙古自治', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654301', '阿勒泰市', 3, 0, 1, '阿勒泰市', '阿勒泰', '阿勒泰', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654321', '布尔津县', 3, 0, 1, '布尔津县', '布尔津', '布尔津', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654322', '富蕴县', 3, 0, 1, '富蕴县', '富蕴', '富蕴', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654323', '福海县', 3, 0, 1, '福海县', '福海', '福海', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654324', '哈巴河县', 3, 0, 1, '哈巴河县', '哈巴河', '哈巴河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654325', '青河县', 3, 0, 1, '青河县', '青河', '青河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@阿勒泰地_6543_id, '654326', '吉木乃县', 3, 0, 1, '吉木乃县', '吉木乃', '吉木乃', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659001', '石河子市', 3, 0, 1, '石河子市', '石河子', '石河子', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659002', '阿拉尔市', 3, 0, 1, '阿拉尔市', '阿拉尔', '阿拉尔', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659003', '图木舒克市', 3, 0, 1, '图木舒克市', '图木舒克', '图木舒克', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659004', '五家渠市', 3, 0, 1, '五家渠市', '五家渠', '五家渠', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659005', '北屯市', 3, 0, 1, '北屯市', '北屯', '北屯', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659006', '铁门关市', 3, 0, 1, '铁门关市', '铁门关', '铁门关', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659007', '双河市', 3, 0, 1, '双河市', '双河', '双河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659008', '可克达拉市', 3, 0, 1, '可克达拉市', '可克达拉', '可克达拉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659009', '昆玉市', 3, 0, 1, '昆玉市', '昆玉', '昆玉', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659010', '胡杨河市', 3, 0, 1, '胡杨河市', '胡杨河', '胡杨河', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659011', '新星市', 3, 0, 1, '新星市', '新星', '新星', '', NOW(), NOW(), 0);
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@自治区直辖县级行政区划_6590_id, '659012', '白杨市', 3, 0, 1, '白杨市', '白杨', '白杨', '', NOW(), NOW(), 0);
-- 已插入 3056/3056 条区级数据

-- ----------------------------
-- 数据验证
-- ----------------------------
SELECT '数据导入完成！' AS message;
SELECT level AS '层级', COUNT(*) AS '数量' FROM sys_region WHERE deleted = 0 GROUP BY level ORDER BY level;
SELECT '省级数据示例：' AS message;
SELECT id, region_code, region_name, level FROM sys_region WHERE level = 1 AND deleted = 0 LIMIT 5;
SELECT '市级数据示例：' AS message;
SELECT id, parent_id, region_code, region_name, level FROM sys_region WHERE level = 2 AND deleted = 0 LIMIT 5;
SELECT '区级数据示例：' AS message;
SELECT id, parent_id, region_code, region_name, level FROM sys_region WHERE level = 3 AND deleted = 0 LIMIT 5;

SET FOREIGN_KEY_CHECKS = 1;