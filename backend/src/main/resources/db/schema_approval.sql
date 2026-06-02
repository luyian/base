-- 第三方审批集成表结构
-- 表名统一使用 tp_ 前缀（thirdparty）

-- 审批模板表
CREATE TABLE IF NOT EXISTS `tp_approval_template` (
    `id`                     BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `template_code`          VARCHAR(100)    NOT NULL                 COMMENT '模板编码（业务类型标识）',
    `template_name`          VARCHAR(200)    NOT NULL                 COMMENT '模板名称',
    `platform`               VARCHAR(20)     NOT NULL DEFAULT 'feishu' COMMENT '第三方平台（feishu/dingtalk）',
    `platform_approval_code` VARCHAR(200)    DEFAULT NULL             COMMENT '平台审批定义编码',
    `form_mapping`           TEXT            DEFAULT NULL             COMMENT '表单字段映射配置（JSON）',
    `description`            VARCHAR(500)    DEFAULT NULL             COMMENT '模板说明',
    `status`                 TINYINT         NOT NULL DEFAULT 1       COMMENT '状态（0-禁用 1-启用）',
    `create_by`              VARCHAR(64)     DEFAULT NULL             COMMENT '创建人',
    `create_time`            DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`              VARCHAR(64)     DEFAULT NULL             COMMENT '更新人',
    `update_time`            DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code_platform` (`template_code`, `platform`, `deleted`),
    INDEX `idx_platform` (`platform`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方审批模板表';

-- 审批实例表
CREATE TABLE IF NOT EXISTS `tp_approval_instance` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `template_id`           BIGINT          NOT NULL                 COMMENT '关联模板ID',
    `template_code`         VARCHAR(100)    NOT NULL                 COMMENT '模板编码（冗余）',
    `platform`              VARCHAR(20)     NOT NULL DEFAULT 'feishu' COMMENT '第三方平台',
    `platform_instance_id`  VARCHAR(200)    DEFAULT NULL             COMMENT '平台审批实例ID',
    `business_key`          VARCHAR(200)    NOT NULL                 COMMENT '业务主键',
    `business_type`         VARCHAR(100)    DEFAULT NULL             COMMENT '业务类型',
    `title`                 VARCHAR(500)    DEFAULT NULL             COMMENT '审批标题',
    `applicant_user_id`     BIGINT          NOT NULL                 COMMENT '发起人系统用户ID',
    `applicant_open_id`     VARCHAR(100)    DEFAULT NULL             COMMENT '发起人平台用户ID',
    `form_data`             TEXT            DEFAULT NULL             COMMENT '提交的表单数据（JSON）',
    `status`                VARCHAR(30)     NOT NULL DEFAULT 'PENDING' COMMENT '审批状态（PENDING/APPROVED/REJECTED/CANCELED）',
    `platform_status`       VARCHAR(50)     DEFAULT NULL             COMMENT '平台原始状态',
    `result_comment`        VARCHAR(1000)   DEFAULT NULL             COMMENT '审批结果说明',
    `idempotent_key`        VARCHAR(200)    DEFAULT NULL             COMMENT '幂等键',
    `submitted_at`          DATETIME        DEFAULT NULL             COMMENT '提交时间',
    `completed_at`          DATETIME        DEFAULT NULL             COMMENT '完成时间',
    `create_by`             VARCHAR(64)     DEFAULT NULL             COMMENT '创建人',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             VARCHAR(64)     DEFAULT NULL             COMMENT '更新人',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`               TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idempotent` (`idempotent_key`),
    UNIQUE KEY `uk_platform_instance` (`platform`, `platform_instance_id`),
    INDEX `idx_template_code` (`template_code`),
    INDEX `idx_business_key` (`business_key`),
    INDEX `idx_applicant` (`applicant_user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方审批实例表';

-- 事件回调日志表
CREATE TABLE IF NOT EXISTS `tp_event_callback_log` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `platform`              VARCHAR(20)     NOT NULL                 COMMENT '来源平台',
    `event_id`              VARCHAR(200)    DEFAULT NULL             COMMENT '事件唯一ID（去重用）',
    `event_type`            VARCHAR(100)    NOT NULL                 COMMENT '事件类型',
    `event_payload`         LONGTEXT        NOT NULL                 COMMENT '事件原始内容（完整JSON）',
    `status`                VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT '处理状态（PENDING/PROCESSING/SUCCESS/FAILED/SKIPPED）',
    `retry_count`           INT             NOT NULL DEFAULT 0       COMMENT '已重试次数',
    `max_retry`             INT             NOT NULL DEFAULT 3       COMMENT '最大重试次数',
    `error_message`         VARCHAR(2000)   DEFAULT NULL             COMMENT '处理失败原因',
    `processed_at`          DATETIME        DEFAULT NULL             COMMENT '处理完成时间',
    `next_retry_at`         DATETIME        DEFAULT NULL             COMMENT '下次重试时间',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_event_id` (`platform`, `event_id`),
    INDEX `idx_event_type` (`event_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_next_retry` (`status`, `next_retry_at`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方事件回调日志表';
