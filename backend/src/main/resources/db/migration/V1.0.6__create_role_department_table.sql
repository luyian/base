-- 创建角色部门关联表（用于自定义数据权限）
CREATE TABLE IF NOT EXISTS sys_role_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    department_id BIGINT NOT NULL COMMENT '部门ID',
    UNIQUE KEY uk_role_dept (role_id, department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

-- 为角色表添加数据权限字段（如果不存在）
ALTER TABLE sys_role ADD COLUMN IF NOT EXISTS data_scope INT DEFAULT 1 COMMENT '数据权限范围（1-全部数据 2-自定义 3-本部门 4-本部门及以下 5-仅本人）';
