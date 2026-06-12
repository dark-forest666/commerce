package com.example.demo.admin.service;

import com.example.demo.admin.repository.AdminUserRepository;
import com.example.demo.entity.User;
import com.example.demo.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    // 获取所有普通用户
    public List<User> getAllUsers() {
        return adminUserRepository.findByRoleNot("admin");
    }
    
    // 禁用/启用用户
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = adminUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        adminUserRepository.save(user);
    }
    
    // 重置用户密码为123456
    @Transactional
    public void resetPassword(Long userId) {
        User user = adminUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(BCryptUtil.encode("123456"));
        adminUserRepository.save(user);
    }
    
    // 修改管理员密码
    @Transactional
    public boolean changeAdminPassword(Long adminId, String oldPassword, String newPassword) {
        User admin = adminUserRepository.findById(adminId).orElseThrow(() -> new RuntimeException("管理员不存在"));
        if (!BCryptUtil.matches(oldPassword, admin.getPassword())) {
            return false;
        }
        admin.setPassword(BCryptUtil.encode(newPassword));
        adminUserRepository.save(admin);
        return true;
    }
}