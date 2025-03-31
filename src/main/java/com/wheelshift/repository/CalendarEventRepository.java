package com.wheelshift.repository;

import com.wheelshift.model.CalendarEvent;
import com.wheelshift.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * Find all events between start and end dates
     */
    List<CalendarEvent> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);

    /**
     * Find all events by event type
     */
    List<CalendarEvent> findByEventTypeOrderByStartTimeAsc(EventType eventType);

    /**
     * Find all events for a specific customer
     */
    List<CalendarEvent> findByCustomerNameContainingIgnoreCaseOrderByStartTimeAsc(String customerName);

    /**
     * Find events for a specific car
     */
    List<CalendarEvent> findByCarDetailsContainingIgnoreCaseOrderByStartTimeAsc(String carDetails);

    /**
     * Find events by type within a date range
     */
    List<CalendarEvent> findByEventTypeAndStartTimeBetweenOrderByStartTimeAsc(
            EventType eventType, LocalDateTime start, LocalDateTime end);

    /**
     * Count events by type within a date range
     */
    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.eventType = :eventType " +
           "AND e.startTime BETWEEN :start AND :end")
    Long countByEventTypeAndDateRange(
            @Param("eventType") EventType eventType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Get summary count of events by type within a date range
     */
    @Query("SELECT e.eventType as type, COUNT(e) as count FROM CalendarEvent e " +
           "WHERE e.startTime BETWEEN :start AND :end GROUP BY e.eventType")
    List<Object[]> getEventTypeSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}