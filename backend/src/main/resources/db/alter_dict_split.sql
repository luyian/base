-- 数据字典拆表迁移：sys_enum → sys_dict_type + sys_dict_data
-- 执行时间：2026-06-09

-- 1. 创建字典类型表
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    `dict_name`   VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 2. 创建字典数据表
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    `dict_label`  VARCHAR(200) NOT NULL COMMENT '字典标签（显示值）',
    `dict_value`  VARCHAR(200) NOT NULL COMMENT '字典键值（编码）',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 3. 从 sys_enum 迁移数据到 sys_dict_type（去重）
INSERT INTO `sys_dict_type` (`dict_type`, `dict_name`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
SELECT
    `enum_type`,
    COALESCE(NULLIF(`type_desc`, ''), `enum_type`),
    1,
    MIN(`create_by`),
    MIN(`create_time`),
    MAX(`update_by`),
    MAX(`update_time`),
    0
FROM `sys_enum`
WHERE `deleted` = 0
GROUP BY `enum_type`, `type_desc`;

-- 4. 从 sys_enum 迁移数据到 sys_dict_data
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
SELECT
    `enum_type`,
    `enum_value`,
    `enum_code`,
    COALESCE(`sort`, 0),
    COALESCE(`status`, 1),
    `description`,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    `deleted`
FROM `sys_enum`;

-- 5. 备份旧表（可选，正式上线后删除）
-- RENAME TABLE `sys_enum` TO `sys_enum_bak`;
