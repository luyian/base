# 规格：小程序端商品管理页

## 版次
- 状态：待实现
- 来源：提案 `specs/proposals/2026-09-21-mp-product-manage.md`
- 用户确认：其他功能不改；入口改建（barcode 页 → 商品管理页）；列表全功能；绑定流程「先选商品再扫码」。

## 功能需求

### FR-1 商品列表
- 页面进入加载「我的商品」分页列表（`GET /prod/list?page&size`，默认 10 条）。
- 每行卡片：商品名称、编码、分类、库存；操作「编辑」「扫码绑定」「删除」。
- 触底加载下一页（`onReachBottom`），加载中显示 loading。

### FR-2 添加商品
- 顶部「＋添加商品」按钮 → 空白表单弹层，保存走 `POST /prod`（系统自动生成 PROD 编码）。
- 必填校验：商品名称非空。

### FR-3 编辑商品
- 来源一：列表行「编辑」→ 预填该行商品。
- 来源二：顶部「扫码」命中已绑定商品 → 预填该商品，顶部回显扫码 code 徽章。
- 保存走 `PUT /prod/{id}`。

### FR-4 扫码编辑
- 顶部大「扫码」→ `wx.scanCode`（barCode+qrCode）→ `GET /prod/scan/{code}`。
- 命中 → 进编辑表单；未命中 → toast「未命中商品」，提示用列表行「扫码绑定」。

### FR-5 扫码绑定（先选商品再扫码）
- 列表行「扫码绑定」→ `wx.scanCode` → 结果非空 → `POST /prod/{id}/bind`（type 按扫码码制：barCode=1 条码 / qrCode=2 二维码，source=1）。
- 成功 toast「绑定成功」并刷新列表；失败或取消不阻断。

### FR-6 删除商品
- 列表行「删除」→ `wx.showModal` 确认 → `DELETE /prod/{id}` → 刷新列表。

## 非功能需求
- 深/浅色主题适配，沿用 `--paper/--card/--ink/--seal` Design Token。
- 所有请求经 `utils/request.js`（自动带 token，401 跳登录）。
- 表单字段与 PC 端一致：名称*/分类/规格/单位/售价/成本价/库存/供应商/生产日期/备注。

## 接口契约（均已在后端存在，无改动）
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/prod/list` | 分页商品（name/page/size） |
| POST | `/prod` | 新增商品 |
| PUT | `/prod/{id}` | 编辑商品 |
| DELETE | `/prod/{id}` | 删除商品 |
| GET | `/prod/scan/{code}` | 扫码命中商品 |
| POST | `/prod/{id}/bind` | 绑定条码到商品（code/type/source，x-www-form-urlencoded） |

## 验收标准
- 商品列表正确分页展示；添加/编辑/删除生效并刷新。
- 顶部扫码命中已绑定商品可编辑；未命中给出提示。
- 列表行扫码绑定：先定商品再扫码，绑定成功列表条码数不变（列表不展示条码数）但接口成功。
- 主题正常、无回归到 PDF/去水印等其它功能。