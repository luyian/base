# 规格：通用「部门子树」数据权限过滤

## 数据权限规则
- **可见范围**：当前登录用户 → 自身部门 `sys_user.dept_id` → 递归取该部门及其所有下级部门 → 可见集合 S。
- **可见性**：一条数据的可见 当且仅当 数据 owner 的 `dept_id ∈ S`。
- **超管旁路**：用户名为 `admin` 视为全量可见（S = 全部，不加过滤）。
- **无部门**：非超管且 `dept_id` 为空 → 不可见任何（查询恒空）。

## 新组件
`com.base.common.service.DataScopeHelper`（`@Service`）：
- 注入 `SysUserMapper`、`DeptMapper`（`deptMapper.selectDeptAndChildIds` 递归 CTE 复用既有）。
- `Set<Long> visibleDeptIds(Long currentUserId)`：超管→`null`；无部门→空集；否则→自身+下级 deptId 集合。
- `boolean isVisible(Long currentUserId, Long targetOwnerUserId)`：同人→true；超管→true；否则判 owner.deptId ∈ 可见集。

## 商品模块改造（首个落地）
`ProductServiceImpl` 注入 `DataScopeHelper`：
- **列表 `pageProducts`**：去掉 `.eq(userId, userId)`，改为——超管不加过滤；空集→`eq(userId, -1)`（恒空）；否则 `inSql(ownerUserCol, "SELECT id FROM sys_user WHERE dept_id IN (...) AND deleted = 0")`。
- **新增 `checkViewableProduct(userId, id)`**：查存在且 `isVisible`，否则 403「无权查看该商品」；取代读路径的 `checkOwnProduct`。
- **读路径放开**：
  - `getProduct` → 改用 `checkViewableProduct`（详情/商品条码，上级可见）。
  - `scanByCode` → 命中校验由「仅本人」改 `isVisible`。
- **写路径仍限本人**：
  - `updateProduct`/`deleteProduct`/`unbindCode` 仍走 `checkOwnProduct`。
  - `bindCodeToProduct` 开头补 `checkOwnProduct(userId, id)`（原由控制器 `getProduct` 代做所有权门槛，现移到 service 内统一，防上级绑定下级商品）。
- **控制器 `ProductController`**：`/bind` 移除 `productService.getProduct(userId,id)` 这行（所有权已内移）。

## 前端
- 无改动：`/prod/list` 返回即部门范围行，PC端与小程序仅渲染。

## 数据依赖（确认过的表/列，不猜测）
- `sys_user`：`id`（主键）、`dept_id`、`deleted`、`username`。
- `sys_dept`：`id`、`parent_id`、`deleted`（`selectDeptAndChildIds` 递归 CTE 复用）。
- `t_product`：`user_id`（owner，引用 `sys_user.id`）、`deleted`。