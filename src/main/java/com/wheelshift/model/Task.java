package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String taskId; // Unique client-side ID (e.g., 'task-1')
    private String title;
    private String description;
    private String assignee;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @ElementCollection
    @CollectionTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "column_id")
    private KanbanColumn column;
    
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}