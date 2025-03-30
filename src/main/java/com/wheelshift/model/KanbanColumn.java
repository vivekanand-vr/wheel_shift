package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "kanban_columns")
public class KanbanColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String columnId; // Unique client-side ID (e.g., 'column-1')
    private String title;
    private Integer position; // To maintain columnOrder
    
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<TaskPosition> taskPositions = new ArrayList<>();
}