-- Flowable 流程定义扩展表（存储 Flowable 原生不支持的自定义字段）
CREATE TABLE IF NOT EXISTS sys_flowable_definition_ext (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    deployment_id   VARCHAR(64)     DEFAULT NULL             COMMENT 'Flowable部署ID',
    process_definition_id VARCHAR(64) DEFAULT NULL           COMMENT 'Flowable流程定义ID',
    process_key     VARCHAR(100)    NOT NULL                 COMMENT '流程标识',
    process_name    VARCHAR(200)    NOT NULL                 COMMENT '流程名称',
    category        VARCHAR(50)     DEFAULT NULL             COMMENT '流程分类',
    description     VARCHAR(500)    DEFAULT NULL             COMMENT '描述',
    version         INT             NOT NULL DEFAULT 1       COMMENT '版本号',
    status          TINYINT         NOT NULL DEFAULT 0       COMMENT '状态(0草稿 1已发布 2禁用)',
    bpmn_xml        LONGTEXT                                 COMMENT 'BPMN XML内容',
    create_by       VARCHAR(64)     DEFAULT NULL             COMMENT '创建人',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT NULL             COMMENT '更新人',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除(0正常 1删除)',
    PRIMARY KEY (id),
    INDEX idx_process_key (process_key),
    INDEX idx_deployment_id (deployment_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Flowable流程定义扩展表';

-- 清空旧的自定义工作流表数据
TRUNCATE TABLE sys_process_history;
TRUNCATE TABLE sys_process_task;
TRUNCATE TABLE sys_process_instance;
TRUNCATE TABLE sys_process_node_relation;
TRUNCATE TABLE sys_process_node;
TRUNCATE TABLE sys_process_definition;
