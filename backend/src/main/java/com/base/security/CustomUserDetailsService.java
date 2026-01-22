package com.base.security;

import com.base.common.result.ResultCode;
import com.base.system.entity.Permission;
import com.base.system.entity.User;
import com.base.system.exception.BusinessException;
import com.base.system.mapper.PermissionMapper;
import com.base.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 * 用于加载用户信息
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已被禁用: {}", username);
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 加载用户权限
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
        List<GrantedAuthority> authorities = permissions.stream()
                .filter(p -> p.getPermissionCode() != null && !p.getPermissionCode().isEmpty())
                .map(p -> new SimpleGrantedAuthority(p.getPermissionCode()))
                .collect(Collectors.toList());
        user.setAuthorities(authorities);

        log.debug("用户 {} 加载了 {} 个权限", username, authorities.size());

        // 返回自定义 User 对象（实现了 UserDetails 接口）
        return user;
    }
}
