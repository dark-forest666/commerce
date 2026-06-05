package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.BCryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 注册新用户
     * @param username 用户名
     * @param rawPassword 明文密码
     * @return true-注册成功 false-用户名已存在
     */
    public boolean register(String username, String rawPassword) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            log.warn("注册失败，用户名已存在: {}", username);
            return false;
        }

        // 创建新用户，密码加密
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCryptUtil.encode(rawPassword));
        user.setRole("user");  // 普通用户
        userRepository.save(user);

        log.info("新用户注册成功: {}", username);
        return true;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param rawPassword 明文密码
     * @param session 当前会话
     * @return true-登录成功 false-用户名或密码错误
     */
    public boolean login(String username, String rawPassword, HttpSession session) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCryptUtil.matches(rawPassword, user.getPassword())) {
                // 登录成功，存储用户信息到 session
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                log.info("用户登录成功: {}, 角色: {}", username, user.getRole());
                return true;
            } else {
                log.warn("用户 {} 登录失败：密码错误", username);
            }
        } else {
            log.warn("用户 {} 登录失败：用户名不存在", username);
        }
        return false;
    }
    /**findById(Long id) 方法用于根据用户ID查询用户信息，返回一个Optional<User>对象。*/
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    /**
     * 退出登录
     */
    public void logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        session.invalidate();
        log.info("用户退出登录: {}", username);
    }

}