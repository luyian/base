package com.base.security;

import com.base.system.util.JwtUtil;
import com.base.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器
 * 用于验证请求中的 JWT Token
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String TOKEN_PREFIX = "token:";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. 从请求头中获取 Token
            String token = getTokenFromRequest(request);

            // 2. 验证 Token
            if (StringUtils.hasText(token)) {
                // 从 Token 中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证 Token 是否有效
                    if (jwtUtil.validateToken(token, username)) {
                        // 验证 Redis 中的 Token 是否一致
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        String cachedToken = redisUtil.get(TOKEN_PREFIX + userId, String.class);

                        if (token.equals(cachedToken)) {
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // 设置到 SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.debug("JWT 认证成功，username: {}", username);
                        } else {
                            log.warn("Token 已失效或被替换，username: {}", username);
                        }
                    } else {
                        log.warn("Token 验证失败，username: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT 认证失败", e);
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
