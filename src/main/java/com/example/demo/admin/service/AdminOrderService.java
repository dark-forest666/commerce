package com.example.demo.admin.service;
import com.example.demo.admin.repository.AdminOrderRepository;
import com.example.demo.admin.repository.AdminProductRepository;
import com.example.demo.admin.repository.AdminUserRepository;
import com.example.demo.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AdminOrderService {
    @Autowired
    private AdminOrderRepository adminOrderRepository;
    @Autowired
    private AdminUserRepository adminUserRepository;
    @Autowired
    private AdminProductRepository adminProductRepository;

    public List<Order> getAllOrders() {
        return adminOrderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        try {
            log.info("查询订单详情，订单ID：{}", id);
            return adminOrderRepository.findByIdWithAllDetails(id).orElse(null);
        } catch (Exception e) {
            log.error("查询订单详情失败", e);
            return null;
        }
    }

    @Transactional
    public void shipOrder(Long id, String expressNo, String adminRemark) {
        Order order = getOrderById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != Order.STATUS_PAID) {
            throw new RuntimeException("仅已支付订单可发货");
        }
        order.setStatus(Order.STATUS_SHIPPED);
        order.setExpressNo(expressNo);
        order.setAdminRemark(adminRemark);
        adminOrderRepository.save(order);
        log.info("订单{}发货成功", id);
    }

    @Transactional
    public void batchShip(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请先选择至少一个已支付订单");
        }
        int successCount = 0;
        for (Long id : ids) {
            try {
                Order order = getOrderById(id);
                if (order != null && order.getStatus() == Order.STATUS_PAID) {
                    order.setStatus(Order.STATUS_SHIPPED);
                    adminOrderRepository.save(order);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量发货失败，订单ID：{}", id, e);
            }
        }
        if (successCount == 0) {
            throw new RuntimeException("没有可发货的订单");
        }
        log.info("批量发货完成，成功{}个订单", successCount);
    }

    public List<Order> searchOrders(String keyword) {
        return adminOrderRepository.searchOrders(keyword);
    }

    public List<Order> getOrdersByStatus(Integer status) {
        return adminOrderRepository.findByStatus(status);
    }

    // 【修复】调用修改后的Repository方法
    public List<Order> getOrdersByTimeRange(Date start, Date end) {
        return adminOrderRepository.findByCreateTimeBetween(start, end);
    }

    private Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getTodayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public long getTotalNormalUsers() {
        return adminUserRepository.countByRoleNot("admin");
    }

    public long getTotalAdminUsers() {
        return adminUserRepository.countByRole("admin");
    }

    public long getTotalProducts() {
        return adminProductRepository.count();
    }

    public long getTotalOrders() {
        return adminOrderRepository.count();
    }

    public double getTotalSales() {
        return adminOrderRepository.getTotalSales();
    }

    public long getTodayNewUsers() {
        return adminUserRepository.countByCreateTimeBetween(getTodayStart(), getTodayEnd());
    }

    // 【修复】调用修改后的Repository方法
    public long getTodayOrders() {
        return adminOrderRepository.countByCreateTimeBetween(getTodayStart(), getTodayEnd());
    }

    // 【修复】调用修改后的Repository方法
    public double getTodaySales() {
        return adminOrderRepository.getSalesByCreateTimeBetween(getTodayStart(), getTodayEnd());
    }

    public List<Order> getPendingShipOrders() {
        return adminOrderRepository.findByStatus(Order.STATUS_PAID);
    }
}