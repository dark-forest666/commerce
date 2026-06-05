package com.example.demo.controller;

import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // 处理注册请求
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        // 简单输入校验（防XSS：Thymeleaf会自动转义输出，这里不需要额外处理）
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "用户名和密码不能为空");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "密码长度至少6位");
            return "register";
        }

        boolean success = userService.register(username.trim(), password);
        if (success) {
            log.info("注册成功，跳转登录页: {}", username);
            return "redirect:/login";
        } else {
            model.addAttribute("error", "用户名已存在，请更换");
            return "register";
        }
    }

    // 显示登录页面
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 处理登录请求
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        boolean success = userService.login(username.trim(), password, session);
        if (success) {
            return "redirect:/index";   // 登录成功跳转到主页
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        userService.logout(session);
        return "redirect:/login";
    }

    // 受保护的主页（登录后才能访问）
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "index";
    }
}