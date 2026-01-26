package com.base.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.base.system.entity.Region;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行政区划数据转换工具类
 * 用于将 GitHub 开源数据转换为 Region 实体
 * 数据源：https://github.com/modood/Administrative-divisions-of-China
 */
@Slf4j
public class RegionDataConverter {

    /**
     * 拼音映射表（常用省市区名称）
     */
    private static final Map<String, String> PINYIN_MAP = new HashMap<>();

    static {
        // 省级拼音映射
        PINYIN_MAP.put("北京", "Beijing");
        PINYIN_MAP.put("天津", "Tianjin");
        PINYIN_MAP.put("河北", "Hebei");
        PINYIN_MAP.put("山西", "Shanxi");
        PINYIN_MAP.put("内蒙古", "Neimenggu");
        PINYIN_MAP.put("辽宁", "Liaoning");
        PINYIN_MAP.put("吉林", "Jilin");
        PINYIN_MAP.put("黑龙江", "Heilongjiang");
        PINYIN_MAP.put("上海", "Shanghai");
        PINYIN_MAP.put("江苏", "Jiangsu");
        PINYIN_MAP.put("浙江", "Zhejiang");
        PINYIN_MAP.put("安徽", "Anhui");
        PINYIN_MAP.put("福建", "Fujian");
        PINYIN_MAP.put("江西", "Jiangxi");
        PINYIN_MAP.put("山东", "Shandong");
        PINYIN_MAP.put("河南", "Henan");
        PINYIN_MAP.put("湖北", "Hubei");
        PINYIN_MAP.put("湖南", "Hunan");
        PINYIN_MAP.put("广东", "Guangdong");
        PINYIN_MAP.put("广西", "Guangxi");
        PINYIN_MAP.put("海南", "Hainan");
        PINYIN_MAP.put("重庆", "Chongqing");
        PINYIN_MAP.put("四川", "Sichuan");
        PINYIN_MAP.put("贵州", "Guizhou");
        PINYIN_MAP.put("云南", "Yunnan");
        PINYIN_MAP.put("西藏", "Xizang");
        PINYIN_MAP.put("陕西", "Shaanxi");
        PINYIN_MAP.put("甘肃", "Gansu");
        PINYIN_MAP.put("青海", "Qinghai");
        PINYIN_MAP.put("宁夏", "Ningxia");
        PINYIN_MAP.put("新疆", "Xinjiang");
        PINYIN_MAP.put("台湾", "Taiwan");
        PINYIN_MAP.put("香港", "Xianggang");
        PINYIN_MAP.put("澳门", "Aomen");
    }

    /**
     * 从 JSON 文件读取并转换为 Region 实体列表
     *
     * @param jsonFilePath JSON 文件路径
     * @return Region 实体列表
     */
    public static List<Region> convertFromJson(String jsonFilePath) {
        List<Region> regions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // 解析 JSON 数组
            JSONArray jsonArray = JSON.parseArray(jsonContent.toString());

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Region region = convertJsonToRegion(jsonObject);
                if (region != null) {
                    regions.add(region);
                       }

            log.info("成功转换 {} 条行政区划数据", regions.size());

        } catch (IOException e) {
            log.error("读取 JSON 文件失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("转换数据失败: {}", e.getMessage(), e);
        }

        return regions;
    }

