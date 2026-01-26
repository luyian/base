-- ============================
-- 行政区划模块完整初始化脚本
-- 包含：表结构 + 权限配置 + 测试数据
-- 执行方式：mysql -u root -p base_db < init_region.sql
-- ============================

-- ============================
-- 1. 创建行政区划表
-- ============================
DROP TABLE IF EXISTS `sys_region`;
CREATE TABLE `sys_region` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父级ID（0表示顶级）',
    `region_code` VARCHAR(20) NOT NULL COMMENT '行政区划代码（统计用区划代码）',
    `region_name` VARCHAR(100) NOT NULL COMMENT '行政区划名称',
    `level` TINYINT(1) NOT NULL COMMENT '层级（1-省，2-市，3-区，4-街道）',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `full_name` VARCHAR(255) DEFAULT NULL COMMENT '全称（如：广东省广州市天河区）',
    `short_name` VARCHAR(50) DEFAULT NULL COMMENT '简称',
    `pinyin` VARCHAR(100) DEFAULT NULL COMMENT '拼音',
    `pinyin_prefix` VARCHAR(50) DEFAULT NULL COMMENT '拼音首字母',
    `longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_region_code` (`region_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_region_name` (`region_name`),
    KEY `idx_pinyin_prefix` (`pinyin_prefix`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行政区划表';

-- ============================
-- 2. 添加权限配置
-- ============================

-- 行政区划管理菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(108, 1, '行政区划', 'system:region:list', 2, '/system/region', 'system/Region', 'Location', 8, 1, 1, 'system');

-- 行政区划管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `status`, `create_by`) VALUES
(10801, 108, '区划查询', 'system:region:query', 3, NULL, NULL, NULL, 1, 1, 1, 'system'),
(10802, 108, '区划新增', 'system:region:add', 3, NULL, NULL, NULL, 2, 1, 1, 'system'),
(10803, 108, '区划编辑', 'system:region:edit', 3, NULL, NULL, NULL, 3, 1, 1, 'system'),
(10804, 108, '区划删除', 'system:region:delete', 3, NULL, NULL, NULL, 4, 1, 1, 'system'),
(10805, 108, '区划导入', 'system:region:import', 3, NULL, NULL, NULL, 5, 1, 1, 'system');

-- 为超级管理员角色分配权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission` WHERE id IN (108, 10801, 10802, 10803, 10804, 10805);

-- ============================
-- 3. 导入测试数据
-- ============================

-- 3.1 省级行政区（34个）
INSERT INTO `sys_region` (`parent_id`, `region_code`, `region_name`, `level`, `sort`, `status`, `full_name`, `short_name`, `pinyin`, `pinyin_prefix`, `create_by`) VALUES
(0, '110000', '北京市', 1, 1, 1, '北京市', '北京', 'Beijing', 'BJ', 'system'),
(0, '120000', '天津市', 1, 2, 1, '天津市', '天津', 'Tianjin', 'TJ', 'system'),
(0, '130000', '河北省', 1, 3, 1, '河北省', '河北', 'Hebei', 'HB', 'system'),
(0, '140000', '山西省', 1, 4, 1, '山西省', '山西', 'Shanxi', 'SX', 'system'),
(0, '150000', '内蒙古自治区', 1, 5, 1, '内蒙古自治区', '内蒙古', 'Neimenggu', 'NMG', 'system'),
(0, '210000', '辽宁省', 1, 6, 1, '辽宁省', '辽宁', 'Liaoning', 'LN', 'system'),
(0, '220000', '吉林省', 1, 7, 1, '吉林省', '吉林', 'Jilin', 'JL', 'system'),
(0, '230000', '黑龙江省', 1, 8, 1, '黑龙江省', '黑龙江', 'Heilongjiang', 'HLJ', 'system'),
(0, '310000', '上海市', 1, 9, 1, '上海市', '上海', 'Shang, 'system'),
(0, '320000', '江苏省', 1, 10, 1, '江苏省', '江苏', 'Jiangsu', 'JS', 'system'),
(0, '330000', '浙江省', 1, 11, 1, '浙江省', '浙江', 'Zhejiang', 'ZJ', 'system'),
(0, '340000', '安徽省', 1, 12, 1, '安徽省', '安徽', 'Anhui', 'AH', 'system'),
(0, '350000', '福建省', 1, 13, 1, '福建省', '福建', 'Fujian', 'FJ', 'system'),
(0, '360000', '江西省', 1, 14, 1, '江西省', '江西', 'Jiangxi', 'JX', 'system'),
(0, '370000', '山东省', 1, 15, 1, '山东省', '山东', 'Shandong', 'SD', 'system'),
(0, '410000', '河南省', 1, 16, 1, '河南省', '河南', 'Henan', 'HN', 'system'),
(0, '420000', '湖北省', 1, 17, 1, '湖北省', '湖北', 'Hubei', 'HB', 'system'),
(0, '430000', '湖南省', 1, 18, 1, '湖南省', '湖南', 'Hunan', 'HN', 'system'),
(0, '440000', '广东省', 1, 19, 1, '广东省', '广东', 'Guangdong', 'GD', 'system'),
(0, '450000', '广西壮族自治区', 1, 20, 1, '广西壮族自治区', '广西', 'Guangxi', 'GX', 'system'),
(0, '460000', '海南省', 1, 21, 1, '海南省', '海南', 'Hainan', 'HN', 'system'),
(0, '500000', '重庆市', 1, 22, 1, '重庆市', '重庆', 'Chongqing', 'CQ', 'system'),
(0, '510000', '四川省', 1, 23, 1, '四川省', '四川', 'Sichuan', 'SC', 'system'),
(0, '520000', '贵州省', 1, 24, 1, '贵州省', '贵州', 'Guizhou', 'GZ', 'system'),
(0, '530000', '云南省', 1, 25, 1, '云南省', '云南', 'Yunnan', 'YN', 'system'),
(0, '540000', '西藏自治区', 1, 26, 1, '西藏自治区', '西藏', 'Xizang', 'XZ', 'system'),
(0, '610000', '陕西省', 1, 27, 1, '陕西省', '陕西', 'Shaanxi', 'SN', 'system'),
(0, '620000', '甘肃省', 1, 28, 1, '甘肃省', '甘肃', 'Gansu', 'GS', 'system'),
(0, '630000', '青海省', 1, 29, 1, '青海省', '青海', 'Qinghai', 'QH', 'system'),
(0, '640000', '宁夏回族自治区', 1, 30, 1, '宁夏回族自治区', '宁夏', 'Ningxia', 'NX', 'system'),
(0, '650000', '新疆维吾尔自治区', 1, 31, 1, '新疆维吾尔自治区', '新疆', 'Xinjiang', 'XJ', 'system'),
(0, '710000', '台湾省', 1, 32, 1, '台湾省', '台湾', 'Taiwan', 'TW', 'system'),
(0, '810000', '香港特别行政区', 1, 33, 1, '香港特别行政区', '香港', 'Xianggang', 'HK', 'system'),
(0, '820000', '澳门特别行政区', 1, 34, 1, '澳门特别行政区', '澳门', 'Aomen', 'MO', 'system');

-- 获取广东省的ID
SET @guangdong_id = (SELECT id FROM sys_region WHERE region_code = '440000');

-- 3.2 广东省市级行政区（21个）
INSERT INTO `sys_region` (`parent_id`, `region_code`, `region_name`, `level`, `sort`, `status`, `full_name`, `short_name`, `pinyin`, `pinyin_prefix`, `create_by`) VALUES
(@guangdong_id, '440100', '广州市', 2, 1, 1, '广东省广州市', '广州', 'Guangzhou', 'GZ', 'system'),
(@guangdong_id, '440200', '韶关市', 2, 2, 1, '广东省韶关市', '韶关', 'Shaoguan', 'SG', 'system'),
(@guangdong_id, '440300', '深圳市', 2, 3, 1, '广东省深圳市', '深圳', 'Shenzhen'system'),
(@guangdong_id, '440400', '珠海市', 2, 4, 1, '广东省珠海市', '珠海', 'Zhuhai', 'ZH', 'system'),
(@guangdong_id, '440500', '汕头市', 2, 5, 1, '广东省汕头市', '汕头', 'Shantou', 'ST', 'system'),
(@guangdong_id, '440600', '佛山市', 2, 6, 1, '广东省佛山市', '佛山', 'Foshan', 'FS', 'system'),
(@guangdong_id, '440700', '江门市', 2, 7, 1, '广东省江门市', '江门', 'Jiangmen', 'JM', 'system'),
(@guangdong_id, '440800', '湛江市', 2, 8, 1, '广东省湛江市', '湛江', 'Zhanjiang', 'ZJ', 'system'),
(@guangdong_id, '440900', '茂名市', 2, 9, 1, '广东省茂名市', '茂名', 'Maoming', 'MM', 'system'),
(@guangdong_id, '441200', '肇庆市', 2, 10, 1, '广东省肇庆市', '肇庆', 'Zhaoqing', 'ZQ', 'system'),
(@guangdong_id, '441300', '惠州市', 2, 11, 1, '广东省惠州市', '惠州', 'Huizhou', 'HZ', 'system'),
(@guangdong_id, '441400', '梅州市', 2, 12, 1, '广东省梅州市', '梅州', 'Meizhou', 'MZ', 'system'),
(@guangdong_id, '441500', '汕尾市', 2, 13, 1, '广东省汕尾市', '汕尾', 'Shanwei', 'SW', 'system'),
(@guangdong_id, '441600', '河源市', 2, 14, 1, '广东省河源市', '河源', 'Heyuan', 'HY', 'system'),
(@guangdong_id, '441700', '阳江市', 2, 15, 1, '广东省阳江市', '阳江', 'Yangjiang', 'YJ', 'system'),
(@guangdong_id, '441800', '清远市', 2, 16, 1, '广东省清远市', '清远', 'Qingyuan', 'QY', 'system'),
(@guangdong_id, '441900', '东莞市', 2, 17, 1, '广东省东莞市', '东莞', 'Dongguan', 'DG', 'system'),
(@guangdong_id, '442000', '中山市', 2, 18, 1, '广东省中山市', '中山', 'Zhongshan', 'ZS', 'system'),
(@guangdong_id, '445100', '潮州市', 2, 19, 1, '广东省潮州市', '潮州', 'Chaozhou', 'CZ', 'system'),
(@guangdong_id, '445200', '揭阳市', 2, 20, 1, '广东省揭阳市', '揭阳', 'Jieyang', 'JY', 'system'),
(@guangdong_id, '445300', '云浮市', 2, 21, 1, '广东省云浮市', '云浮', 'Yunfu', 'YF', 'system');

-- 获取广州市的ID
SET @guangzhou_id = (SELECT id FROM sys_region WHERE region_code = '440100');

-- 3.3 广州市区级行政区（11个）
INSERT INTO `sys_region` (`parent_id`, `region_code`,ion_name`, `level`, `sort`, `status`, `full_name`, `short_name`, `pinyin`, `pinyin_prefix`, `create_by`) VALUES
(@guangzhou_id, '440103', '荔湾区', 3, 1, 1, '广东省广州市荔湾区', '荔湾', 'Liwan', 'LW', 'system'),
(@guangzhou_id, '440104', '越秀区', 3, 2, 1, '广东省广州市越秀区', '越秀', 'Yuexiu', 'YX', 'system'),
(@guangzhou_id, '440105', '海珠区', 3, 3, 1, '广东省广州市海珠区', '海珠', 'Haizhu', 'HZ', 'system'),
(@guangzhou_id, '440106', '天河区', 3, 4, 1, '广东省广州市天河区', '天河', 'Tianhe', 'TH', 'system'),
(@guangzhou_id, '440111', '白云区', 3, 5, 1, '广东省广州市白云区', '白云', 'Baiyun', 'BY', 'system'),
(@guangzhou_id, '440112', '黄埔区', 3, 6, 1, '广东省广州市黄埔区', '黄埔', 'Huangpu', 'HP', 'system'),
(@guangzhou_id, '440113', '番禺区', 3, 7, 1, '广东省广州市番禺区', '番禺', 'Panyu', 'PY', 'system'),
(@guangzhou_id, '440114', '花都区', 3, 8, 1, '广东省广州市花都区', '花都', 'Huadu', 'HD', 'system'),
(@guangzhou_id, '440115', '南沙区', 3, 9, 1, '广东省广州市南沙区', '南沙', 'Nansha', 'NS', 'system'),
(@guangzhou_id, '440117', '从化区', 3, 10, 1, '广东省广州市从化区', '从化', 'Conghua', 'CH', 'system'),
(@guangzhou_id, '440118', '增城区', 3, 11, 1, '广东省广州市增城区', '增城', 'Zengcheng', 'ZC', 'system');

-- ============================
-- 4. 数据验证
-- ============================
SELECT '=== 数据导入完成 ===' AS message;
SELECT
    CASE level
        WHEN 1 THEN '省级'
        WHEN 2 TH        WHEN 3 THEN '区级'
        WHEN 4 THEN '街道级'
    END AS '层级',
    COUNT(*) AS '数量'
FROM sys_region
GROUP BY level
ORDER BY level;

SELECT '=== 权限配置完成 ===' AS message;
SELECT
    permission_name AS '权限名称',
    permission_code AS '权限编码',
    CASE type
        WHEN 1 THEN '目录'
        WHEN 2 THEN '菜单'
        WHEN 3 THEN '按钮'
    END AS '权限类型'
FROM sys_permission
WHERE id IN (108, 10801, 10802, 10803, 10804, 10805)
ORDER BY id;

SELECT '=== 初始化完成，可以开始使用了！ ===' AS message;
