package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    // 获取用户的购物车所有商品
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    // 加入购物车（如果已存在则增加数量）
    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        CartItem existing = cartItemRepository.findByUserAndProduct(user, product).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemRepository.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setSelected(false);
            cartItemRepository.save(item);
        }
    }

    // 更新商品数量
    @Transactional
    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("购物车项不存在"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // 删除购物车项
    @Transactional
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 切换选中状态
    @Transactional
    public void toggleSelect(Long cartItemId, Boolean selected) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("购物车项不存在"));
        item.setSelected(selected);
        cartItemRepository.save(item);
    }

    // 获取选中商品的总价
    public double getSelectedTotalPrice(User user) {
        return cartItemRepository.findByUser(user).stream()
                .filter(CartItem::getSelected)
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // 获取选中的购物车项列表（用于下单）
    public List<CartItem> getSelectedItems(User user) {
        return cartItemRepository.findByUser(user).stream()
                .filter(CartItem::getSelected)
                .collect(Collectors.toList());
    }
}