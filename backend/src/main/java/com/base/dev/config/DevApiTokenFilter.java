package com.base.dev.config;

import com.base.system.service.ConfigService;
import com.base.system.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 开发工具模块 API Token 校验过滤器
 * <p>
 * 拦截 /api/dev/** 请求，双通道验证：
 * 1. 请求携带有效 JWT（已登录用户从系统菜单进入）→ 直接放行
 * 2. 无 JWT 时校验 X-Dev-Token 请求头（免登录场景的访问码）
 * <p>
 * token 从 sys_config 表读取（config_key = dev.api.token）
 *
 * @author base
 */
@Slf4j
@Component
@Order(1)
public class DevApiTokenFilter implements Filter {

    private static final String TOKEN_HEADER = "X-Dev-Token";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CONFIG_KEY = "dev.api.token";

    @Resource
    private ConfigService configService;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();

        // 只拦截 /api/dev/ 开头的请求
        if (!path.startsWith("/api/dev/")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 从 sys_config 读取 token 配置
        String apiToken = configService.getConfigValueByKey(CONFIG_KEY);

        // 如果未配置 token，放行（兼容未配置的环境）
        if (!StringUtils.hasText(apiToken)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 通道1：携带有效 JWT 直接放行（已登录用户）
        if (hasValidJwt(request)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 通道2：校验 X-Dev-Token（免登录访问码）
        String requestToken = request.getHeader(TOKEN_HEADER);
        if (apiToken.equals(requestToken)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            log.warn("Dev API Token 校验失败, path: {}, IP: {}", path, request.getRemoteAddr());
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"访问码无效\"}");
        }
    }

    /**
     * 检查请求是否携带有效的 JWT Token
     */
    private boolean hasValidJwt(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return false;
        }
        String jwt = authHeader.substring(BEARER_PREFIX.length());
        try {
            String username = jwtUtil.getUsernameFromToken(jwt);
            return StringUtils.hasText(username) && !jwtUtil.isTokenExpired(jwt);
        } catch (Exception e) {
            return false;
        }
    }
}
