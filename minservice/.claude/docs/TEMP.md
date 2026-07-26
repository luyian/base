# 代码修改记录

## 2026-07-26 打卡页面视觉重设计（印章主题）+ 日历完成数統计修复

### 修改的功能模块
- 小程序打卡页面（`minservice/pages/checkin/checkin.wxml`、`checkin.js`、`checkin.wxss` 全量重写样式）
- 后端（`CheckinRecordServiceImpl.java`、`CheckinPlanServiceImpl.java`）

### 业务逻辑变更说明
1. **打卡页面视觉重设计（frontend-design）**
   - 设计概念：打卡即盖章。瓷青纸面 `#F1F3EF` + 墨绿黑 `#22302A`，朱砂红 `#C6402E` 仅用于「已完成」语义
   - 签名元素：完成计划右侧盖下歪 8° 的朱砂方章（stamp-in 回弹动效）；未完成为虚线空章位
   - 日历全勤日显示为不规则圆形朱砂章；月份用大号 DIN 数字做版面主角
   - 计划色板换为中式色（朱砂/鎏金/松绿/黛蓝/青莲/胭脂/青瓷/墨灰），后端默认色同步改为 `#C6402E`
   - 进场淡升 stagger 动效，respect prefers-reduced-motion；暗色主题同步适配
   - 功能无变更：所有事件绑定（点击/长按日历、打卡、编辑、弹窗）保持原样
2. **修复日历"4/3"统计问题（completed 口径）**
   - 原因：已删除计划的历史打卡记录仍被计入当天 completed
   - 修复：`getCalendar` 统计 completed 时按 `isEffectiveOn` 过滤，只计打卡当天计划仍生效的记录，与 total 口径一致

### 与其他模块的关联影响
- 无关联影响

## 2026-07-24 小程序打卡日历点击查看详情

### 修改的功能模块
- 小程序打卡页面（`minservice/pages/checkin/checkin.wxml`、`checkin.js`、`checkin.wxss`）
- 后端 Controller 和 Service（`CheckinController.java`、`CheckinPlanService.java`、`CheckinPlanServiceImpl.java`）
- 前端 API（`minservice/api/checkin.js`）
- MyBatis 配置（`MybatisPlusConfig.java`）

### 业务逻辑变更说明
1. **点击日历日期查看当天计划**
   - 点击日历日期后，下方列表切换为该日期的计划
   - 标题显示"X月X日计划"，右侧显示"返回今日"
   - 再次点击同一日期或点击"返回今日"恢复今日计划
   - 选中日期在日历上高亮显示

2. **新增查询指定日期计划接口**
   - 后端新增 `GET /checkin/plan/list/{date}` 接口
   - 前端新增 `getPlanListByDate(date)` API 方法

3. **修复启动失败问题**
   - `MybatisPlusConfig` 的 `@MapperScan` 缺少 `com.base.checkin.mapper`，已添加

### 与其他模块的关联影响
- 无关联影响

## 2026-07-23 小程序打卡功能优化

### 修改的功能模块
- 小程序打卡页面（`minservice/pages/checkin/checkin.wxml`、`checkin.js`、`checkin.wxss`）
- 后端打卡计划实体和服务（`CheckinPlan.java`、`CheckinPlanRequest.java`、`CheckinPlanResponse.java`、`CheckinPlanServiceImpl.java`、`CheckinRecordServiceImpl.java`）
- 数据库建表脚本（`checkin.sql`）

### 业务逻辑变更说明
1. **新增单日事件功能**
   - 长按日历日期可添加单日事件（只能添加今天及未来的日期）
   - `checkin_plan` 表新增 `plan_type`（0长期计划/1单日事件）和 `target_date` 字段
   - 单日事件只在目标日期生效，不会出现在其他日期的计划列表中

2. **修复弹框点击后立即关闭的问题**
   - 原因：`catchtap=""` 绑定空字符串无法正确阻止事件冒泡
   - 修复：改为 `catchtap="preventBubble"` 并添加空函数

3. **修复删除计划后日历统计不准确的问题**
   - 原因：删除当天的计划仍被计入总数
   - 修复：将 `!date.isAfter(deletedTime.toLocalDate())` 改为 `date.isBefore(deletedTime.toLocalDate())`

4. **计划列表改为长条形布局**
   - 从卡片网格改为单行列表，左侧图标+信息，右侧状态按钮
   - 左侧彩色边框标识计划颜色

