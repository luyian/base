# 菜单和按钮权限添加规范

## 一、权限表结构

### 1.1 权限表 `sys_permission`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID（按规则编号） |
| parent_id | bigint | 父级ID（0表示顶级） |
| permission_name | varchar(50) | 权限名称 |
| permission_code | varchar(100) | 权限编码（用于权限校验） |
| type | tinyint | 类型：1-目录，2-菜单，3-按钮 |
| path | varchar(200) | 路由地址（仅菜单需要） |
| component | varchar(200) | 组件路径（仅菜单需要） |
| icon | varchar(100) | 图标（仅目录/菜单需要） |
| sort | int | 排序号 |
| visible | tinyint | 是否可见：0-隐藏，1-显示 |
| status | tinyint | 状态：0-禁用，1-启用 |
| remark | varchar(500) | 备注 |

### 1.2 角色权限关联表 `sys_role_permission`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| role_id | bigint | 角色ID |
| permission_id | bigint | 权限ID |

### 1.3 角色ID说明

| role_id | 角色名称 | 说明 |
|---------|----------|------|
| 1 | 超级管理员 | 拥有所有权限 |
| 2 | 系统管理员 | 系统管理权限 |
| 3 | 普通用户 | 基础权限 |

---

## 二、ID 编号规则

### 2.1 一级目录（type=1）

| ID范围 | 模块 |
|--------|------|
| 1 | 系统管理 |
| 2 | 系统监控 |
| 3 | 系统工具 |
| 4 | 股票管理 |
| 5-99 | 预留 |

### 2.2 二级菜单（type=2）

**编号规则**：`父级ID + 两位序号`

| ID范围 | 说明 | 示例 |
|--------|------|------|
| 101-199 | 系统管理下的菜单 | 101用户管理、102角色管理 |
| 201-299 | 系统监控下的菜单 | 201在线用户、202操作日志 |
| 401-499 | 股票管理下的菜单 | 401股票列表、402自选股票 |

**股票模块已使用的菜单ID**：

| ID | 菜单名称 | 权限编码 |
|----|----------|----------|
| 401 | 股票列表 | stock:info:list |
| 402 | 自选股票 | stock:watchlist:list |
| 403 | Token管理 | stock:token:list |
| 404 | 数据映射 | stock:mapping:list |
| 405 | 股票推荐 | stock:recommend |
| 406 | 规则配置 | stock:recommend:rule |
| 407 | 基金估值 | stock:fund:list |
| 408+ | 预留 | - |

### 2.3 按钮权限（type=3）

**编号规则**：`父级菜单ID + 两位序号`

| ID范围 | 说明 | 示例 |
|--------|------|------|
| 40101-40199 | 股票列表下的按钮 | 40101股票查询、40102K线查询 |
| 40201-40299 | 自选股票下的按钮 | 40201自选查询、40202添加自选 |
| 40701-40799 | 基金估值下的按钮 | 40701基金查询、40702基金新增 |

---

## 三、权限编码规范

### 3.1 编码格式

```
模块:子模块:操作
```

### 3.2 常用操作动词

| 动词 | 说明 | 示例 |
|------|------|------|
| list | 列表/菜单权限 | stock:fund:list |
| query | 查询 | stock:fund:query |
| add | 新增 | stock:fund:add |
| edit | 编辑 | stock:fund:edit |
| delete | 删除 | stock:fund:delete |
| export | 导出 | stock:fund:export |
| import | 导入 | stock:fund:import |
| execute | 执行 | stock:sync:execute |

### 3.3 编码示例

```
# 菜单权限
stock:fund:list          # 基金估值菜单

# 按钮权限
stock:fund:query         # 基金查询
stock:fund:add           # 基金新增
stock:fund:edit          # 基金编辑
stock:fund:delete        # 基金删除
stock:fund:valuation     # 估值查询
```

---

## 四、添加权限步骤

### 4.1 确定权限类型和ID

1. **确定父级**：找到要挂载的父级菜单ID
2. **确定类型**：目录(1)、菜单(2)、按钮(3)
3. **分配ID**：按编号规则分配唯一ID
4. **定义编码**：按编码规范定义权限编码

### 4.2 编写 SQL 脚本

#### 添加菜单示例

```sql
-- 添加二级菜单
INSERT INTO `sys_permission` (
    `id`, `parent_id`, `permission_name`, `permission_code`,
    `type`, `path`, `component`, `icon`, `sort`,
    `visible`, `status`, `remark`,
    `create_time`, `create_by`, `update_time`, `update_by`, `deleted`
) VALUES (
    408,                          -- ID：按规则分配
    4,                            -- 父级ID：股票管理
    '新功能菜单',                  -- 菜单名称
    'stock:newfeature:list',      -- 权限编码
    2,                            -- 类型：菜单
    '/stock/newfeature',          -- 路由地址
    'stock/newfeature/index',     -- 组件路径
    'Document',                   -- 图标
    8,                            -- 排序
    1,                            -- 可见
    1,                            -- 启用
    '新功能描述',                  -- 备注
    NOW(), 'system', NOW(), NULL, 0
);
```

