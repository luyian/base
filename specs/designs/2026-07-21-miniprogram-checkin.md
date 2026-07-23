# 小程序打卡功能 - 设计

## 架构链路

```text
小程序 pages/checkin/checkin
   │  (utils/request.js，自动带 Bearer token)
   ▼
api/checkin.js  ──►  backend com.base.checkin.controller.CheckinController
                          │ @PreAuthorize("hasAuthority('checkin:xxx')")
                          │ SecurityUtils.getCurrentUserId()  ← 用户隔离关键
                          ▼
                     CheckinPlanService / CheckinRecordService (impl)
                          ▼
                     CheckinPlanMapper / CheckinRecordMapper (MyBatis-Plus)
                          ▼
                     checkin_plan / checkin_record  (按 user_id 过滤)
```

## 后端包结构（新增 com.base.checkin）

```text
com.base.checkin/
├── controller/
│   └── CheckinController.java          # /checkin 下所有接口
├── service/
│   ├── CheckinPlanService.java
│   ├── CheckinRecordService.java
│   └── impl/
│       ├── CheckinPlanServiceImpl.java
│       └── CheckinRecordServiceImpl.java
├── mapper/
│   ├── CheckinPlanMapper.java
│   └── CheckinRecordMapper.java
├── entity/
│   ├── CheckinPlan.java                # 继承 com.base.entity.BaseEntity
│   └── CheckinRecord.java              # 继承 BaseEntity
└── dto/
    ├── CheckinPlanRequest.java         # 新增/编辑计划入参
    ├── CheckinPlanResponse.java        # 计划 + todayChecked
    └── CalendarDayResponse.java        # {date, total, completed}
```

## 数据库设计

### checkin_plan（打卡计划模板表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK 自增 | 主键 |
| user_id | bigint NOT NULL | 用户ID（隔离维度），索引 idx_user_id |
| title | varchar(50) NOT NULL | 计划名称 |
| icon | varchar(20) | 图标（emoji） |
| color | varchar(20) | 卡片颜色（hex） |
| remark | varchar(200) | 备注 |
| sort_order | int default 0 | 排序号，越小越靠前 |
| status | tinyint default 1 | 1启用 0停用 |
| deleted_time | datetime | 计划删除时间，用于还原历史日历统计口径 |
| create_time/create_by/update_time/update_by/deleted | — | 公共审计列（对应 BaseEntity） |

### checkin_record（每日打卡记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK 自增 | 主键 |
| plan_id | bigint NOT NULL | 计划ID |
| user_id | bigint NOT NULL | 用户ID（冗余，便于按人查日历） |
| checkin_date | date NOT NULL | 打卡日期 |
| create_time/.../deleted | — | 公共审计列 |

约束：
- `UNIQUE KEY uk_plan_date (plan_id, checkin_date)` —— 同一计划同一天只有一条记录。
- `KEY idx_user_date (user_id, checkin_date)` —— 日历按人按月查询。

> 表前缀规范：项目按模块分前缀（sys_/stk_/msg_ 等），打卡属小程序通用个人功能，采用 `checkin_` 前缀。

## 关键业务逻辑

### 打卡/取消切换（toggle）

由于 `uk_plan_date` 唯一 + 逻辑删除，toggle 使用事务内原子 upsert，避免并发查询后插入造成唯一键冲突：

```text
事务内锁定计划并校验归属：
  ├─ INSERT ... ON DUPLICATE KEY UPDATE deleted = IF(deleted=0, 1, 0)
  ├─ 不存在时插入 deleted=0
  └─ 已存在时原子翻转 deleted，再查询并返回最终状态
```

操作前校验该 plan 属于当前 user_id，防止越权。

### 日历完成度（calendar）

入参 `month=yyyy-MM`：
1. 查询当前用户全部启用计划（包含已逻辑删除数据）。
2. 每一天的 `total` = 创建日期不晚于当天，且删除日期不早于当天的计划总数。
3. 查该用户该月 `deleted=0` 的所有打卡记录，按 `checkin_date` 分组计数得 `completed`。
4. 组装当月每一天 `{date, total, completed}` 数组返回（前端渲染颜色深浅 / x/y）。

