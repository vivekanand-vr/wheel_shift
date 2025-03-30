package com.wheelshift.service;

import com.wheelshift.dto.ColumnDTO;
import com.wheelshift.dto.KanbanBoardDTO;
import com.wheelshift.dto.TaskDTO;
import com.wheelshift.dto.TaskMoveDTO;
import com.wheelshift.model.KanbanColumn;
import com.wheelshift.model.Task;
import com.wheelshift.model.TaskPosition;
import com.wheelshift.repository.KanbanColumnRepository;
import com.wheelshift.repository.TaskPositionRepository;
import com.wheelshift.repository.TaskRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KanbanService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private KanbanColumnRepository columnRepository;
    
    @Autowired
    private TaskPositionRepository taskPositionRepository;

    /**
     * Get the entire kanban board data
     */
    public KanbanBoardDTO getKanbanBoard() {
        KanbanBoardDTO boardDTO = new KanbanBoardDTO();
        
        // Get all tasks
        List<Task> tasks = taskRepository.findAll();
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toList());
        boardDTO.setTasks(taskDTOs);
        
        // Get all columns
        List<KanbanColumn> columns = columnRepository.findAllByOrderByPositionAsc();
        List<ColumnDTO> columnDTOs = new ArrayList<>();
        List<String> columnOrder = new ArrayList<>();
        
        for (KanbanColumn column : columns) {
            ColumnDTO columnDTO = new ColumnDTO();
            columnDTO.setId(column.getColumnId());
            columnDTO.setTitle(column.getTitle());
            
            // Get all task IDs in this column in the correct order
            List<String> taskIds = column.getTaskPositions().stream()
                    .sorted(Comparator.comparing(TaskPosition::getPosition))
                    .map(tp -> tp.getTask().getTaskId())
                    .collect(Collectors.toList());
            
            columnDTO.setTaskIds(taskIds);
            columnDTOs.add(columnDTO);
            columnOrder.add(column.getColumnId());
        }
        
        boardDTO.setColumns(columnDTOs);
        boardDTO.setColumnOrder(columnOrder);
        
        return boardDTO;
    }

    /**
     * Move a task from one position to another (possibly between columns)
     */
    @Transactional
    public void moveTask(TaskMoveDTO moveDTO) {
        Task task = taskRepository.findByTaskId(moveDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        KanbanColumn sourceColumn = columnRepository.findByColumnId(moveDTO.getSourceColumnId())
                .orElseThrow(() -> new RuntimeException("Source column not found"));
        
        KanbanColumn destColumn = columnRepository.findByColumnId(moveDTO.getDestinationColumnId())
                .orElseThrow(() -> new RuntimeException("Destination column not found"));
        
        // If moving within the same column
        if (sourceColumn.getId().equals(destColumn.getId())) {
            reorderTasksInColumn(sourceColumn, moveDTO.getSourceIndex(), moveDTO.getDestinationIndex());
        } else {
            // Moving from one column to another
            TaskPosition taskPosition = taskPositionRepository.findByTaskAndColumn(task, sourceColumn)
                    .orElseThrow(() -> new RuntimeException("Task position not found"));
            
            // Remove from source column
            taskPositionRepository.delete(taskPosition);
            
            // Update positions in source column
            List<TaskPosition> sourcePositions = taskPositionRepository.findByColumnOrderByPositionAsc(sourceColumn);
            for (int i = moveDTO.getSourceIndex(); i < sourcePositions.size(); i++) {
                TaskPosition tp = sourcePositions.get(i);
                tp.setPosition(tp.getPosition() - 1);
                taskPositionRepository.save(tp);
            }
            
            // Add to destination column
            TaskPosition newPosition = new TaskPosition();
            newPosition.setTask(task);
            newPosition.setColumn(destColumn);
            newPosition.setPosition(moveDTO.getDestinationIndex());
            
            // Update positions in destination column
            List<TaskPosition> destPositions = taskPositionRepository.findByColumnOrderByPositionAsc(destColumn);
            for (int i = moveDTO.getDestinationIndex(); i < destPositions.size(); i++) {
                TaskPosition tp = destPositions.get(i);
                tp.setPosition(tp.getPosition() + 1);
                taskPositionRepository.save(tp);
            }
            
            taskPositionRepository.save(newPosition);
            
            // Update task's column reference
            task.setColumn(destColumn);
            taskRepository.save(task);
        }
    }
    
    /**
     * Reorder tasks within a column
     */
    private void reorderTasksInColumn(KanbanColumn column, int sourceIndex, int destIndex) {
        List<TaskPosition> positions = taskPositionRepository.findByColumnOrderByPositionAsc(column);
        
        if (sourceIndex < destIndex) {
            // Moving down
            for (int i = 0; i < positions.size(); i++) {
                TaskPosition tp = positions.get(i);
                if (i == sourceIndex) {
                    tp.setPosition(destIndex);
                } else if (i > sourceIndex && i <= destIndex) {
                    tp.setPosition(tp.getPosition() - 1);
                }
                taskPositionRepository.save(tp);
            }
        } else {
            // Moving up
            for (int i = 0; i < positions.size(); i++) {
                TaskPosition tp = positions.get(i);
                if (i == sourceIndex) {
                    tp.setPosition(destIndex);
                } else if (i >= destIndex && i < sourceIndex) {
                    tp.setPosition(tp.getPosition() + 1);
                }
                taskPositionRepository.save(tp);
            }
        }
    }

    /**
     * Create a new task
     */
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, String columnId) {
        KanbanColumn column = columnRepository.findByColumnId(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        
        Task task = new Task();
        String newTaskId = "task-" + UUID.randomUUID().toString().substring(0, 8);
        task.setTaskId(newTaskId);
        updateTaskFromDTO(task, taskDTO);
        task.setColumn(column);
        
        task = taskRepository.save(task);
        
        // Add task to the end of the column
        Integer maxPosition = taskPositionRepository.findMaxPositionInColumn(column);
        int newPosition = (maxPosition != null) ? maxPosition + 1 : 0;
        
        TaskPosition taskPosition = new TaskPosition();
        taskPosition.setTask(task);
        taskPosition.setColumn(column);
        taskPosition.setPosition(newPosition);
        taskPositionRepository.save(taskPosition);
        
        taskDTO.setId(newTaskId);
        return taskDTO;
    }

    /**
     * Update an existing task
     */
    @Transactional
    public TaskDTO updateTask(String taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        updateTaskFromDTO(task, taskDTO);
        taskRepository.save(task);
        
        return convertToTaskDTO(task);
    }

    /**
     * Delete a task
     */
    @Transactional
    public void deleteTask(String taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        KanbanColumn column = task.getColumn();
        
        // Find and delete the task position
        TaskPosition taskPosition = taskPositionRepository.findByTaskAndColumn(task, column)
                .orElseThrow(() -> new RuntimeException("Task position not found"));
        
        int deletedPosition = taskPosition.getPosition();
        taskPositionRepository.delete(taskPosition);
        
        // Update positions for tasks after the deleted one
        List<TaskPosition> positions = taskPositionRepository.findByColumnOrderByPositionAsc(column);
        for (TaskPosition tp : positions) {
            if (tp.getPosition() > deletedPosition) {
                tp.setPosition(tp.getPosition() - 1);
                taskPositionRepository.save(tp);
            }
        }
        
        // Finally delete the task
        taskRepository.delete(task);
    }

    /**
     * Create a new column
     */
    @Transactional
    public ColumnDTO createColumn(ColumnDTO columnDTO) {
        KanbanColumn column = new KanbanColumn();
        String newColumnId = "column-" + UUID.randomUUID().toString().substring(0, 8);
        column.setColumnId(newColumnId);
        column.setTitle(columnDTO.getTitle());
        
        // Find max position and add 1
        Integer maxPosition = columnRepository.findAll().stream()
                .map(KanbanColumn::getPosition)
                .max(Integer::compareTo)
                .orElse(-1);
        column.setPosition(maxPosition + 1);
        
        columnRepository.save(column);
        
        columnDTO.setId(newColumnId);
        columnDTO.setTaskIds(new ArrayList<>());
        return columnDTO;
    }

    /**
     * Update a column
     */
    @Transactional
    public ColumnDTO updateColumn(String columnId, ColumnDTO columnDTO) {
        KanbanColumn column = columnRepository.findByColumnId(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        
        column.setTitle(columnDTO.getTitle());
        columnRepository.save(column);
        
        return convertToColumnDTO(column);
    }

    /**
     * Delete a column
     */
    @Transactional
    public void deleteColumn(String columnId) {
        KanbanColumn column = columnRepository.findByColumnId(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        
        // Delete all task positions in this column
        List<TaskPosition> positions = taskPositionRepository.findByColumnOrderByPositionAsc(column);
        taskPositionRepository.deleteAll(positions);
        
        // Update positions for columns after the deleted one
        int deletedPosition = column.getPosition();
        List<KanbanColumn> columns = columnRepository.findAllByOrderByPositionAsc();
        for (KanbanColumn c : columns) {
            if (c.getPosition() > deletedPosition) {
                c.setPosition(c.getPosition() - 1);
                columnRepository.save(c);
            }
        }
        
        // Finally delete the column
        columnRepository.delete(column);
    }

    private void updateTaskFromDTO(Task task, TaskDTO dto) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssignee(dto.getAssignee());
        task.setDueDate(dto.getDueDate());
        
        // Convert priority string to enum
        if (dto.getPriority() != null) {
            switch (dto.getPriority().toLowerCase()) {
                case "high":
                    task.setPriority(Task.Priority.HIGH);
                    break;
                case "medium":
                    task.setPriority(Task.Priority.MEDIUM);
                    break;
                case "low":
                    task.setPriority(Task.Priority.LOW);
                    break;
                default:
                    task.setPriority(Task.Priority.MEDIUM);
            }
        }
        
        if (dto.getTags() != null) {
            task.setTags(dto.getTags());
        }
    }
    
    private TaskDTO convertToTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getTaskId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setAssignee(task.getAssignee());
        dto.setDueDate(task.getDueDate());
        
        // Convert priority enum to string
        if (task.getPriority() != null) {
            dto.setPriority(task.getPriority().name().toLowerCase());
        }
        
        dto.setTags(task.getTags());
        return dto;
    }
    
    private ColumnDTO convertToColumnDTO(KanbanColumn column) {
        ColumnDTO dto = new ColumnDTO();
        dto.setId(column.getColumnId());
        dto.setTitle(column.getTitle());
        
        // Get task IDs in order
        List<String> taskIds = column.getTaskPositions().stream()
                .sorted(Comparator.comparing(TaskPosition::getPosition))
                .map(tp -> tp.getTask().getTaskId())
                .collect(Collectors.toList());
        
        dto.setTaskIds(taskIds);
        return dto;
    }
}