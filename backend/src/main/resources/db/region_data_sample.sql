-- =============================================
-- 行政区划测试数据（简化版）
-- 包含：全国34个省级行政区 + 广东省的城市数据
-- 用于快速测试功能
-- =============================================

-- 清空现有数据（可选）
-- TRUNCATE TABLE sys_region;

-- =============================================
-- 省级行政区（34个）
-- =============================================

-- 直辖市（4个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '110000', '北京市', 1, 1, 1, '北京市', '北京', 'Beijing', 'B', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '120000', '天津市', 1, 2, 1, '天津市', '天津', 'Tianjin', 'T', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '310000', '上海市', 1, 3, 1, '上海市', '上海', 'Shanghai', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '500000', '重庆市', 1, 4, 1, '重庆市', '重庆', 'Chongqing', 'C', NOW(), NOW(), 0);

-- 华北地区（3个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '130000', '河北省', 1, 5, 1, '河北省', '河北', 'Hebei', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '140000', '山西省', 1, 6, 1, '山西省', '山西', 'Shanxi', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '150000', '内蒙古自治区', 1, 7, 1, '内蒙古自治区', '内蒙古', 'Neimenggu', 'N', NOW(), NOW(), 0);

-- 东北地区（3个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '210000', '辽宁省', 1, 8, 1, '辽宁省', '辽宁', 'Liaoning', 'L', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '220000', '吉林省', 1, 9, 1, '吉林省', '吉林', 'Jilin', 'J', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '230000', '黑龙江省', 1, 10, 1, '黑龙江省', '黑龙江', 'Heilongjiang', 'H', NOW(), NOW(), 0);

-- 华东地区（7个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '320000', '江苏省', 1, 11, 1, '江苏省', '江苏', 'Jiangsu', 'J', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '330000', '浙江省', 1, 12, 1, '浙江省', '浙江', 'Zhejiang', 'Z', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '340000', '安徽省', 1, 13, 1, '安徽省', '安徽', 'Anhui', 'A', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '350000', '福建省', 1, 14, 1, '福建省', '福建', 'Fujian', 'F', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '360000', '江西省', 1, 15, 1, '江西省', '江西', 'Jiangxi', 'J', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '370000', '山东省', 1, 16, 1, '山东省', '山东', 'Shandong', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (paron_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '710000', '台湾省', 1, 17, 1, '台湾省', '台湾', 'Taiwan', 'T', NOW(), NOW(), 0);

-- 华中地区（3个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '410000', '河南省', 1, 18, 1, '河南省', '河南', 'Henan', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create, update_time, deleted)
VALUES (0, '420000', '湖北省', 1, 19, 1, '湖北省', '湖北', 'Hubei', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '430000', '湖南省', 1, 20, 1, '湖南省', '湖南', 'Hunan', 'H', NOW(), NOW(), 0);

-- 华南地区（3个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '440000', '广东省', 1, 21, 1, '广东省', '广东', 'Guangdong', 'G', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '450000', '广西壮族自治区', 1, 22, 1, '广西壮族自治区', '广西', 'Guangxi', 'G', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '460000', '海南省', 1, 23, 1, '海南省', '海南', 'Hainan', 'H', NOW(), NOW(), 0);

-- 西南地区（5个）
INSERT INTO sys_(parent_id, region_code, ame, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '510000', '四川省', 1, 24, 1, '四川省', '四川', 'Sichuan', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '520000', '贵州省', 1, 25, 1, '贵州省', '贵州', 'Guizhou', 'G', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '530000', '云南省', 1, 26, 1, '云南省', '云南', 'Yunnan', 'Y', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '540000', '西藏自治区', 1, 27, 1, '西藏自治区', '西藏', 'Xizang', 'X', NOW(), NOW(), 0);

-- 西北地区（5个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '610000', '陕西省', 1, 28, 1, '陕西省', '陕西', 'Shaanxi', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '620000', '甘肃省', 1, 29, 1, '甘肃省', '甘肃', 'Gansu', 'G', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '630000', '青海省', 1, 30, 1, '青海省', '青海', 'Qinghai', 'Q', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status,short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '640000', '宁夏回族自治区', 1, 31, 1, '宁夏回族自治区', '宁夏', 'Ningxia', 'N', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '650000', '新疆维吾尔自治区', 1, 32, 1, '新疆维吾尔自治区', '新疆', 'Xinjiang', 'X', NOW(), NOW(), 0);

