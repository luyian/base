# 代码修改记录

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
