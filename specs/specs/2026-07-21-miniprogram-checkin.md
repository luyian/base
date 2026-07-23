# 小程序打卡功能 - 规格

## 功能规格

1. 用户可在小程序"打卡" Tab 查看自己的每日计划，以卡片形式排列。
2. 用户可新增计划（名称必填，可选图标 emoji、卡片颜色、备注）。
3. 用户可编辑、删除自己的计划；删除计划仅标记计划的删除时间，保留历史打卡记录与历史统计口径。
4. 用户每天可对某计划点击完成打卡，再次点击取消；同一天同一计划仅一条有效记录。
5. 月历展示当月每天完成度（完成数/当日有效计划总数），支持切换月份。
6. 所有数据严格按用户隔离，任何接口只能读写当前登录用户的数据。

## API 规格

统一前缀 `/checkin`，响应统一 `Result`（`{code, message, data}`，code=200 成功）。所有接口需认证（Bearer token），并按权限点控制。

### GET /checkin/plan/list

查询我的计划列表（含今日打卡状态）。

- 权限：`checkin:plan:list`
- 入参：无
- 响应 data：`CheckinPlanResponse[]`

```json
[
  {
    "id": 1,
    "title": "背单词",
    "icon": "📚",
    "color": "#3B82F6",
    "remark": "每天30个",
    "sortOrder": 0,
    "status": 1,
    "todayChecked": true
  }
]
```

### POST /checkin/plan

新增计划。

- 权限：`checkin:plan:add`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 计划名称，≤50字 |
| icon | string | 否 | emoji |
| color | string | 否 | hex 颜色，缺省给默认色 |
| remark | string | 否 | 备注，≤200字 |

- 响应 data：新计划 `id`
- 校验失败：code=400，message 说明原因

### PUT /checkin/plan/{id}

编辑计划（仅本人）。

- 权限：`checkin:plan:edit`
- 请求体：同新增（title 必填）
- 响应 data：`true`
- 计划不存在或非本人：code=403/404

### DELETE /checkin/plan/{id}

删除计划（仅本人，逻辑删除）。

- 权限：`checkin:plan:delete`
- 响应 data：`true`

### POST /checkin/record/toggle

打卡/取消打卡切换。

- 权限：`checkin:record:toggle`
- 请求参数：`planId`（long，必填）、`date`（yyyy-MM-dd，必填，通常为当天）
- 行为：见设计"事务内原子 upsert"策略
- 响应 data：`{ "checked": true }`（切换后的状态）
- 计划非本人：code=403

### GET /checkin/calendar

查询某月完成度概览。

- 权限：`checkin:calendar:view`
- 请求参数：`month`（yyyy-MM，必填）
- 响应 data：`CalendarDayResponse[]`（当月每一天）

```json
[
  { "date": "2026-07-01", "total": 3, "completed": 2 },
  { "date": "2026-07-02", "total": 3, "completed": 3 }
]
```

## 业务规则

1. 计划为每日复用模板，不归属某一天；`total` 取当日有效计划总数，创建日与删除日均计入。
2. 同一 `(plan_id, checkin_date)` 物理唯一；通过数据库原子 upsert 切换逻辑删除标志。
3. `completed` 只统计 `deleted=0` 的打卡记录。
4. 所有写操作先校验记录/计划的 `user_id` 等于当前登录用户，否则拒绝。
5. 日期、月份参数做格式校验，缺失或非法均返回 400。

## 安全要求

- 全部接口需登录（JWT），未认证返回 401。
- 接口按权限点 `@PreAuthorize` 控制，无权限返回 403。
- 查询与写入强制 `user_id = 当前用户`，杜绝水平越权。
- 所有 SQL 走 MyBatis-Plus 参数绑定，无拼接注入风险。
- 入参（title 长度、日期格式、id 归属）做服务端校验。

## 前端交互规格

- 新增"打卡"底部 Tab，进入 `pages/checkin/checkin`。
- 页面分两段：上方月历（可切换月份，每格显示日期号与完成度，完成度用颜色深浅或 x/y 文本表示），下方"今日计划"卡片网格。
- 点击计划卡片：调用 toggle，卡片即时切换"已完成（高亮+✓）/未完成"样式。
- 长按卡片：弹出"编辑 / 删除"操作菜单。
- 页面右上或底部提供"+ 新增计划"，弹出表单（名称/图标/颜色/备注）。
- 下拉刷新重新拉取计划列表与当月日历。
- 适配深色/浅色主题（`themeClass`）。
