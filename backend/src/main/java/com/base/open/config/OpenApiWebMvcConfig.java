package com.base.open.config;

import com.base.open.interceptor.OpenApiAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 开放接口 Web MVC 配置
 * 注册 Token 校验拦截器到 /open/** 路径
 *
 * @author base
 */
@Configuration
public class OpenApiWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private OpenApiAuthInterceptor openApiAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(openApiAuthInterceptor)
                .addPathPatterns("/open/**");
    }
}
