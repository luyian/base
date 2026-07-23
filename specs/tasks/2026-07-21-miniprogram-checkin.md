# 小程序打卡功能 - 任务清单

- 提案：[proposals/2026-07-21-miniprogram-checkin.md](../proposals/2026-07-21-miniprogram-checkin.md)
- 设计：[designs/2026-07-21-miniprogram-checkin.md](../designs/2026-07-21-miniprogram-checkin.md)
- 规格：[specs/2026-07-21-miniprogram-checkin.md](../specs/2026-07-21-miniprogram-checkin.md)

## 后端

- [x] 任务1：新增建表 SQL `backend/src/main/resources/db/checkin.sql`（checkin_plan、checkin_record），并同步追加到 schema.sql
- [x] 任务2：新增权限初始化 SQL `backend/src/main/resources/db/init_checkin_permission.sql`（目录11 + 菜单110000 + 按钮110001~110005，授权角色1/2/4）
- [x] 任务3：新增实体 `CheckinPlan`、`CheckinRecord`，计划记录 `deletedTime` 以保留历史统计口径
- [x] 任务4：新增 Mapper `checkin/mapper/CheckinPlanMapper.java`、`CheckinRecordMapper.java`
- [x] 任务5：新增 DTO `CheckinPlanRequest`、`CheckinPlanResponse`、`CalendarDayResponse`
- [x] 任务6：新增 Service 接口与实现 `CheckinPlanService(Impl)`（list/add/update/delete，按 user_id 隔离，list 填充 todayChecked）
- [x] 任务7：新增 Service 接口与实现 `CheckinRecordService(Impl)`（toggle 原子 upsert + 按日有效计划统计）
- [x] 任务8：新增控制器 `checkin/controller/CheckinController.java`（6 个接口 + @PreAuthorize + SecurityUtils.getCurrentUserId()）
- [x] 任务9：补充打卡服务和请求参数异常单元测试，定向测试 7 项通过，后端 clean compile 通过

## 小程序前端

- [x] 任务10：新增 `minservice/api/checkin.js` 接口封装
- [x] 任务11：新增 `minservice/pages/checkin/checkin.{js,wxml,wxss,json}`（月历 + 计划卡片 + 新增/编辑/删除 + 打卡切换）
- [x] 任务12：`app.json` 注册页面与 tabBar 第5项"打卡"，新增 tab 图标 assets/checkin.png、checkin-active.png

## 收尾

- [x] 任务13：执行 `codegraph sync` 增量更新索引
- [x] 任务14：变更记录写入 `.claude/docs/TEMP.md`
- [x] 任务15：新增文件 `git add`（不 commit）
