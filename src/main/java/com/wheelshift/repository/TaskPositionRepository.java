package com.wheelshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wheelshift.model.KanbanColumn;
import com.wheelshift.model.Task;
import com.wheelshift.model.TaskPosition;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPositionRepository extends JpaRepository<TaskPosition, Long> {
    List<TaskPosition> findByColumnOrderByPositionAsc(KanbanColumn column);
    Optional<TaskPosition> findByTaskAndColumn(Task task, KanbanColumn column);
    
    @Query("SELECT MAX(tp.position) FROM TaskPosition tp WHERE tp.column = ?1")
    Integer findMaxPositionInColumn(KanbanColumn column);
}