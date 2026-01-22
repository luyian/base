package com.base.common.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * XSS 过滤器
 * 防止跨站脚本攻击
 *
 * @author base
 * @since 2026-01-15
 */
@Slf4j
@Component
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("XSS 过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 包装请求，对参数进行 XSS 过滤
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {
        log.info("XSS 过滤器销毁");
    }
}
