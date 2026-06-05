package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    /**
     * 创建订单（从购物车选中的商品）
     */
    @Transactional
    public Order createOrder(User user, Address address, List<CartItem> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new RuntimeException("未选中任何商品");
        }

        // 计算总金额并检查库存
        double total = 0.0;
        for (CartItem item : selectedItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }
            total += product.getPrice() * item.getQuantity();
        }

        // 扣减库存
        for (CartItem item : selectedItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productService.save(product);
        }

        // 生成订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        order.setTotalAmount(total);
        order.setStatus(Order.STATUS_WAIT_PAY);
        order.setAddress(address);
        order.setCreateTime(new Date());
        order = orderRepository.save(order);

        // 生成订单项
        for (CartItem item : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        // 清空购物车中选中的商品
        for (CartItem item : selectedItems) {
            cartService.removeCartItem(item.getId());
        }

        return order;
    }

    /**
     * 根据用户获取订单列表
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreateTimeDesc(user);
    }

    /**
     * 根据订单ID获取订单（带权限校验）
     */
    public Order getOrderByIdAndUser(Long orderId, User user) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return null;
        }
        return order;
    }

    /**
     * 取消订单（仅待支付状态可取消，恢复库存）
     */
    @Transactional
    public boolean cancelOrder(Long orderId, User user) {
        Order order = getOrderByIdAndUser(orderId, user);
        if (order == null) {
            return false;
        }
        if (order.getStatus() != Order.STATUS_WAIT_PAY) {
            return false; // 非待支付状态不可取消
        }
        // 恢复库存
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.save(product);
        }
        order.setStatus(Order.STATUS_CANCELED);
        orderRepository.save(order);
        return true;
    }

    /**
     * 模拟支付（仅待支付状态可支付）
     */
    @Transactional
    public boolean simulatePay(Long orderId, User user) {
        Order order = getOrderByIdAndUser(orderId, user);
        if (order == null) {
            return false;
        }
        if (order.getStatus() != Order.STATUS_WAIT_PAY) {
            return false;
        }
        order.setStatus(Order.STATUS_PAID);
        orderRepository.save(order);
        return true;
    }
}