package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // 商品列表页（登录后可访问，拦截器已放行需登录？注意：index和products都需要登录）
    @GetMapping("/products")
    public String productList(Model model) {
        List<Product> products = productService.getAllOnSaleProducts();
        model.addAttribute("products", products);
        return "productList";
    }
}