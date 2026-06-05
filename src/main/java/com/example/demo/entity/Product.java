// src/main/java/com/example/demo/entity/Product.java
package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private Double price;

    private Integer stock;

    @Column(length = 500)
    private String description;

    private String imageUrl;   // 商品图片URL

    private Integer status = 1; // 1-上架 0-下架
}