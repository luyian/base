# 任务清单：通用「部门子树」数据权限过滤（落地商品）

- [x] T1 规格：proposal/spec/tasks 三文档，用户确认（部门子树规则 / 子查询不改表 / 只放开查看）
- [x] T2 新增 `common/service/DataScopeHelper`：`visibleDeptIds` + `isVisible`（超管旁路、无部门→空、递归子树）
- [x] T3 改 `ProductServiceImpl`：列表部门子树过滤 + 读路径（get/scan/codes）viewable + 写路径（update/delete/bind/unbind）仍 own
- [x] T4 改 `ProductController`：`/bind` 移所有权门槛到 service
- [x] T5 编译校验（`mvn -o test-compile`）+ TEMP.md 记录
- [ ] T6 codegraph sync

追加任务（用户要求顺带整合两套用户体系/三套 SecurityUtils）：
- [x] T7 整合用户体系：`User`（唯一，extends `com.base.entity.BaseEntity`，去掉自声明 id/deleted）；删除 `SysUser`/`SysUserMapper`；8 个文件 `SysUser→User`、`SysUserMapper→UserMapper`
- [x] T8 整合 SecurityUtils：归并为唯一 `com.base.common.util.SecurityUtils`，删除 `com.base.util.SecurityUtils`、`com.base.system.util.SecurityUtils`，全后端导入统一
- [x] T9 校验：`mvn -o -s ... compile` + `test-compile` 均通过

状态：已完成（待重启后端 + 真机验证）