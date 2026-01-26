# 行政区划数据导入说明

## 概述

本文档说明如何获取和导入中国行政区划数据（省市区街道四级）。

## 数据源

推荐使用 GitHub 开源项目：[Administrative-divisions-of-China](https://github.com/modood/Administrative-divisions-of-China)

该项目提供了最新的中国行政区划数据，包括：
- 省级（省、直辖市、自治区、特别行政区）
- 市级（地级市、自治州）
- 区级（市辖区、县级市、县、自治县）
- 街道级（街道、镇、乡）

## 快速测试

### 1. 使用测试数据

项目已提供简化的测试数据，包含：
- 全国 34 个省级行政区
- 广东省 21 个市级行政区
- 广州市 11 个区级行政区

**导入步骤：**

```bash
# 进入数据库
mysql -u root -p

# 选择数据库
use base_db;

# 执行测试数据脚本
source D:/workspace/base/backend/src/main/resources/db/region_data_sample.sql;

# 验证数据
SELECT level, COUNT(*) as count FROM sys_region GROUP BY level;
```

**预期结果：**
```
+-------+-------+
| level | count |
+-------+-------+
|     1 |    34 |
|     2 |    21 |
|     3 |    11 |
+-------+-------+
```

## 完整数据导入

### 方式一：下载 JSON 数据并转换

#### 1. 下载数据文件

访问 GitHub 仓库下载以下文件：

- **省市区三级数据**：`pca-code.json`
  - 下载地址：https://github.com/modood/Administrative-divisions-of-China/blob/master/dist/pca-code.json
  - 包含省、市、区三级数据，约 3000+ 条记录

- **省市区街道四级数据**：`pcas-code.json`
  - 下载地址：https://github.com/modood/Administrative-divisions-of-China/blob/master/dist/pcas-code.json
  - 包含省、市、区、街道四级数据，约 40000+ 条记录

#### 2. 数据格式说明

JSON 数据格式示例：

```json
[
  {
    "code": "110000",
    "name": "北京市"
  },
  {
    "code": "110100",
    "name": "市辖区"
  },
  {
    "code": "110101",
    "name": "东城区"
  }
]
```

#### 3. 使用转换工具

项目提供了 `RegionDataConverter` 工具类，可以将 JSON 数据转换为 SQL 语句。

**使用方法：**

```java
import com.base.system.util.RegionDataConverter;
import com.base.system.entity.Region;
import java.util.List;

public class RegionDataImportTest {
    public static void main(String[] args) {
        // 1. 读取 JSON 文件并转换为 Region 实体
        String jsonFilePath = "D:/data/pca-code.json";
        List<Region> regions = RegionDataConverter.convertFromJson(jsonFilePath);

        // 2. 生成 SQL 插入语句
        List<String> sqlList = RegionDataConverter.generateInsertSql(regions);

        // 3. 输出到文件
        try (FileWriter writer = new FileWriter("D:/data/region_data.sql")) {
            for (String sql : sqlList) {
                writer.write(sql + "\n");
            }
            System.out.println("SQL 文件生成成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### 4. 导入数据库

```bash
# 执行生成的 SQL 文件
mysql -u root -p base_db < D:/data/region_data.sql
```

### 方式二：使用后端导入接口

#### 1. 准备 JSON 数据

将下载的 JSON 文件放到服务器可访问的位置。

#### 2. 调用导入接口

```bash
# 使用 curl 调用导入接口
curl -X POST http://localhost:8080/system/region/import \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d @pca-code.json
```

#### 3. 查看导入结果

接口会返回导入的数据条数和结果信息。

**注意事项：**
- 导入接口需要 `system:region:import` 权限
- 大数据量导入建议分批进行（每批 1000 条）
- 导入前建议备份数据库

## 数据处理说明

### 1. 层级判断规则

根据区划代码（6位数字）判断层级：

- **省级**：后 4 位为 0000（如：110000 北京市）
- **市级**：后 2 但后 4 位不为 0000（如：110100 市辖区）
- **区级**：后 2 位不为 00（如：110101 东城区）

### 2. 父级关系处理

根据区划代码确定父级关系：

- **省级**：parentId = 0（顶级）
- **市级**：父级为省级，代码前 2 位相同（如：110100 的父级是 110000）
- **区级**：父级为市级，代码前 4 位相同（如：110101 的父级是 110100）

### 3. 拼音转换

工具类提供了常用省市区的拼音映射表，对于映射表中没有的数据：

- **方案一**：直接使用中文（简单但不支持拼音搜索）
- **方案二**：集成拼音库（如 pinyin4j）进行自动转换

**集成 pinyin4j 示例：**

```xml
<!-- pom.xml 添加依赖 -->
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.1</version>
</dependency>
```

```java
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public static String convertToPinyin(String chinese) {
    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

    StringBuilder pinyin = new StringBuilder();
    for (char c : chinese.toCharArray()) {
        if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
            if (pinyinArray != null && pinyinArray.length > 0) {
                pinyin.append(pinyinArray[0]);
            }
        } else {
            pinyin.append(c);
        }
    }
    return pinyin.toString();
}
```

## 数据维护

### 1. 更新数据

行政区划数据会定期调整，建议：

- 定期访问数据源仓库查看更新
- 使用增量更新方式，只更新变化的数据
- 保留历史数据，使用状态字段标记废弃的区划

### 2. 数据校验

导入后建议进行以下校验：

```sql
-- 1. 检查数据总数
SELECT COUNT(*) FROM sys_region WHERE deleted = 0;

