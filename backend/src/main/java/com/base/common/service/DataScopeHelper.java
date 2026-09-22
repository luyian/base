package com.base.common.service;

import com.base.system.entity.User;
import com.base.system.mapper.DeptMapper;
import com.base.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用「部门子树」数据权限过滤助手（模块无关，可复用于任意按用户归属的业务）。
 *
 * <p>规则：当前登录用户可见「自身部门及其所有下级部门」的用户所拥有的数据；
 * 超级管理员（用户名 admin）全量可见；非超管且无部门则不可见任何数据。
 *
 * <p>使用方式（以商品为例）：
 * <ul>
 *   <li>列表：{@link #visibleDeptIds(Long)} 取可见部门集，超管为 null（不过滤）、无部门为空集（恒空）、
 *       否则用 {@code wrapper.inSql(ownerUserCol, "SELECT id FROM sys_user WHERE dept_id IN (...) AND deleted = 0")} 过滤。</li>
 *   <li>单条详情：{@link #isVisible(Long, Long)} 校验资源 owner 是否在当前用户可见范围内。</li>
 * </ul>
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataScopeHelper {

    /** 超级管理员用户名（全量旁路） */
    public static final String SUPER_ADMIN_USERNAME = "admin";

    private final UserMapper userMapper;
    private final DeptMapper deptMapper;

    /**
     * 获取当前用户可见的部门ID集合（含自身部门与所有下级部门，递归）。
     *
     * @param currentUserId 当前登录用户ID
     * @return null=超级管理员全量可见（调用方不追加过滤）；空集=无部门不可见任何数据；否则为可见 deptId 集合
     */
    public Set<Long> visibleDeptIds(Long currentUserId) {
        if (currentUserId == null) {
            return Collections.emptySet();
        }
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            return Collections.emptySet();
        }
        // 超级管理员：全量可见
        if (SUPER_ADMIN_USERNAME.equals(user.getUsername())) {
            return null;
        }
        if (user.getDeptId() == null) {
            return Collections.emptySet();
        }
        List<Long> ids = deptMapper.selectDeptAndChildIds(user.getDeptId());
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        return new HashSet<>(ids);
    }

    /**
     * 判断当前用户能否查看 owner 为 {@code targetOwnerUserId} 的资源（部门子树规则）。
     *
     * @param currentUserId     当前登录用户ID
     * @param targetOwnerUserId 资源归属用户ID
     * @return true=可见
     */
    public boolean isVisible(Long currentUserId, Long targetOwnerUserId) {
        if (currentUserId == null || targetOwnerUserId == null) {
            return false;
        }
        // 本人持自己的数据，始终可见
        if (currentUserId.equals(targetOwnerUserId)) {
            return true;
        }
        Set<Long> visible = visibleDeptIds(currentUserId);
        // 超级管理员：全量可见
        if (visible == null) {
            return true;
        }
        if (visible.isEmpty()) {
            return false;
        }
        User owner = userMapper.selectById(targetOwnerUserId);
        return owner != null && owner.getDeptId() != null && visible.contains(owner.getDeptId());
    }
}