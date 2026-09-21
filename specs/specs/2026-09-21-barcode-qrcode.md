# 规格：通用条码/二维码能力层 + 商品管理场景

- 提案：proposals/2026-09-21-barcode-qrcode.md

## 概述

两层架构：

```
通用能力层（与业务解耦，可复用于任意端）
├── 生成：POST /barcode/generate  → 内容+码制 → 条码 PNG（后端 Java ZXing）
├── 识别：POST /barcode/decode-image → 图片 → 原始条码/二维码内容（python-tools 图像库）
└── 线上记录：t_barcode 通用表（biz_type + biz_id 关联任意业务）

业务层（首个落地场景）
└── 商品：t_product + 关联 t_barcode(biz_type=PRODUCT) + 价库字段 + 来源区分
```

## 一、通用能力层

### 1. 生成条码 `POST /barcode/generate`

- 请求：`{ content: string, type: CODE128 | QR }`（后端规范化）
- 返回：条码 PNG 二进制（可直接 `<img>` / `wx.saveImageToPhotosAlbum`）
- 实现：后端新增 `com.base/barcode` 模块，引入 `ZXing`（Java）渲染；`CODE128` 编码条码、`QR` 编码二维码
- 说明：不校验 content 是否对应任何业务记录；纯编码输出，彻底解耦

### 2. 识别图片条码 `POST /barcode/decode-image`

- 落点：python-tools，新增 `app/routers/barcode.py` + `app/services/barcode_service.py`
- 请求：multipart 图片
- 返回：`{ content: string }`（识别到的原始内容）或解码失败错误
- 说明：**只返回识别内容，不决定命中逻辑**；商品层拿内容去查 t_barcode / 询问用户归属
- 依赖：新增图像识别库（实施时按 Windows 环境挑选 pyzbar / opencv-python / 其它纯 python 实现）

### 3. 通用条码记录表 `t_barcode`

