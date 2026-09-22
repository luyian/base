# Proposal：通用「部门子树」数据权限过滤（首个落地：商品）

## 背景与诉求
- 诉求：**上级部门可查看下级部门的商品；总公司可查看全部；做成通用的数据过滤，以后其他业务也能用。**
- 现状问题：
  - `t_product` 只有 `user_id` 归属，无 `dept_id`；当前 `/prod/list` 按 `user_id = 当前用户` 硬隔离，只能看本人商品。
  - 项目已有 `@DataScope` 注解 + `DataScopeInterceptor` 脚手架，但**不匹配**：它按主表 `dept_id`/`create_by` 列过滤，`t_product` 两者皆无，且拦截器用正则改 SQL 很脆弱、对每张表一视同仁，硬套会有 SQL 报错风险。
  - 存在 `User`/`SysUser` 两套用户实体、三份 `SecurityUtils` 的历史混乱（另文梳理）。

## 方案（用户已确认三取舍）
1. **判定规则：部门子树规则** —— 当前用户可见「自身部门 + 所有下级部门」的用户所拥有的数据。总公司部门位处根节点时其子树即全量，天然满足「总公司看全部」。不依赖角色 `data_scope` 配置。
2. **部门归属：子查询不改表** —— 数据的部门 = 数据 owner(user) 的 `sys_user.dept_id`，过滤用子查询，不冗余 `dept_id` 列，对任何「按用户归属」的业务通用。
3. **读写范围：只放开查看** —— 列表/详情/扫码（含商品条码查看）按部门子树放开；编辑/删除/绑定/解绑等写操作仍限本人，防止上级误改/误删下级数据。

## 通用性设计
- 新增 `DataScopeHelper`（`common/service`，模块无关）：
  - `Set<Long> visibleDeptIds(Long currentUserId)`：当前用户可见部门集（自身+递归下级）；超管(admin)返回 `null` 表示全量；无部门返回空集（不可见任何）。
  - `boolean isVisible(Long currentUserId, Long targetOwnerUserId)`：单条资源可见性判定（详情/扫码复用）。
- 落地方式：任何「按拥有者用户归属」的业务，均可（a）列表用 `visibleDeptIds` + `wrapper.inSql(ownerCol, "SELECT id FROM sys_user WHERE dept_id IN (...) AND deleted=0")` 过滤；（b）详情用 `isVisible` 校验。商品为首个消费者，后续模块复用同一 helper 即可。

## 不做
- 不统一 `User/SysUser`、不删旧 `@DataScope` 脚手架（留作独立重构任务，风险较大）。
- 不放到条码管理并列改造（`/barcode/list` 亦可由同一 helper 扩展，本次不着手）。
- 不改 `t_product` 表结构。

## 验收标准
- 超管(admin)/总公司根部门用户：`/prod/list` 返回全部商品。
- 上级部门用户：能看到自己及下级部门用户创建的商品；详情/扫码/条码可见。
- 同级/下级用户：看不到上级部门（越部）的商品。
- 编辑/删除/绑定/解绑仍限本人（越权报 403）。
- `mvn -o test-compile` 通过。