-- 特别行政区（2个）
INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, updame, deleted)
VALUES (0, '810000', '香港特别行政区', 1, 33, 1, '香港特别行政区', '香港', 'Xianggang', 'X', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (0, '820000', '澳门特别行政区', 1, 34, 1, '澳门特别行政区', '澳门', 'Aomen', 'A', NOW(), NOW(), 0);


-- =============================================
-- 广东省的市级行政区（21个）
-- 注意：需要先查询广东省的 ID，这里假设为 @guangdong_id
-- =============================================

-- 获取广东省的ID
SET @guangdong_id = (ECT id FROM sys_region WHERE region_code = '440000');

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440100', '广州市', 2, 1, 1, '广东省广州市', '广州', 'Guangzhou', 'G', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440200', '韶关市', 2, 2, 1, '广东省韶关市', '韶关', 'Shaoguan', 'S', NOW(), NOW(), 0);

INSERT INTO sys_regiod, region_code, region_name, levet, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440300', '深圳市', 2, 3, 1, '广东省深圳市', '深圳', 'Shenzhen', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440400', '珠海市', 2, 4, 1, '广东省珠海市', '珠海', 'Zhuhai', 'Z', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440500' 2, 5, 1, '广东省汕头市', '汕头', 'Shantou', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440600', '佛山市', 2, 6, 1, '广东省佛山市', '佛山', 'Foshan', 'F', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440700', '江门市', 2, 7, 1, '广东省江门市', '江门', 'Jiangmen', 'J', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440800', '湛江市', 2, 8, 1, '广东省湛江市', '湛江', 'Zhanjiang', 'Z', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '440900', '茂名市', 2, 9, 1, '广东省茂名市', '茂名', 'MaomingOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441200', '肇庆市', 2, 10, 1, '广东省肇庆市', '肇庆', 'Zhaoqing', 'Z', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441300', '惠州市', 2, 11, 1, '广东省惠州市', '惠州', 'Huizhou', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441400', '梅州市', 2, 12, 1, '广东省梅州市', '梅州', 'Meizhou', 'M', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441500', '汕尾市', 2, 13, 1, '广东省汕尾市', '汕尾', 'Shanwei', 'S', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_namnyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441600', '河源市', 2, 14, 1, '广东省河源市', '河源', 'Heyuan', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441700', '阳江市', 2, 15, 1, '广东省阳江市', '阳江', 'Yangjiang', 'Y', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441800', '清远市'6, 1, '广东省清远市', '清远', 'Qingyuan', 'Q', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '441900', '东莞市', 2, 17, 1, '广东省东莞市', '东莞', 'Dongguan', 'D', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '442000', '中山市', 2, 18, 1, '广东省中山市', '中山', 'Zhongshan', 'Z', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '445100', '潮州市', 2, 19, 1, '广东省潮州市', '潮州', 'Chaozhou', 'C', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '445200', '揭阳市', 2, 20, 1, '广东省揭阳市', '揭阳', 'Jieyang', 'J', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangdong_id, '445300', '云浮市', 2, 21, 1, '广东省云浮市', '云浮', 'Yunfu', 'Y', NOW(), NOW(), 0);


-- =============================================
-- 广州市的区级行政区（11个）
-- =============================================

-- 获取广州市的ID
SET @guangzhou_id = (SELECT id FROM sys_region WHERE region_code = '440100');

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440103', '荔湾区', 3, 1, 1, '广东省广州市荔湾区', '荔湾', 'Liwan', 'L', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440104', '越秀区', 3, 2, 1, '广东省广州市越秀区', '越秀', 'Yuexiu', 'Y', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440105', '海珠区', 3, 3, 1, '广东省广州市海珠区', '海珠', 'Haizhu', 'H', NOW(), NOW(), 0);

IO sys_region (parent_id, regionon_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440106', '天河区', 3, 4, 1, '广东省广州市天河区', '天河', 'Tianhe', 'T', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440111', '白云区', 3, 5, 1, '广东省广州市白云区', '白云', 'Baiyun', 'B', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
Vngzhou_id, '440112', '黄埔区', 3, 6, 1, '广东省广州市黄埔区', '黄埔', 'Huangpu', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440113', '番禺区', 3, 7, 1, '广东省广州市番禺区', '番禺', 'Panyu', 'P', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440114', '花都区', 3, 8, 1, '广东省广州市花都区', '花都', 'Huadu', 'H', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440115', '南沙区', 3, 9, 1, '广东省广州市南沙区', '南沙', 'Nansha', 'N', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440117', '从化区', 3, 10, 1, '广东省广州市从化区', '从化', 'Conghua', 'C', NOW(), NOW(), 0);

INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, full_name, short_name, pinyin, pinyin_prefix, create_time, update_time, deleted)
VALUES (@guangzhou_id, '440118', '增城区', 3, 11, 1, '广东省广州市增城区', '增城', 'Zengcheng', 'Z', NOW(), NOW(), 0);


-- =============================================
-- 数据统计
-- =============================================
-- 省级：34个
-- 市级：21个（广东省）
-- 区级：11个（广州市）
-- 总计：66条测试数据
-- =============================================
