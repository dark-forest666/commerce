package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查找用户（用于登录和注册校验）
    Optional<User> findByUsername(String username);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

     // 新增：统计指定时间范围内的用户数
     long countByCreateTimeBetween(Date start, Date end);
}