#### 添加按钮权限示例

```sql
-- 添加按钮权限
INSERT INTO `sys_permission` (
    `id`, `parent_id`, `permission_name`, `permission_code`,
    `type`, `path`, `component`, `icon`, `sort`,
    `visible`, `status`, `remark`,
    `create_time`, `create_by`, `update_time`, `update_by`, `deleted`
) VALUES
(40801, 408, '查询', 'stock:newfeature:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询列表', NOW(), 'system', NOW(), NULL, 0),
(40802, 408, '新增', 'stock:newfeature:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增数据', NOW(), 'system', NOW(), NULL, 0),
(40803, 408, '编辑', 'stock:newfeature:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑数据', NOW(), 'system', NOW(), NULL, 0),
(40804, 408, '删除', 'stock:newfeature:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除数据', NOW(), 'system', NOW(), NULL, 0);
```

#### 分配权限给超级管理员

```sql
-- 为超级管理员分配权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 408),
(1, 40801), (1, 40802), (1, 40803), (1, 40804);
```

### 4.3 后端添加权限注解

在 Controller 方法上添加 `@PreAuthorize` 注解：

```java
@RestController
@RequestMapping("/stock/newfeature")
public class NewFeatureController {

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:newfeature:query')")
    public Result<List<Entity>> list() {
        // ...
    }

    @PostMapping
    @PreAuthorize("hasAuthority('stock:newfeature:add')")
    public Result<Void> add(@RequestBody Entity entity) {
        // ...
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:newfeature:edit')")
    public Result<Void> edit(@PathVariable Long id, @RequestBody Entity entity) {
        // ...
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:newfeature:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        // ...
    }
}
```

### 4.4 前端按钮权限控制（可选）

在 Vue 组件中使用 `v-hasPermi` 指令：

```vue
<template>
  <div>
    <el-button v-hasPermi="['stock:newfeature:add']" @click="handleAdd">
      新增
    </el-button>
    <el-button v-hasPermi="['stock:newfeature:edit']" @click="handleEdit">
      编辑
    </el-button>
    <el-button v-hasPermi="['stock:newfeature:delete']" @click="handleDelete">
      删除
    </el-button>
  </div>
</template>
```

---

## 五、完整示例

### 5.1 新增「基金估值」功能的权限配置

```sql
-- =============================================
-- 基金估值模块 - 菜单和权限初始化
-- =============================================

-- 1. 添加菜单
INSERT INTO `sys_permission` VALUES
(407, 4, '基金估值', 'stock:fund:list', 2, '/stock/fund', 'stock/fund/index', 'PieChart', 7, 1, 1, '基金估值管理', NOW(), 'system', NOW(), NULL, 0);

-- 2. 添加按钮权限
INSERT INTO `sys_permission` VALUES
(40701, 407, '基金查询', 'stock:fund:query', 3, NULL, NULL, NULL, 1, 1, 1, '查询基金列表', NOW(), 'system', NOW(), NULL, 0),
(40702, 407, '基金新增', 'stock:fund:add', 3, NULL, NULL, NULL, 2, 1, 1, '新增基金配置', NOW(), 'system', NOW(), NULL, 0),
(40703, 407, '基金编辑', 'stock:fund:edit', 3, NULL, NULL, NULL, 3, 1, 1, '编辑基金配置', NOW(), 'system', NOW(), NULL, 0),
(40704, 407, '基金删除', 'stock:fund:delete', 3, NULL, NULL, NULL, 4, 1, 1, '删除基金配置', NOW(), 'system', NOW(), NULL, 0),
(40705, 407, '估值查询', 'stock:fund:valuation', 3, NULL, NULL, NULL, 5, 1, 1, '查询实时估值', NOW(), 'system', NOW(), NULL, 0);

-- 3. 分配给超级管理员
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 407),
(1, 40701), (1, 40702), (1, 40703), (1, 40704), (1, 40705);
```

---

## 六、常用图标参考

| 图标名称 | 适用场景 |
|----------|----------|
| List | 列表 |
| Document | 文档 |
| Setting | 设置 |
| User | 用户 |
| Key | 密钥/Token |
| Star | 收藏/自选 |
| TrendCharts | 图表/趋势 |
| PieChart | 饼图/统计 |
| Connection | 连接/映射 |
| Monitor | 监控 |
| Timer | 定时/日志 |
| Tools | 工具 |

---

## 七、检查清单

添加新功能权限时，请确认以下事项：

- [ ] ID 是否按规则分配且未被占用
- [ ] 是否符合规范
- [ ] 菜单的 path 和 component 是否正确
- [ ] 是否为超级管理员分配了权限
- [ ] 后端 Controller 是否添加了 @PreAuthorize 注解
- [ ] SQL 文件是否放在 `backend/src/main/resources/db/` 目录
- [ ] 是否更新了本文档的「已使用ID」列表

---

## 八、SQL 文件命名规范

```
{模块名}_permission.sql
```

示例：
- `stock_permission.sql` - 股票模块权限
- `fund_permission.sql` - 基金模块权限
- `recommend_permission.sql` - 推荐模块权限