-- 2. 检查各层级数据量
SELECT level, COUNT(*) as count FROM sys_region WHERE deleted = 0 GROUevel;

-- 3. 检查是否有孤立数据（父级不存在）
SELECT r1.* FROM sys_region r1
LEFT JOIN sys_region r2 ON r1.parent_id = r2.id
WHERE r1.parent_id != 0 AND r2.id IS NULL AND r1.deleted = 0;

-- 4. 检查区划代码唯一性
SELECT region_code, COUNT(*) as count FROM sys_region
WHERE deleted = 0 GROUP BY region_code HAVING count > 1;

-- 5. 检查拼音数据完整性
SELECT COUNT(*) FROM sys_region
WHERE (pinyin IS NULL OR pinyin = '') AND deleted = 0;
```

### 3. 性能优化

对于大数据量查询，建议：

```sql
-- 添加复合索引
CREATE INDEX idx_parent_level ON sys_region(parent_id, level);
CREATE INDEX idx_code_status ON sys_region(region_code, ;

-- 分析表
ANALYZE TABLE sys_region;
```

## 常见问题

### Q1: 导入数据后前端显示不正常？

**A:** 检查以下几点：
1. 确认 parent_id 关联关系正确
2. 确认 level 字段值正确（1-省、2-市、3-区、4-街道）
3. 确认 status 字段为 1（启用状态）
4. 清除浏览器缓存重新加载

### Q2: 数据量太大导致导入失败？

**A:** 建议分批导入：
1. 先导入省级数据
2. 再导入市级数据
3. 最后导入区级和街道级数据
4. 每批控制在 1000 条以内

### Q3: 如何处理特殊行政区划？

**A:** 特殊情况处理：
- 直辖市：市级数据的 parent_id 直接指向省级
- 县级市：作为市级数据处理
- 市辖区：可以保留或跳过（根据业务需求）

### Q4: 拼音数据不准确怎么办？

**A:** 解决方案：
1. 使用 pinyin4j 库自动转换
2. 手动维护常用地名的拼音映射表
3. 对于多音字，选择常用读音

## 附录

### A. 数据源更新记录

| 日期 | 版本 | 说明 |
|------|------|------|
| 2024-01 | v1.0 | 初始版本，基于 2023 年行政区划数据 |

### B. 相关链接

- [数据源仓//github.com/modood/Administrative-divisions-of-China)
- [国家统计局行政区划代码](http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/)
- [民政部行政区划代码](http://www.mca.gov.cn/article/sj/xzqh/)

### C. 联系方式

如有问题，请联系系统管理员或提交 Issue。
