package com.example.demo.admin.service;

// 必须和Repository/实体类的包路径完全一致
import com.example.demo.entity.OperationLog;
import com.example.demo.admin.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class OperationLogService {
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    // 记录操作日志（业务逻辑完全不变）
    @Transactional
    public void logOperation(Long adminId, String operation, HttpServletRequest request) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setOperation(operation);
        log.setIpAddress(request.getRemoteAddr());
        operationLogRepository.save(log);
    }
    
    // 获取所有操作日志（返回值类型和Repository完全匹配）
    public List<OperationLog> getAllLogs() {
        return operationLogRepository.findAllByOrderByCreateTimeDesc();
    }
}