package com.example.demo.admin.repository;

// 必须和Service/实体类的包路径完全一致
import com.example.demo.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    // 返回值类型必须和泛型一致，不能写List<com.example.demo.entity.OperationLog>
    List<OperationLog> findAllByOrderByCreateTimeDesc();
}