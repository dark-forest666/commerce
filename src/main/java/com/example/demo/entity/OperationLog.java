package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;       // 管理员ID
    private String operation;   // 操作内容
    private String ipAddress;   // 操作IP

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;    // 操作时间

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}