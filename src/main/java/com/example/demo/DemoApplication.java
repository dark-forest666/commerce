package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("测试商品A");
                p1.setPrice(99.9);
                p1.setStock(100);
                p1.setDescription("这是一个测试商品");
                p1.setImageUrl("https://picsum.photos/150/120?random=1");
                p1.setStatus(1);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setName("测试商品B");
                p2.setPrice(199.0);
                p2.setStock(50);
                p2.setDescription("另一个测试商品");
                p2.setImageUrl("https://picsum.photos/150/120?random=2");
                p2.setStatus(1);
                productRepository.save(p2);

                System.out.println("初始化商品数据完成");
            }
        };
    }

}
