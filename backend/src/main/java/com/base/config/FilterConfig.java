package com.base.config;

import com.base.common.security.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 *
 * @author base
 * @since 2026-01-15
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 XSS 过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssFilter xssFilter) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(xssFilter);
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);
        return registration;
    }
}
