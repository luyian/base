---
name: db-export-init
aliases: ["db:export", "db:init"]
description: 连接远程数据库，按现有数据生成初始化脚本（schema.sql + data.sql）
---

# 数据库初始化脚本导出

## 触发方式
用户输入 `/db:export` 或要求"整理数据库初始化脚本"时触发。

## 工作流程

### 1. 读取数据库连接配置
- 从 `backend/src/main/resources/application-dev.yml` 读取数据源配置
- 提取 host、port、database、username、password

### 2. 连接远程数据库
- 使用 Python + PyMySQL 连接（本地无 mysql 客户端时的替代方案）
- 列出所有表，区分自定义表和框架自动创建的表

### 3. 表分类规则

**跳过的表（框架自动创建）：**
- `ACT_*` — Flowable 活动引擎表
- `FLW_*` — Flowable 批处理表

**导出结构但不导出数据的表（运行时数据）：**
- `sys_log_login` — 登录日志
- `sys_log_operation` — 操作日志
- `stk_kline_daily` — K线日数据（大量）
- `stk_recommend` — 推荐记录
- `stk_score_record` — 评分记录（大量）
- `stk_sync_failure` — 同步失败记录
- `sys_export_task` — 导出任务
- `sys_file_log` — 文件日志
- `relay_log` — 中继日志
- `sys_notice_read` — 通知已读记录
- 其他空表或纯运行时表

**导出结构+数据的表（配置/种子数据）：**
- `sys_user` — 用户账号
- `sys_role` — 角色
- `sys_user_role` — 用户-角色关联
- `sys_permission` — 菜单权限
- `sys_role_permission` — 角色-权限关联
- `sys_dept` — 部门
- `sys_dict_type` — 字典类型
- `sys_dict_data` — 字典数据
- `sys_enum` — 枚举
- `sys_config` — 系统配置
- `sys_region` — 行政区域
- `sys_ai_config` — AI配置
- `sys_export_field` — 导出字段配置
- `sys_flowable_definition_ext` — 流程定义扩展
- `stk_score_rule` — 评分规则
- `stk_api_token` — API令牌配置
- `stk_data_mapping` — 数据映射配置
- `stk_fund_config` — 基金配置
- `relay_api_key` — 中继API密钥
- `relay_channel` — 中继渠道
- `msg_subscription` — 消息订阅配置
- `sys_user_oauth` — OAuth绑定

### 4. 生成 schema.sql
- 路径：`backend/src/main/resources/db/schema.sql`
- 内容：所有自定义表的 `DROP TABLE IF EXISTS` + `CREATE TABLE` 语句
- 顺序：按表名字母排序
- 头部包含文件说明注释

### 5. 生成 data.sql
- 路径：`backend/src/main/resources/db/data.sql`
- 内容：配置表的 `DELETE` + `INSERT` 语句
- 每张表先 `DELETE FROM`，再批量 `INSERT`（每 500 行一批）
- 值转义规则：NULL 保持 NULL，数字不加引号，字符串转义单引号和反斜杠

### 6. 清理旧文件
- 删除 `db/` 目录下除 `schema.sql` 和 `data.sql` 以外的所有 `.sql` 文件
- `git add` 新生成的两个文件

## 输出文件

```
backend/src/main/resources/db/
├── schema.sql   # 表结构（DROP + CREATE）
└── data.sql     # 初始数据（DELETE + INSERT）
```

## 判断数据是否需要导出的原则

1. **导出**：系统启动必须的配置数据、权限数据、字典数据
2. **不导出**：用户产生的业务数据、日志、大量历史数据
3. **判断依据**：如果新环境部署后系统无法正常运行（缺少菜单、角色、配置等），则该表数据需要导出

## 注意事项

- 如果新增了表，需要重新运行此 skill 更新脚本
- 敏感数据（密码已是加密存储）可以导出，但 API Secret 等明文配置需评估
- sys_region 数据量较大（3000+），但属于必要的基础数据
- 导出前确认 PyMySQL 已安装：`pip show pymysql`
