package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");

        String uri = request.getRequestURI();
        // 静态资源和登录注册页面不拦截
        if (uri.startsWith("/login") || uri.startsWith("/register") || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images")) {
            return true;
        }

        if (userId == null) {
            log.debug("未登录用户尝试访问受保护页面: {}", uri);
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}