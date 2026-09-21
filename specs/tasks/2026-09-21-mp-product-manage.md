# 任务清单：小程序端商品管理页

- [x] T1 改建 `pages/barcode/barcode.js`：商品列表加载+触底分页、添加/编辑/删除、扫码命中编辑、行内扫码绑定（先选商品）、状态管理
- [x] T2 改建 `pages/barcode/barcode.wxml`：列表卡片区 + 添加/编辑表单弹层 + 扫码主按钮结构
- [x] T3 改建 `pages/barcode/barcode.wxss`：列表/表单/弹层样式（沿用 Design Token，深浅主题）
- [x] T4 首页入口（`pages/index/index.*`）：「条码/二维码」→「商品管理」，图标/文案语义对齐
- [x] T5 语法校验（`node --check` 通过）+ 逻辑走查（含 onRowEdit/noop/TYPE 码制、dataset 传参）

状态：已完成