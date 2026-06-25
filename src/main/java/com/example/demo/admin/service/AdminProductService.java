package com.example.demo.admin.service;

import com.example.demo.admin.repository.AdminProductRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProductService {
    @Autowired
    private AdminProductRepository adminProductRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    public List<Product> getAllProducts() {
        return adminProductRepository.findAll();
    }
    @Transactional
    public void saveProduct(Product product) {
        adminProductRepository.save(product);
    }
    
    // 根据ID获取商品
    public Product getProductById(Long id) {
        return adminProductRepository.findById(id).orElseThrow(() -> new RuntimeException("商品不存在"));
    }
    
    // 删除商品（前置校验：未被加入购物车）
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        if (cartItemRepository.countByProduct(product) > 0) {
            throw new RuntimeException("该商品已被加入购物车，无法删除");
        }
        adminProductRepository.delete(product);
    }
    
    // 上下架切换
    @Transactional
    public void toggleProductStatus(Long id) {
        Product product = getProductById(id);
        product.setStatus(product.getStatus() == 1 ? 0 : 1);
        adminProductRepository.save(product);
    }
    
    // 批量上下架
    @Transactional
    public void batchToggleStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            Product product = getProductById(id);
            product.setStatus(status);
            adminProductRepository.save(product);
        }
    }
    
    // 批量删除
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            deleteProduct(id);
        }
    }
    
    // 获取库存预警商品
    public List<Product> getLowStockProducts() {
        return adminProductRepository.findLowStockProducts();
    }
}