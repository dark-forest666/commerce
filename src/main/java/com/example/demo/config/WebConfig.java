package com.example.demo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册管理员拦截器（优先级更高）
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login"); // 排除管理员登录页（GET+POST所有请求）
        
        // 再注册普通用户拦截器
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                // 核心修复：将管理员登录接口加入普通拦截器白名单
                .excludePathPatterns(
                    "/login", 
                    "/register", 
                    "/admin/login", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/products"
                );
    }
}