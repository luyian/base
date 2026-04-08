package com.base.open.interceptor;

import com.base.open.config.OpenApiConfig;
import com.base.open.context.OpenApiContext;
import com.base.open.service.OpenApiTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 开放接口认证拦截器
 * 校验 /open/** 路径下请求的系统 Token
 *
 * @author base
 */
@Slf4j
@Component
public class OpenApiAuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private OpenApiTokenService openApiTokenService;

    @Autowired
    private OpenApiConfig openApiConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // Token 签发接口本身不需要校验
        if (requestUri.endsWith("/open/auth/token")) {
            return true;
        }

        // 检查开放接口是否启用
        if (!Boolean.TRUE.equals(openApiConfig.getEnabled())) {
            writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "开放接口未启用");
            return false;
        }

        // 从请求头提取 Token
        String token = getTokenFromRequest(request);
        if (token == null) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "缺少访问令牌");
            return false;
        }

        // 校验 Token
        String appId = openApiTokenService.validateToken(token);
        if (appId == null) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "访问令牌无效或已过期");
            return false;
        }

        // 查找应用名称
        OpenApiConfig.AppInfo appInfo = openApiConfig.getAppByAppId(appId);
        String appName = appInfo != null ? appInfo.getName() : appId;

        // 设置请求上下文
        OpenApiContext.set(appId, appName);
        log.info("开放接口认证成功，应用: {}({}), 请求: {}", appName, appId, requestUri);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        OpenApiContext.clear();
    }

    /**
     * 从请求头中提取 Bearer Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>(4);
        result.put("code", status);
        result.put("message", message);
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
