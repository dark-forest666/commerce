package com.example.demo.admin.repository;
import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long> {
    // 按状态查询订单
    List<Order> findByStatus(Integer status);

    // 模糊搜索订单（订单号/收货人/手机号）
    @Query("SELECT o FROM Order o LEFT JOIN o.address a " +
           "WHERE o.orderNo LIKE CONCAT('%', :keyword, '%') " +
           "OR a.receiverName LIKE CONCAT('%', :keyword, '%') " +
           "OR a.phone LIKE CONCAT('%', :keyword, '%')")
    List<Order> searchOrders(@Param("keyword") String keyword);

    // 【修复】按创建时间范围查询订单（方法名符合JPA规范）
    @Query("SELECT o FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    List<Order> findByCreateTimeBetween(@Param("start") Date start, @Param("end") Date end);

    // 总销售额（所有已支付及以上状态的订单）
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status >= 1")
    Double getTotalSales();

    // 【修复】统计指定时间范围内的订单数（方法名符合JPA规范）
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    Long countByCreateTimeBetween(@Param("start") Date start, @Param("end") Date end);

    // 【修复】统计指定时间范围内的销售额（方法名符合JPA规范）
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createTime BETWEEN :start AND :end AND o.status >= 1")
    Double getSalesByCreateTimeBetween(@Param("start") Date start, @Param("end") Date end);

    // 查询订单时一次性加载关联的地址和订单项（解决懒加载500）
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.address " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.product " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithAllDetails(@Param("id") Long id);
}