package com.leyou.auth.config;

import com.leyou.auth.interceptors.LoginInterceptor;
import com.leyou.auth.utils.JwtUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class MvcConfiguration implements WebMvcConfigurer {

    private JwtUtils jwtUtils;

    private ClientProperties properties;

    //雷，循环依赖，feign请求要web环境，
    public MvcConfiguration(@Lazy JwtUtils jwtUtils, ClientProperties properties) {
        this.jwtUtils = jwtUtils;
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new LoginInterceptor(jwtUtils));
        // 判断用户是否配置了拦截路径，如果没配置走默认，就是拦截 /**
        if (!CollectionUtils.isEmpty(properties.getIncludeFilterPaths())) {
            interceptorRegistration.addPathPatterns(properties.getIncludeFilterPaths());
        }
        // 判断用户是否配置了放行路径，如果没配置就是空
        if (!CollectionUtils.isEmpty(properties.getExcludeFilterPaths())) {
            interceptorRegistration.excludePathPatterns(properties.getExcludeFilterPaths());
        }
    }
}
