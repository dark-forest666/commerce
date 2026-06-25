package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.admin.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.BCryptUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
        ProductRepository productRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository) {
    return args -> {
        // ======================================
        // 1. 初始化4个管理员账号（含原默认账号）
        // ======================================
        // 管理员1：原默认账号
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(BCryptUtil.encode("admin123"));
            admin.setRole("admin");
            admin.setStatus(1);
            userRepository.save(admin);
            System.out.println("初始化管理员账号1完成：admin / admin123");
        }

        // 管理员2：新增
        if (!userRepository.existsByUsername("admin2")) {
            User admin2 = new User();
            admin2.setUsername("admin2");
            admin2.setPassword(BCryptUtil.encode("admin2@123"));
            admin2.setRole("admin");
            admin2.setStatus(1);
            userRepository.save(admin2);
            System.out.println("初始化管理员账号2完成：admin2 / admin2@123");
        }

        // 管理员3：新增
        if (!userRepository.existsByUsername("admin3")) {
            User admin3 = new User();
            admin3.setUsername("admin3");
            admin3.setPassword(BCryptUtil.encode("admin3@123"));
            admin3.setRole("admin");
            admin3.setStatus(1);
            userRepository.save(admin3);
            System.out.println("初始化管理员账号3完成：admin3 / admin3@123");
        }

        // 管理员4：新增
        if (!userRepository.existsByUsername("admin4")) {
            User admin4 = new User();
            admin4.setUsername("admin4");
            admin4.setPassword(BCryptUtil.encode("admin4@123"));
            admin4.setRole("admin");
            admin4.setStatus(1);
            userRepository.save(admin4);
            System.out.println("初始化管理员账号4完成：admin4 / admin4@123");
        }

        // ======================================
        // 2. 初始化默认商品分类（保留原有逻辑）
        // ======================================
        if (categoryRepository.count() == 0) {
            Category c1 = new Category();
            c1.setName("电子产品");
            c1.setSort(1);
            categoryRepository.save(c1);

            Category c2 = new Category();
            c2.setName("服装鞋帽");
            c2.setSort(2);
            categoryRepository.save(c2);

            Category c3 = new Category();
            c3.setName("食品饮料");
            c3.setSort(3);
            categoryRepository.save(c3);

            Category c4 = new Category();
            c4.setName("家居用品");
            c4.setSort(4);
            categoryRepository.save(c4);

            System.out.println("初始化默认商品分类完成");
        }

        // ======================================
        // 3. 初始化测试商品（保留原有逻辑）
        // ======================================
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("无线蓝牙耳机");
            p1.setPrice(199.9);
            p1.setStock(50);
            p1.setDescription("高品质无线蓝牙耳机，续航24小时");
            p1.setImageUrl("/product-images/蓝牙耳机.png");
            p1.setStatus(1);
            p1.setCategoryId(1L); // 电子产品分类
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setName("纯棉T恤");
            p2.setPrice(89.0);
            p2.setStock(100);
            p2.setDescription("100%纯棉，舒适透气");
            p2.setImageUrl("/product-images/纯棉T恤.png");
            p2.setStatus(1);
            p2.setCategoryId(2L); // 服装鞋帽分类
            productRepository.save(p2);

            Product p3 = new Product();
            p3.setName("矿泉水整箱");
            p3.setPrice(29.9);
            p3.setStock(200);
            p3.setDescription("550ml*24瓶，天然矿泉水");
            p3.setImageUrl("/product-images/矿泉水.png");
            p3.setStatus(1);
            p3.setCategoryId(3L); // 食品饮料分类
            productRepository.save(p3);

            System.out.println("初始化测试商品数据完成");
        }

        System.out.println("=====================================");
        System.out.println("数据库自动初始化完成！");
        System.out.println("管理员后台：http://localhost:8080/admin/login");
        System.out.println("可用管理员账号：");
        System.out.println("1. admin / admin123");
        System.out.println("2. admin2 / admin2@123");
        System.out.println("3. admin3 / admin3@123");
        System.out.println("4. admin4 / admin4@123");
        System.out.println("普通用户可自行注册");
        System.out.println("=====================================");
        };
    }
}