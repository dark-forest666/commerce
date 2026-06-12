package com.example.demo.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        // 获取请求路径
        String requestURI = request.getRequestURI();

        // 1. 放行后台登录页：未登录用户也能访问登录页
        if (requestURI.equals("/admin/login")) {
            return true;
        }

        // 2. 检查session中是否有管理员登录信息
        Object userId = session.getAttribute("userId");
        Object role = session.getAttribute("role");

        if (userId == null || !"admin".equals(role)) {
            // 未登录/非管理员：跳转到后台登录页
            response.sendRedirect("/admin/login");
            return false;
        }

        // 3. 管理员已登录：放行请求
        return true;
    }
}