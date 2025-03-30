package com.wheelshift.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private String assignee;
    private LocalDate dueDate;
    private String priority;
    private Set<String> tags;
}