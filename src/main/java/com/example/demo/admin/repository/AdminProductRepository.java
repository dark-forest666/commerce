package com.example.demo.admin.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminProductRepository extends JpaRepository<Product, Long> {
    // 查询库存预警商品（库存≤5）
    @Query("SELECT p FROM Product p WHERE p.stock <= 5")
    List<Product> findLowStockProducts();
}