```sql
CREATE TABLE IF NOT EXISTS `t_barcode` (
  `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code`        varchar(64)  NOT NULL COMMENT '条码/二维码内容（唯一，支持多业务关联）',
  `type`        tinyint(1)   NOT NULL DEFAULT '1' COMMENT '类型（1条码 2二维码）',
  `source`      tinyint(1)   NOT NULL DEFAULT '1' COMMENT '来源（1自生成 2图片识别录入）',
  `biz_type`    varchar(32)  NOT NULL COMMENT '业务类型（如 PRODUCT，可空可存其他业务）',
  `biz_id`      bigint DEFAULT NULL COMMENT '业务对象ID（如商品ID）',
  `user_id`     bigint NOT NULL COMMENT '所属用户ID',
  `remark`      varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by`   varchar(50)  DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by`   varchar(50)  DEFAULT NULL COMMENT '更新人',
  `deleted`     tinyint(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_biz` (`biz_type`,`biz_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用条码/二维码记录表';
```

- 通用层备注：`biz_type`/`biz_id` 不设外键（符合禁外键规范），一致性由业务层保证；同一 `code` 全局唯一，保障扫码命中。

## 二、商品场景

### 4. 商品表 `t_product`

```sql
CREATE TABLE IF NOT EXISTS `t_product` (
  `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`           varchar(100) NOT NULL COMMENT '商品名称',
  `category`       varchar(50)  DEFAULT NULL COMMENT '分类',
  `spec`           varchar(50)  DEFAULT NULL COMMENT '规格',
  `unit`           varchar(20)  DEFAULT NULL COMMENT '单位',
  `sale_price`     decimal(12,2) DEFAULT NULL COMMENT '售价',
  `cost_price`     decimal(12,2) DEFAULT NULL COMMENT '成本价',
  `stock`          int NOT NULL DEFAULT '0' COMMENT '库存',
  `supplier`       varchar(100) DEFAULT NULL COMMENT '供应商',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `image_url`      varchar(255) DEFAULT NULL COMMENT '商品图片地址（预留）',
  `remark`         varchar(200) DEFAULT NULL COMMENT '备注',
  `user_id`        bigint NOT NULL COMMENT '所属用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by`      varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by`      varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

### 5. 商品业务编码与关联

- 商品场景条码内容用业务唯一编码（如 `PROD20260921+0001` 递增），编码对应商品的 `id`。
- 一商品可关联多条条码（`t_barcode.biz_type=PRODUCT`、`biz_id=商品id`），支持同一个商品下既有自生成条码、也容纳"已有二维码识别录入"的编码。
- 扫码命中链路：扫出内容 → 查 `t_barcode.code` → 得 `biz_id` → 得商品；未命中则可新建并归属当前商品。

### 6. 后端接口（`com.base/barcode` 通用 + `com.base/product` 商品）

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| POST | `/barcode/generate` | 生成条码/二维码 PNG | 登录（双端） |
| POST | `/barcode/decode-image` | 识别图片条码（由 backend 转发到 python-tools `/api/barcode/decode-image`） | 登录（双端） |
| POST | `/barcode/code-record` | 登记一条通用条码记录（bizType+bizId） | 登录 |
| GET | `/barcode/code/{code}` | 按编码查通用记录与关联业务 | 登录 |
| GET | `/prod/list` | 商品分页列表 | 登录 |
| POST | `/prod` | 新增商品 | 登录 |
| PUT | `/prod/{id}` | 编辑商品 | 登录 |
| DELETE | `/prod/{id}` | 删除商品 | 登录 |
| GET | `/prod/codes/{id}` | 该商品关联条码列表 | 登录 |
| GET | `/prod/scan/{code}` | 扫码命中：编码→商品信息（未命中提示） | 登录 |

- 权限码规划：`common:barcode:use`（通用层，角色 1/2/4）；`product` 管理可复用登录即用（商品为主数据，暂按用户隔离不上菜单权限，实施阶段与用户final）。

### 7. 双端界面

**PC 端（frontend/src/views/）——仅数据管理 + 生成条码，不做扫码**
- `views/product/ProductList.vue`：商品列表（含来源列、新增/编辑/删除）。
- `views/product/ProductEdit.vue`：商品编辑（价库字段；生成条形码/二维码 PNG 供下载/打印贴标；条码列表）。
- PC 端**不做**图片识别、不做扫码录入、不做扫码枪输入框。

**小程序（ling-tools）——仅扫码 + 编辑表单，不生成码**
- `pages/barcode/barcode`：工具箱新增「条码」宫格。
  - **扫码**：`wx.scanCode` → 编码命中商品 → 显示商品信息+编辑表单 → 保存；未命中则新建商品并绑定该码。
  - **不提供生成码/图片识别入口**（生成在 PC 端）。
- 复用 `utils/request.js` 与新增 `api/product.js`（扫码命中/商品编辑）；登录/theme/nav-bar 复用现有。

## 验收标准

- `POST /barcode/generate`：任意内容可生成条码/二维码 PNG，双端可展示/导出/存相册。
- `POST /barcode/decode-image`：对一张含二维码的图可识别返回原始内容；纯图失败返回明确错误。
- `POST /barcode/code-record`：可登记 bizType+bizId 记录；同 code 保障唯一。
- 商品全流程：PC 端生成条码→生成/保存商品（含价库字段）→小程序扫码→命中查看/编辑商品→保存→列表可见。
- 小程序不生成码：扫到的条码默认已对应商品（PC 预生成贴标）；未对应时可新建商品并绑定该码。
- PC 端生成条码 + 数据管理，无扫码；小程序端扫码 + 编辑表单；两端走同一后端接口，数据按用户隔离。
- 后端零第三方扫码 SDK 冲突；新增表通过 `db/*.sql`；权限初始化脚本放 `db/init_product_permission.sql`。