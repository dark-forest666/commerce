package com.example.demo.admin.repository;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
@Repository
public interface AdminUserRepository extends JpaRepository<User, Long> {
    // 查询所有非管理员用户
    List<User> findByRoleNot(String role);
    
    // 统计指定时间范围内的新增用户数
    long countByCreateTimeBetween(Date start, Date end);
    
    // 新增：统计管理员数量
    long countByRole(String role);
    
    // 新增：统计普通用户数量
    long countByRoleNot(String role);
}