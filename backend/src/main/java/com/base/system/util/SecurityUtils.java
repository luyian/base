package com.base.system.util;

import com.base.system.entity.User;
import com.base.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 */
@Slf4j
@Component
public class SecurityUtils {

    @Autowired
    private UserMapper userMapper;

    // ThreadLocal to store current user ID
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    /**
     * 设置当前用户ID（由JWT过滤器调用）
     */
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * 设置当前用户名（由JWT过滤器调用）
     */
    public static void setCurrentUsername(String username) {
        CURRENT_USERNAME.set(username);
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        // 首先尝试从ThreadLocal获取
        Long userId = CURRENT_USER_ID.get();
        if (userId != null) {
            return userId;
        }
        
        // 然后尝试从SecurityContext获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof User) {
                return ((User) principal).getId();
            }
            
            if (principal instanceof UserDetails) {
                // 尝试从数据库获取用户
                String username = ((UserDetails) principal).getUsername();
                return getUserIdByUsername(username);
            }
            
            if (principal instanceof String) {
                return getUserIdByUsername((String) principal);
            }
        }
        
        return null;
    }

    /**
     * 根据用户名获取用户ID
     */
    private static Long getUserIdByUsername(String username) {
        try {
            if (getUserMapper() != null) {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, username);
                User user = getUserMapper().selectOne(wrapper);
                return user != null ? user.getId() : null;
            }
        } catch (Exception e) {
            log.warn("获取用户ID失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof User) {
                return (User) principal;
            }
            
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return getUserByUsername(username);
            }
            
            if (principal instanceof String) {
                return getUserByUsername((String) principal);
            }
        }
        return null;
    }

    /**
     * 根据用户名获取用户
     */
    private static User getUserByUsername(String username) {
        try {
            UserMapper mapper = getUserMapper();
            if (mapper != null) {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, username);
                return mapper.selectOne(wrapper);
            }
        } catch (Exception e) {
            log.warn("获取用户失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        // 首先尝试从ThreadLocal获取
        String username = CURRENT_USERNAME.get();
        if (username != null) {
            return username;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof User) {
                return ((User) principal).getUsername();
            } else if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }

    /**
     * 获取 UserMapper 实例
     */
    private static UserMapper getUserMapper() {
        try {
            org.springframework.web.context.WebApplicationContext context = 
                org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
            if (context != null) {
                return context.getBean(UserMapper.class);
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }
}