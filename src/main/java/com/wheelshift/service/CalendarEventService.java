package com.wheelshift.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wheelshift.dto.CalendarEventDTO;
import com.wheelshift.dto.EventSummaryDTO;
import com.wheelshift.model.CalendarEvent;
import com.wheelshift.model.EventType;
import com.wheelshift.repository.CalendarEventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository eventRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Create a new calendar event
     *
     * @param eventDTO the event data transfer object
     * @return the created event DTO
     */
    public CalendarEventDTO createEvent(CalendarEventDTO eventDTO) {
        log.info("Creating new calendar event: {}", eventDTO.getTitle());
        
        CalendarEvent event = mapToEntity(eventDTO);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("system"); // Replace with actual user when authentication is implemented
        
        CalendarEvent savedEvent = eventRepository.save(event);
        return mapToDTO(savedEvent);
    }

    /**
     * Update an existing calendar event
     *
     * @param id the event ID
     * @param eventDTO the updated event data
     * @return the updated event DTO
     */
    public CalendarEventDTO updateEvent(Long id, CalendarEventDTO eventDTO) {
        log.info("Updating calendar event with ID: {}", id);
        
        CalendarEvent existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        
        // Update fields
        existingEvent.setTitle(eventDTO.getTitle());
        existingEvent.setStartTime(LocalDateTime.parse(eventDTO.getStart(), ISO_FORMATTER));
        existingEvent.setEndTime(LocalDateTime.parse(eventDTO.getEnd(), ISO_FORMATTER));
        existingEvent.setBackgroundColor(eventDTO.getBackgroundColor());
        existingEvent.setBorderColor(eventDTO.getBorderColor());
        existingEvent.setEventType(eventDTO.getEventType());
        existingEvent.setCustomerName(eventDTO.getCustomerName());
        existingEvent.setCarDetails(eventDTO.getCarDetails());
        existingEvent.setUpdatedAt(LocalDateTime.now());
        existingEvent.setUpdatedBy("system"); // Replace with actual user when authentication is implemented
        
        CalendarEvent updatedEvent = eventRepository.save(existingEvent);
        return mapToDTO(updatedEvent);
    }

    /**
     * Delete a calendar event by ID
     *
     * @param id the event ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteEvent(Long id) {
        log.info("Deleting calendar event with ID: {}", id);
        
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get an event by ID
     *
     * @param id the event ID
     * @return the event DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<CalendarEventDTO> getEventById(Long id) {
        log.info("Fetching calendar event with ID: {}", id);
        
        return eventRepository.findById(id)
                .map(this::mapToDTO);
    }

    /**
     * Get all events within a date range (for month view)
     *
     * @param start the start date
     * @param end the end date
     * @return list of events in the date range
     */
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching calendar events between {} and {}", start, end);
        
        return eventRepository.findByStartTimeBetweenOrderByStartTimeAsc(start, end)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get events for a specific week
     *
     * @param weekStart the start date of the week
     * @return list of events for the week
     */
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsForWeek(LocalDateTime weekStart) {
        LocalDateTime weekEnd = weekStart.plusDays(7);
        log.info("Fetching calendar events for week starting {} and ending {}", weekStart, weekEnd);
        
        return getEventsByDateRange(weekStart, weekEnd);
    }

    /**
     * Get events for a specific day
     *
     * @param day the day
     * @return list of events for the day
     */
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsForDay(LocalDateTime day) {
        LocalDateTime dayEnd = day.plusDays(1);
        log.info("Fetching calendar events for day: {}", day);
        
        return getEventsByDateRange(day, dayEnd);
    }

    /**
     * Get events summary for a date range
     *
     * @param start the start date
     * @param end the end date
     * @return summary of events
     */
    @Transactional(readOnly = true)
    public EventSummaryDTO getEventsSummary(LocalDateTime start, LocalDateTime end) {
        log.info("Generating events summary between {} and {}", start, end);
        
        List<Object[]> summaryData = eventRepository.getEventTypeSummary(start, end);
        Map<String, Integer> countByType = new HashMap<>();
        int totalCount = 0;
        
        for (Object[] row : summaryData) {
            EventType type = (EventType) row[0];
            Long count = (Long) row[1];
            countByType.put(type.getValue(), count.intValue());
            totalCount += count.intValue();
        }
        
        return EventSummaryDTO.builder()
                .totalAppointments(totalCount)
                .countByType(countByType)
                .build();
    }

    // Helper methods for entity-DTO mapping
    private CalendarEventDTO mapToDTO(CalendarEvent event) {
        return CalendarEventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .start(event.getStartTime().format(ISO_FORMATTER))
                .end(event.getEndTime().format(ISO_FORMATTER))
                .backgroundColor(event.getBackgroundColor())
                .borderColor(event.getBorderColor())
                .eventType(event.getEventType())
                .customerName(event.getCustomerName())
                .carDetails(event.getCarDetails())
                .build();
    }

    private CalendarEvent mapToEntity(CalendarEventDTO dto) {
        return CalendarEvent.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .startTime(LocalDateTime.parse(dto.getStart(), ISO_FORMATTER))
                .endTime(LocalDateTime.parse(dto.getEnd(), ISO_FORMATTER))
                .backgroundColor(dto.getBackgroundColor())
                .borderColor(dto.getBorderColor())
                .eventType(dto.getEventType())
                .customerName(dto.getCustomerName())
                .carDetails(dto.getCarDetails())
                .build();
    }
}