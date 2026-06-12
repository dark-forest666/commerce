package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreateTimeDesc(User user);

    // 新增：按状态查询订单
    List<Order> findByStatus(Integer status);

    // 新增：模糊搜索订单
    @Query("SELECT o FROM Order o WHERE o.orderNo LIKE %:keyword% OR o.address.receiverName LIKE %:keyword% OR o.address.phone LIKE %:keyword%")
    List<Order> searchOrders(@Param("keyword") String keyword);

    // 新增：按时间范围查询订单
    @Query("SELECT o FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    List<Order> findOrdersByTimeRange(@Param("start") Date start, @Param("end") Date end);

    // 新增：总销售额
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status >= 1")
    Double getTotalSales();

    // 新增：统计指定时间范围内的订单数
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    Long countOrdersByTimeRange(@Param("start") Date start, @Param("end") Date end);

    // 新增：统计指定时间范围内的销售额
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createTime BETWEEN :start AND :end AND o.status >= 1")
    Double getSalesByTimeRange(@Param("start") Date start, @Param("end") Date end);
}