### 数据库变更
```sql
ALTER TABLE checkin_plan 
ADD COLUMN plan_type tinyint(1) NOT NULL DEFAULT '0' COMMENT '计划类型（0长期计划 1单日事件）' AFTER remark,
ADD COLUMN target_date date DEFAULT NULL COMMENT '目标日期（仅单日事件）' AFTER plan_type,
ADD INDEX idx_target_date (target_date);
```

### 与其他模块的关联影响
- 无关联影响

## 2026-06-23 小程序首页自选基金置顶功能

### 修改的功能模块
- 小程序首页（`minservice/pages/index/index.wxml`、`index.js`、`index.wxss`）
- 小程序基金 API（`minservice/api/fund.js`）

### 业务逻辑变更说明
1. **新增自选基金长按操作交互**
   - 「我的自选」卡片绑定 `bindlongpress`，长按浮现深色玻璃操作层：左侧保留基金上下文（操作 / 基金名），右侧两个操作块（置顶 / 删除）依次从右滑入
   - 「置顶」调用 `topWatchlist` 接口，成功后 toast 提示并重新加载列表
   - 「删除」弹确认框，确认后调用 `removeFromWatchlist` 取消自选，成功后刷新列表
   - 点击空白蒙层（`top-mask`）关闭浮层；浮层激活时单击卡片只关闭浮层，不跳转详情
2. **API 接口**
   - `api/fund.js` 新增 `topWatchlist(fundId)`，对应后端 `PUT /stock/fund/watchlist/{fundId}/top`
   - 删除复用已有的 `removeFromWatchlist(fundId)`（`DELETE /stock/fund/watchlist/{fundId}`）

### 与其他模块的关联影响
- 依赖后端 `stk_fund_watchlist` 新增的 `sort_order` 字段及置顶接口
- 自选列表排序由后端按 `sort_order` 返回，前端无需额外排序

## 2026-05-06 小程序基金编辑页面权重输入修复

### 修改的功能模块
- 小程序基金编辑页面（`minservice/pages/fund/edit.wxml`、 `minservice/pages/fund/edit.js`）

### 业务逻辑变更说明
1. **修复权重输入框无法输入小数的问题**
   - 原因：权重输入框使用 `type="number"`，在微信小程序中会唤起不带小数点的数字键盘
   - 修复：将 `type="number"` 改为 `type="digit"`，唤起带小数点的数字键盘
2. **修复小数点输入后被立即删除的问题**
   - 原因：`onWeightChange` 中使用 `parseFloat` 处理输入值，输入 `"1."` 时被转成数字 `1`，小数点被吞掉
   - 修复：`onWeightChange` 中保留原始字符串值，仅在提交时通过 `parseFloat` 转换为数字
   - 影响范围：基金创建/编辑页面的持仓权重输入与提交

### 与其他模块的关联影响
- 无关联影响

## 2026-04-07 小程序股票编辑弹窗修复

### 修改的功能模块
- 小程序股票列表页面（`minservice/pages/stock/`）

### 业务逻辑变更说明

1. **修复编辑/新增弹窗表单无法交互的问题**
   - 原因：`modal-mask` 和 `modal` 是兄弟元素，mask 覆盖全屏拦截了表单的触摸事件
   - 修复：将 modal 嵌入 mask 内部作为子元素，modal 使用 `catchtap=""` 阻止事件冒泡到 mask
   - 同步调整 CSS，modal 移除 `position: fixed` 定位，改为由 mask 的 flex 布局居中

2. **股票代码字段改为可编辑**
   - 编辑弹窗中的股票代码字段移除了 `disabled` 属性和 `disabled-input` 样式类
   - 新增 `onEditStockCodeChange` 事件处理方法
   - 新增 `originalStockCode` 字段保存原始股票代码，用于调用更新接口
   - 提交时校验股票代码和名称均不为空

3. **后端 updateStock 支持修改股票代码**
   - `StockServiceImpl.updateStock()` 新增股票代码变更逻辑
   - 修改前检查新代码是否已被其他股票占用，避免重复
   - 通过 `id`（主键）更新记录，`stockCode` 作为普通字段可修改

### 与其他模块的关联影响
- 修改股票代码可能影响 K 线数据、自选股等关联表中使用 `stockCode` 的记录
