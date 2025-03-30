package com.wheelshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wheelshift.model.KanbanColumn;

import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanColumnRepository extends JpaRepository<KanbanColumn, Long> {
    Optional<KanbanColumn> findByColumnId(String columnId);
    List<KanbanColumn> findAllByOrderByPositionAsc();
}