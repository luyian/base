-- ----------------------------
-- 通用条码/二维码能力层 + 商品管理场景建表
-- 通用条码记录表 t_barcode + 商品表 t_product
-- ----------------------------

-- ----------------------------
-- Table: t_barcode 通用条码/二维码记录表
-- 与业务解耦：biz_type + biz_id 可关联任意业务对象；商品只是其中一个业务类型（PRODUCT）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_barcode` (
  `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code`        varchar(64) NOT NULL COMMENT '条码/二维码内容（全局唯一）',
  `type`        tinyint(1)  NOT NULL DEFAULT '1' COMMENT '类型（1条码 2二维码）',
  `source`      tinyint(1)  NOT NULL DEFAULT '1' COMMENT '来源（1自生成 2图片识别录入）',
  `biz_type`    varchar(32)          DEFAULT NULL COMMENT '业务类型（如 PRODUCT）',
  `biz_id`      bigint               DEFAULT NULL COMMENT '业务对象ID（如商品ID）',
  `user_id`     bigint      NOT NULL COMMENT '所属用户ID',
  `file_id`     bigint               DEFAULT NULL COMMENT '关联文件ID(sys_file，生成图归档)',
  `remark`      varchar(200)         DEFAULT NULL COMMENT '备注',
  `create_time` datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by`   varchar(50)          DEFAULT NULL COMMENT '创建人',
  `update_time` datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by`   varchar(50)          DEFAULT NULL COMMENT '更新人',
  `deleted`     tinyint(1) DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`) USING BTREE,
  KEY `idx_biz` (`biz_type`,`biz_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通用条码/二维码记录表';

-- ----------------------------
-- Table: t_product 商品表
-- 价库字段：售价/成本/库存/供应商/生产日期；按用户维度隔离
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_product` (
  `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code`            varchar(64)           DEFAULT NULL COMMENT '商品唯一编码（新增时自动生成，如 PROD202609210001）',
  `name`            varchar(100) NOT NULL COMMENT '商品名称',
  `category`        varchar(50)           DEFAULT NULL COMMENT '分类',
  `spec`            varchar(50)           DEFAULT NULL COMMENT '规格',
  `unit`            varchar(20)           DEFAULT NULL COMMENT '单位',
  `sale_price`      decimal(12,2)         DEFAULT NULL COMMENT '售价',
  `cost_price`      decimal(12,2)         DEFAULT NULL COMMENT '成本价',
  `stock`           int          NOT NULL DEFAULT '0' COMMENT '库存',
  `supplier`        varchar(100)          DEFAULT NULL COMMENT '供应商',
  `production_date` date                  DEFAULT NULL COMMENT '生产日期',
  `image_url`       varchar(255)          DEFAULT NULL COMMENT '商品图片地址（预留）',
  `remark`          varchar(200)          DEFAULT NULL COMMENT '备注',
  `user_id`         bigint       NOT NULL COMMENT '所属用户ID',
  `create_time`     datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by`       varchar(50)           DEFAULT NULL COMMENT '创建人',
  `update_time`     datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by`       varchar(50)           DEFAULT NULL COMMENT '更新人',
  `deleted`         tinyint(1)   DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';