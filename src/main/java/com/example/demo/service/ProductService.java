package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllOnSaleProducts() {
        return productRepository.findByStatus(1);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // 保存或更新商品（后续后台管理会用）
    public Product save(Product product) {
        return productRepository.save(product);
    }
}