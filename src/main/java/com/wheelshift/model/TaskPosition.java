package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "task_positions")
public class TaskPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "column_id")
    private KanbanColumn column;
    
    private Integer position; // Position within the column
}