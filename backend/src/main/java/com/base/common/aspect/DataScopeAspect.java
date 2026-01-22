package com.base.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.annotation.DataScope;
import com.base.common.enums.DataScopeEnum;
import com.base.common.util.SecurityUtils;
import com.base.system.entity.Role;
import com.base.system.entity.RoleDepartment;
import com.base.system.entity.User;
import com.base.system.mapper.DeptMapper;
import com.base.system.mapper.RoleDepartmentMapper;
import com.base.system.mapper.RoleMapper;
import com.base.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限切面
 *
 * @author base
 * @since 2026-01-13
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DataScopeAspect {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleDepartmentMapper roleDepartmentMapper;
    private final DeptMapper deptMapper;

    /**
     * 数据权限过滤
     */
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        try {
            // 获取当前用户
            Long userId = SecurityUtils.getCurrentUserId();
            User user = userMapper.selectById(userId);
            if (user == null) {
                return;
            }

            // 获取用户角色
            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            if (roles == null || roles.isEmpty()) {
                return;
            }

            // 获取最大的数据权限范围（数字越小权限越大）
            Integer maxDataScope = roles.stream()
                    .map(Role::getDataScope)
                    .min(Integer::compareTo)
                    .orElse(DataScopeEnum.SELF.getCode());

            // 构建数据权限SQL
            String sqlCondition = buildDataScopeSql(maxDataScope, user, roles, dataScope);

            // 将SQL条件设置到ThreadLocal中，供MyBatis拦截器使用
            DataScopeContextHolder.setDataScope(sqlCondition);

            log.debug("数据权限SQL: {}", sqlCondition);
        } catch (Exception e) {
            log.error("数据权限过滤失败", e);
        }
    }

    /**
     * 构建数据权限SQL
     */
    private String buildDataScopeSql(Integer dataScope, User user, List<Role> roles, DataScope dataScopeAnnotation) {
        StringBuilder sql = new StringBuilder();
        String deptAlias = dataScopeAnnotation.deptAlias();
        String userAlias = dataScopeAnnotation.userAlias();

        DataScopeEnum dataScopeEnum = DataScopeEnum.getByCode(dataScope);
        if (dataScopeEnum == null) {
            return "";
        }

        switch (dataScopeEnum) {
            case ALL:
                // 全部数据权限，不需要过滤
                break;
            case CUSTOM:
                // 自定义数据权限，查询角色关联的部门
                List<Long> customDeptIds = roles.stream()
                        .filter(role -> DataScopeEnum.CUSTOM.getCode().equals(role.getDataScope()))
                        .flatMap(role -> {
                            List<RoleDepartment> roleDepts = roleDepartmentMapper.selectList(
                                    new LambdaQueryWrapper<RoleDepartment>()
                                            .eq(RoleDepartment::getRoleId, role.getId())
                            );
                            return roleDepts.stream().map(RoleDepartment::getDepartmentId);
                        })
                        .distinct()
                        .collect(Collectors.toList());

                if (!customDeptIds.isEmpty()) {
                    sql.append(" AND ");
                    if (deptAlias != null && !deptAlias.isEmpty()) {
                        sql.append(deptAlias).append(".");
                    }
                    sql.append("dept_id IN (")
                            .append(customDeptIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                            .append(")");
                }
                break;
            case DEPT:
                // 本部门数据权限
                if (user.getDeptId() != null) {
                    sql.append(" AND ");
                    if (deptAlias != null && !deptAlias.isEmpty()) {
                        sql.append(deptAlias).append(".");
                    }
                    sql.append("dept_id = ").append(user.getDeptId());
                }
                break;
            case DEPT_AND_CHILD:
                // 本部门及以下数据权限（递归查询所有子部门）
                if (user.getDeptId() != null) {
                    List<Long> deptAndChildIds = deptMapper.selectDeptAndChildIds(user.getDeptId());
                    if (deptAndChildIds != null && !deptAndChildIds.isEmpty()) {
                        sql.append(" AND ");
                        if (deptAlias != null && !deptAlias.isEmpty()) {
                            sql.append(deptAlias).append(".");
                        }
                        sql.append("dept_id IN (")
                                .append(deptAndChildIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                                .append(")");
                    }
                }
                break;
            case SELF:
                // 仅本人数据权限
                sql.append(" AND ");
                if (userAlias != null && !userAlias.isEmpty()) {
                    sql.append(userAlias).append(".");
                }
                sql.append("create_by = ").append(user.getId());
                break;
            default:
                break;
        }

        return sql.toString();
    }

    /**
     * 方法执行后清理ThreadLocal
     */
    @After("@annotation(dataScope)")
    public void doAfter(JoinPoint point, DataScope dataScope) {
        DataScopeContextHolder.clearDataScope();
    }
}
