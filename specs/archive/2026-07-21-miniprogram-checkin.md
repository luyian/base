# 小程序打卡功能归档

## 状态

已完成。

## 结果

- 后端新增 `com.base.checkin` 包，提供 6 个接口：
  - `GET /checkin/plan/list` 计划列表（含今日打卡状态）
  - `POST /checkin/plan` 新增计划、`PUT /checkin/plan/{id}` 编辑、`DELETE /checkin/plan/{id}` 删除
  - `POST /checkin/record/toggle` 打卡/取消（事务内原子 upsert，规避并发唯一键冲突）
  - `GET /checkin/calendar?month=yyyy-MM` 月完成度概览（completed/total）
- 用户隔离：全部接口经 `SecurityUtils.getCurrentUserId()` 强制按 `user_id` 过滤；写操作校验归属防水平越权。
- 数据表：`checkin_plan`（计划模板，记录 `deleted_time`）、`checkin_record`（每日打卡记录，`uk_plan_date` 唯一）；日历按每天有效计划数保留历史统计口径。
- 权限：新增隐藏菜单权限码 `checkin:*`（ID 110000~110005），授权角色 1/2/4，避免与既有导出任务权限冲突。
- 小程序端：新增底部"打卡" Tab（`pages/checkin/checkin`），单页含月历（完成度颜色深浅 + x/y）与计划卡片网格（点击打卡、长按编辑/删除、新增弹窗）；新增 `api/checkin.js` 与 tab 图标。

## 验证

- `mvn -s "C:\install\apache-maven-3.5.4\conf\settings.xml" clean compile`（后端编译通过，checkin 各 .class 生成）
- `mvn test -s "C:\install\apache-maven-3.5.4\conf\settings.xml" "-DskipTests=false" "-Dtest=CheckinRecordServiceImplTest,GlobalExceptionHandlerTest"`（7 项测试通过）
- `codegraph sync`（已增量更新索引）
- app.json / checkin.json JSON 语法校验通过

## 待用户执行

- 数据库执行 `db/checkin.sql` 与 `db/init_checkin_permission.sql`（本项目数据库脚本不自动迁移，需手动执行）
- 如需替换 tab 图标为正式设计稿，覆盖 `minservice/assets/checkin.png`、`checkin-active.png` 即可
