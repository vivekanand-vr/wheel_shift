package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_employee_email", columnList = "email", unique = true),
    @Index(name = "idx_employee_position", columnList = "position"),
    @Index(name = "idx_employee_department", columnList = "department"),
    @Index(name = "idx_employee_status", columnList = "status")
})
@Data
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String position;
    
    @Column(nullable = false)
    private String department;
    
    @Column(nullable = false)
    private LocalDate joinDate;
    
    @Column(nullable = false)
    private String status;
    
    private String performance;
    
    private LocalDateTime lastLogin;
    
    @OneToMany(mappedBy = "assignedEmployee")
    @JsonManagedReference("employee-inquiries")
    private List<Inquiry> assignedInquiries = new ArrayList<>();
    
    @OneToMany(mappedBy = "handledBy")
    @JsonManagedReference("employee-sales")
    private List<Sale> handledSales = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}