    /**
     * 将 JSON 对象转换为 Region 实体
     *
     * @param jsonObject JSON 对象
     * @return Region 实体
     */
    private static Region convertJsonToRegion(JSONObject jsonObject) {
        try {
            Region region = new Region();

            // 区划代码
            String code = jsonObject.getString("code");
            if (code == null || code.isEmpty()) {
                return null;
            }
            region.setRegionCode(code);

            // 区划名称
            String name = jsonObject.getString("name");
            if (name == null || name.isEmpty()) {
                return null;
            }
            region.setRegionName(name);

            // 判断层级
            Integer level = determineLevel(code);
            region.setLevel(level);

            // 根据区划代码确定父级ID（需要在导入时处理）
            // 这里先设置为0，实际导入时需要查询父级区划的ID
            region.setParentId(0L);

            // 简称（去掉省、市、区、县等后缀）
            String shortName = removeAdministrativeSuffix(name);
            region.setShortName(shortName);

            // 拼音转换
            String pinyin = convertToPinyin(shortName);
            region.setPinyin(pinyin);

            // 拼音首字母
            String pinyinPrefix = getPinyinPrefix(pinyin);
            region.setPinyinPrefix(pinyinPrefix);

            // 全称（需要在导入时根据父级关系构建）
            region.setFullName(name);

            // 默认状态：启用
        region.setStatus(1);

            // 默认排序
            region.setSort(0);

            // 经纬度（如果 JSON 中有）
            if (jsonObject.containsKey("longitude")) {
                region.setLongitude(jsonObject.getBigDecimal("longitude"));
            }
            if (jsonObject.containsKey("latitude")) {
                region.setLatitude(jsonObject.getBigDecimal("latitude"));
            }

            return region;

        } catch (Exception e) {
            log.error("转换单条数据失败: {}", e.getMessage(), e);
            return null;
            /**
     * 根据区划代码判断层级
     * 规则：
     * - 省级：后4位为0000（如：110000）
     * - 市级：后2位为00，但后4位不为0000（如：110100）
     * - 区级：后2位不为00（如：110101）
     *
     * @param code 区划代码
     * @return 层级（1-省，2-市，3-区）
     */
    private static Integer determineLevel(String code) {
        if (code == null || code.length() != 6) {
            return 3; // 默认为区级
        }

        String last4 = code.substring(2);
        String last2 = code.substring(4);

        if ("0000".equals(last4)) {
            retur// 省级
        } else if ("00".equals(last2)) {
            return 2; // 市级
        } else {
            return 3; // 区级
        }
    }

    /**
     * 根据区划代码获取父级区划代码
     *
     * @param code 区划代码
     * @return 父级区划代码
     */
    public static String getParentCode(String code) {
        if (code == null || code.length() != 6) {
            return null;
        }

        Integer level = determineLevel(code);

        if (level == 1) {
            // 省级没有父级
            return null;
        } else if (level == 2) {
            // 市级的父级是省级
            return code.substring(0, 2) +00";
        } else {
            // 区级的父级是市级
            return code.substring(0, 4) + "00";
        }
    }

    /**
     * 去掉行政区划名称的后缀（省、市、区、县等）
     *
     * @param name 区划名称
     * @return 简称
     */
    private static String removeAdministrativeSuffix(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        // 去掉常见后缀
        String[] suffixes = {"省", "市", "区", "县", "自治区", "特别行政区", "自治州", "自治县"};
        for (String suffix : suffixes) {
            if (name.endsWith(suffix)) {
                return name.substring(0, name.length() - suffix.length());
            }
        }

        return name;
    }

    /**
     * 转换为拼音（使用映射表或直接使用中文）
     *
     * @param name 中文名称
     * @return 拼音
     */
    private static String convertToPinyin(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        // 优先从映射表获取
        if (PINYIN_MAP.containsKey(name)) {
            return PINYIN_MAP.get(name);
        }

        // 如果映射表中没有，返回中文（实际使用时可以集成拼音库）
        return name;
    }

    /**
     * 获取拼音首字母
     *
     * @param pinyin 拼音
     * @return 拼音首字母
     * private static String getPinyinPrefix(String pinyin) {
        if (pinyin == null || pinyin.isEmpty()) {
            return "";
        }

        // 如果是英文拼音，取首字母
        if (pinyin.matches("[a-zA-Z]+")) {
            return pinyin.substring(0, 1).toUpperCase();
        }

        // 如果是中文，返回空（实际使用时可以集成拼音库）
        return "";
    }

    /**
     * 生成 SQL 插入语句
     *
     * @param regions Region 实体列表
     * @return SQL 语句列表
     */
    public static List<String> generateInsertSql(List<Region> regions) {
        List<String> sqlList = new ArrayList<>();

        for (Region region : regions) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO sys_region (parent_id, region_code, region_name, level, sort, status, ");
            sql.append("full_name, short_name, pinyin, pinyin_prefix, longitude, latitude, ");
            sql.append("create_time, update_time, deleted) VALUES (");

            sql.append(region.getParentId()).append(", ");
            sql.append("'").append(region.getRegionCode()).append("', ");
            sql.append("'").append(region.getRegionName()).append("',             sql.append(region.getLevel()).append(", ");
            sql.append(region.getSort()).append(", ");
            sql.append(region.getStatus()).append(", ");
            sql.append("'").append(region.getFullName()).append("', ");
            sql.append("'").append(region.getShortName()).append("', ");
            sql.append("'").append(region.getPinyin()).append("', ");
            sql.append("'").append(region.getPinyinPrefix()).append("', ");

            if (region.getLongitude() != null) {
                sql.append(region.getLongitude());
            } else {
                sql.appendLL");
            }
            sql.append(", ");

            if (region.getLatitude() != null) {
                sql.append(region.getLatitude());
            } else {
                sql.append("NULL");
            }
            sql.append(", ");

            sql.append("NOW(), NOW(), 0);");

            sqlList.add(sql.toString());
        }

        return sqlList;
    }
}