### 计划列表（list）

返回当前用户计划（按 sort_order、id 排序），并对每个计划查询今日是否已有 `deleted=0` 记录，填充 `todayChecked`。

## 权限设计（新增隐藏菜单承载权限码）

参照 `init_gaokao_permission.sql`，新增增量 SQL `db/init_checkin_permission.sql`：

| id | parent_id | 名称 | code | type | visible |
|----|-----------|------|------|------|---------|
| 11 | 0 | 打卡管理 | NULL | 1目录 | 0隐藏 |
| 110000 | 11 | 打卡计划 | checkin:plan:list | 2菜单 | 0隐藏 |
| 110001 | 110000 | 新增计划 | checkin:plan:add | 3按钮 | 0 |
| 110002 | 110000 | 编辑计划 | checkin:plan:edit | 3按钮 | 0 |
| 110003 | 110000 | 删除计划 | checkin:plan:delete | 3按钮 | 0 |
| 110004 | 110000 | 打卡/取消 | checkin:record:toggle | 3按钮 | 0 |
| 110005 | 110000 | 日历查询 | checkin:calendar:view | 3按钮 | 0 |

授权：上述权限插入 `sys_role_permission`，角色 1（超管）、2（系统管理员）、4（小程序用户）。`ON DUPLICATE KEY UPDATE` 保证可重复执行。

> visible=0 表示不在 Web 菜单显示，仅作为后端 `@PreAuthorize` 权限码载体。

## 小程序前端设计

### app.json 改动

- `pages` 数组新增 `"pages/checkin/checkin"`。
- `tabBar.list` 新增第 5 项：`{ "pagePath": "pages/checkin/checkin", "text": "打卡", "iconPath": "assets/checkin.png", "selectedIconPath": "assets/checkin-active.png" }`。
- 新增两张 tab 图标 `assets/checkin.png` / `assets/checkin-active.png`（实现时生成简洁日历图标，可先占位后续替换）。

### 页面 pages/checkin/checkin（js/wxml/wxss/json）

布局（沿用现有卡片 + nav-bar + 深浅主题 `themeClass`）：

```text
<nav-bar title="打卡" />
┌─ 月历区 ─────────────────┐
│  < 2026年7月 >            │  切换上/下月
│  日 一 二 三 四 五 六      │
│  [日期格子] 每格显示:       │
│   日期号 + 完成度(如 3/5   │
│   或颜色深浅圆点)           │
└──────────────────────────┘
┌─ 今日计划 ─────────────────┐
│ [卡片][卡片]  卡片网格排列   │
│  每张卡: icon + title      │
│  今日已打卡→高亮+✓         │
│  点击卡片→toggle 打卡       │
│  长按→编辑/删除菜单         │
└──────────────────────────┘
   [+ 新增计划] → 弹窗(title/icon/color)
```

### api/checkin.js 封装

```js
getPlanList()                          → GET  /checkin/plan/list
createPlan(data)                       → POST /checkin/plan
updatePlan(id, data)                   → PUT  /checkin/plan/{id}
deletePlan(id)                         → DELETE /checkin/plan/{id}
toggleRecord(planId, date)             → POST /checkin/record/toggle
getCalendar(month)                     → GET  /checkin/calendar
```

## 复用与约定

- 用户隔离：Controller 统一 `SecurityUtils.getCurrentUserId()`（`com.base.system.util.SecurityUtils`），Service/查询一律带 `user_id` 条件。
- 响应封装：`Result.success(...)`（`com.base.common.result.Result`）。
- 实体：继承 `com.base.entity.BaseEntity` 自动填充审计列 + 逻辑删除。
- 前端请求：`utils/request.js` 自动携带 token、401 跳登录。
- 主题：页面 `applyTheme()` 读取 `app.getTheme()` 输出 `dark-theme/light-theme`。
