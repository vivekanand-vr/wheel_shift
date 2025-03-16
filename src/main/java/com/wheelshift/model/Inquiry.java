package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries", indexes = {
    @Index(name = "idx_inquiry_status", columnList = "status"),
    @Index(name = "idx_inquiry_car", columnList = "car_id"),
    @Index(name = "idx_inquiry_client", columnList = "client_id"),
    @Index(name = "idx_inquiry_employee", columnList = "assigned_employee_id"),
    @Index(name = "idx_inquiry_created_at", columnList = "created_at")
})
@Data
public class Inquiry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonBackReference("car-inquiries")
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference("client-inquiries")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    @JsonBackReference("employee-inquiries")
    private Employee assignedEmployee;
    
    @Column(nullable = false)
    private String inquiryType;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(nullable = false)
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String response;
    
    private LocalDateTime responseDate;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
