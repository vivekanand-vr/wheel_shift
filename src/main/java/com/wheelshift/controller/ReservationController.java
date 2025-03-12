package com.wheelshift.controller;

import com.wheelshift.model.Reservation;
import com.wheelshift.service.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    
    /**
	 *	   _____ _____  _    _ _____  
	 *	  / ____|  __ \| |  | |  __ \ 
	 *	 | |    | |__) | |  | | |  | |
	 *	 | |    |  _  /| |  | | |  | |
	 *	 | |____| | \ \| |__| | |__| |
	 *	  \_____|_|  \_\\____/|_____/ 
	 *	                                                   
     *				CRUD OPERATIONS
     */
    
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation createdReservation = reservationService.createReservation(reservation);
        return createdReservation != null 
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdReservation) 
                : ResponseEntity.badRequest().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id, 
            @RequestBody Reservation reservation) {
        Reservation updatedReservation = reservationService.updateReservation(id, reservation);
        return updatedReservation != null 
                ? ResponseEntity.ok(updatedReservation) 
                : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
   	 *	   _____ ______          _____   _____ _    _ 
   	 *	  / ____|  ____|   /\   |  __ \ / ____| |  | |
   	 *	 | (___ | |__     /  \  | |__) | |    | |__| |
   	 *	  \___ \|  __|   / /\ \ |  _  /| |    |  __  |
   	 *	  ____) | |____ / ____ \| | \ \| |____| |  | |
   	 *	 |_____/|______/_/    \_\_|  \_\\_____|_|  |_|
   	 *	                                              
   	 *				SEARCH & FILTERS OPERATIONS
     */ 
    
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Reservation>> getReservationsByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(reservationService.getReservationsByCar(carId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }
    
    @GetMapping("/deposit-status")
    public ResponseEntity<List<Reservation>> getReservationsByDepositStatus(@RequestParam Boolean depositPaid) {
        return ResponseEntity.ok(reservationService.getReservationsByDepositStatus(depositPaid));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Reservation>> getReservationsByCustomerEmail(@PathVariable String email) {
        return ResponseEntity.ok(reservationService.getReservationsByCustomerEmail(email));
    }
    
    @GetMapping("/customer/{name}")
    public ResponseEntity<List<Reservation>> getReservationsByCustomerName(@PathVariable String name) {
        return ResponseEntity.ok(reservationService.getReservationsByCustomerName(name));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Reservation>> getReservationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reservationService.getReservationsByDateRange(startDate, endDate));
    }
    
    /**
   	 *	  ____  _    _  _____ _____ _   _ ______  _____ _____   _      ____   _____ _____ _____ 
   	 *	 |  _ \| |  | |/ ____|_   _| \ | |  ____|/ ____/ ____| | |    / __ \ / ____|_   _/ ____|
   	 *	 | |_) | |  | | (___   | | |  \| | |__  | (___| (___   | |   | |  | | |  __  | || |     
   	 *	 |  _ <| |  | |\___ \  | | | . ` |  __|  \___ \\___ \  | |   | |  | | | |_ | | || |     
   	 *	 | |_) | |__| |____) |_| |_| |\  | |____ ____) |___) | | |___| |__| | |__| |_| || |____ 
   	 * 	 |____/ \____/|_____/|_____|_| \_|______|_____/_____/  |______\____/ \_____|_____\_____|
     *                                                                                   
     *				BUSINESS LOGIC & TRANSACTIONS                                                                                   
     */
    
    @PostMapping("/{reservationId}/mark-deposit-paid")
    public ResponseEntity<Reservation> markDepositPaid(
            @PathVariable Long reservationId, 
            @RequestParam BigDecimal amount) {
        Reservation updatedReservation = reservationService.markDepositPaid(reservationId, amount);
        return updatedReservation != null 
                ? ResponseEntity.ok(updatedReservation) 
                : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long reservationId) {
        Reservation cancelledReservation = reservationService.cancelReservation(reservationId);
        return cancelledReservation != null 
                ? ResponseEntity.ok(cancelledReservation) 
                : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{reservationId}/complete")
    public ResponseEntity<Reservation> completeReservation(@PathVariable Long reservationId) {
        Reservation completedReservation = reservationService.completeReservation(reservationId);
        return completedReservation != null 
                ? ResponseEntity.ok(completedReservation) 
                : ResponseEntity.notFound().build();
    }
    
    /**
     *  	____ _______    _______ _____ 
	 *	  / ____|__   __|/\|__   __/ ____|
	 *	 | (___    | |  /  \  | | | (___  
	 *	  \___ \   | | / /\ \ | |  \___ \ 
	 *	  ____) |  | |/ ____ \| |  ____) |
	 *	 |_____/   |_/_/    \_\_| |_____/ 
	 *
	 *				STATISTICS AND ANALYTICS
     */
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReservationStatistics() {
        return ResponseEntity.ok(reservationService.getReservationStatistics());
    }
    
    @GetMapping("/active/car/{carId}")
    public ResponseEntity<List<Reservation>> getActiveReservationsByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(reservationService.getActiveReservationsByCar(carId));
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<List<Reservation>> getExpiringReservations(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(reservationService.getExpiringReservations(days));
    }
}