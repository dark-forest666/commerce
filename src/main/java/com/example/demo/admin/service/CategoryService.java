package com.example.demo.admin.service;

import com.example.demo.entity.Category;
import com.example.demo.admin.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 获取所有分类（按排序）
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderBySortAsc();
    }
    
    // 保存分类
    @Transactional
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }
    
    // 删除分类
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}