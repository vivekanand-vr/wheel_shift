package com.wheelshift.controller;

import com.wheelshift.dto.ColumnDTO;
import com.wheelshift.dto.KanbanBoardDTO;
import com.wheelshift.dto.TaskDTO;
import com.wheelshift.dto.TaskMoveDTO;
import com.wheelshift.service.KanbanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kanban")
public class KanbanController {
	
	public KanbanController(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}
	
    private final KanbanService kanbanService;
    
    /**
     * Get the entire kanban board data
     */
    @GetMapping("/board")
    public ResponseEntity<KanbanBoardDTO> getKanbanBoard() {
        return ResponseEntity.ok(kanbanService.getKanbanBoard());
    }
    
    /**
     * Move a task (drag-and-drop)
     */
    @PostMapping("/move-task")
    public ResponseEntity<String> moveTask(@RequestBody TaskMoveDTO moveDTO) {
        kanbanService.moveTask(moveDTO);
        return ResponseEntity.ok("Task moved successfully");
    }
    
    /**
     * Tasks CRUD operations
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody TaskDTO taskDTO,
            @RequestParam("columnId") String columnId) {
        return ResponseEntity.ok(kanbanService.createTask(taskDTO, columnId));
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(kanbanService.updateTask(taskId, new TaskDTO()));
    }
    
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable String taskId,
            @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(kanbanService.updateTask(taskId, taskDTO));
    }
    
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String taskId) {
        kanbanService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }
    
    /**
     * Columns CRUD operations
     */
    @PostMapping("/columns")
    public ResponseEntity<ColumnDTO> createColumn(@RequestBody ColumnDTO columnDTO) {
        return ResponseEntity.ok(kanbanService.createColumn(columnDTO));
    }
    
    @PutMapping("/columns/{columnId}")
    public ResponseEntity<ColumnDTO> updateColumn(
            @PathVariable String columnId,
            @RequestBody ColumnDTO columnDTO) {
        return ResponseEntity.ok(kanbanService.updateColumn(columnId, columnDTO));
    }
    
    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable String columnId) {
        kanbanService.deleteColumn(columnId);
        return ResponseEntity.ok("Column deleted successfully");
    }
}