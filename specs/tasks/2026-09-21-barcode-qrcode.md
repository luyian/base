# 任务清单：通用条码/二维码能力层 + 商品管理场景

- 提案：proposals/2026-09-21-barcode-qrcode.md
- 规格：specs/2026-09-21-barcode-qrcode.md

## 任务列表

### 阶段一：通用能力层（先做，独立可测）
- [ ] 任务1：建表脚本。新增 `db/barcode_product.sql`：`t_barcode`（含 biz_type/biz_id/source/type 唯一 code）+ `t_product`（价库字段）。
- [ ] 任务2：后端 `com.base/barcode` 模块骨架。pom 引入 ZXing；entity/mapper/service/controller（generate/code-record/按 code 查询）。
- [ ] 任务3：`POST /barcode/generate` 生成条码 PNG（CODE128 / QR）。
- [ ] 任务4：python-tools 新增 `barcode_service.py` + `routers/barcode.py`（`/api/barcode/decode-image` 识别图片返回原始内容；按 Windows 挑选识别库），后端 `PythonToolsClient` 转发封装 decode。
- [ ] 任务5：权限码 `common:barcode:use` + `db/init_product_permission.sql`（授权角色 1/2/4）。

### 阶段二：商品管理
- [ ] 任务6：`com.base/product` 模块。entity/mapper/service/controller：list/create/edit/delete/codes/scan。
- [ ] 任务7：商品业务编码生成（`PROD+日期+递增`，唯一兜底）。
- [ ] 任务8：`/prod/scan/{code}` 扫码命中链路：编码→t_barcode→商品；未命中返回可新建标记。

### 阶段三：PC 端界面（frontend，仅数据管理 + 生成条码，不做扫码）
- [ ] 任务9：`views/product/ProductList.vue`（列表 + 来源列 + 新增/编辑/删除；不含扫码/上传识别入口）。
- [ ] 任务10：`views/product/ProductEdit.vue`（价库字段 + 生成条形码/二维码 PNG 供下载/打印贴标 + 条码来源列表）。
- [ ] 任务11：路由 + 菜单，前端调通。

### 阶段四：小程序界面（ling-tools，仅扫码 + 编辑表单，不生成码）
- [ ] 任务12：`api/product.js` 封装（扫码命中/商品编辑）；首页新增「条码」宫格。
- [ ] 任务13：`pages/barcode/barcode`（`wx.scanCode` 扫码命中商品 → 显示商品 + 编辑表单 → 保存；未命中新建并绑定该码；**不提供生成码/图片识别入口**）。
- [ ] 任务14：验证 PC 与小程序全链路一致性。

## 验收标准
- 通用层接口独立可用（generate/decode-image/code-record 不依赖商品），便于未来复用其他业务。
- 生成条码 PNG 双端可展示/导出/存相册；二维码与条形码均可。
- 识别图片二维码/条码返回原始内容；失败明确报错不静默。
- 商品：PC 端生成条码→生成/保存商品（含价库字段）→小程序扫码/识别→命中查看或编辑→保存→列表可见；已有二维码归属后来源标注「图片识别」。
- PC 端仅数据管理 + 生成条码，无扫码；小程序端扫码与图片识别；两端共用后端。
- 商品数据按用户隔离；商品登录即用（不上菜单权限）；通用层权限码生效；后端零 SDK 冲突。
- 小程序仅扫码 + 编辑，不展示生成码/图片识别入口。
- 双端共用同一后端；符合阿里巴巴规范。