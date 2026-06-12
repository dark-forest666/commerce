package com.example.demo.admin.repository;

// 修正：导入正确的JpaRepository和实体类
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 原有排序查询方法不变
    List<Category> findAllByOrderBySortAsc();
}