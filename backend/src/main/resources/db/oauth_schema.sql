-- 用户第三方登录绑定表
CREATE TABLE sys_user_oauth (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id bigint NOT NULL COMMENT '系统用户ID',
    oauth_type varchar(20) NOT NULL COMMENT '第三方平台类型（github/wechat/gitee）',
    oauth_id varchar(100) NOT NULL COMMENT '第三方平台用户唯一标识',
    oauth_name varchar(100) DEFAULT NULL COMMENT '第三方平台用户名',
    oauth_avatar varchar(500) DEFAULT NULL COMMENT '第三方平台头像',
    oauth_email varchar(200) DEFAULT NULL COMMENT '第三方平台邮箱',
    access_token varchar(500) DEFAULT NULL COMMENT 'access_token',
    create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_oauth (oauth_type, oauth_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户第三方登录绑定表';
