package com.wheelshift.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wheelshift.dto.CalendarEventDTO;
import com.wheelshift.dto.EventSummaryDTO;
import com.wheelshift.service.CalendarEventService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService eventService;

    @PostMapping("/events")
    public ResponseEntity<CalendarEventDTO> createEvent(@RequestBody CalendarEventDTO eventDTO) {
        CalendarEventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEventDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody CalendarEventDTO eventDTO) {
        
        CalendarEventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<CalendarEventDTO> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events/range")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        List<CalendarEventDTO> events = eventService.getEventsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/month")
    public ResponseEntity<List<CalendarEventDTO>> getEventsForMonth(
            @RequestParam int year,
            @RequestParam int month) {
        
        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        
        List<CalendarEventDTO> events = eventService.getEventsByDateRange(startOfMonth, endOfMonth);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/week")
    public ResponseEntity<List<CalendarEventDTO>> getEventsForWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        LocalDateTime startOfWeek = startDate.atStartOfDay();
        List<CalendarEventDTO> events = eventService.getEventsForWeek(startOfWeek);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/day")
    public ResponseEntity<List<CalendarEventDTO>> getEventsForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDateTime startOfDay = date.atStartOfDay();
        List<CalendarEventDTO> events = eventService.getEventsForDay(startOfDay);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/summary")
    public ResponseEntity<EventSummaryDTO> getEventsSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        EventSummaryDTO summary = eventService.getEventsSummary(startDateTime, endDateTime);
        return ResponseEntity.ok(summary);
    }
}