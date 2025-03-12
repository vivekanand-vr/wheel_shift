package com.wheelshift.controller;

import com.wheelshift.model.Inquiry;
import com.wheelshift.service.InquiryService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    
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
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiryById(@PathVariable Long id) {
        return inquiryService.getInquiryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Inquiry> createInquiry(@RequestBody Inquiry inquiry) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inquiryService.saveInquiry(inquiry));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Inquiry> updateInquiry(
            @PathVariable Long id, 
            @RequestBody Inquiry inquiry) {
        Inquiry updatedInquiry = inquiryService.updateInquiry(id, inquiry);
        return updatedInquiry != null 
                ? ResponseEntity.ok(updatedInquiry) 
                : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
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
    public ResponseEntity<List<Inquiry>> getInquiriesByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByCar(carId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Inquiry>> getInquiriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(inquiryService.getInquiriesByStatus(status));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Inquiry>> getInquiriesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByUser(userId));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Inquiry>> getInquiriesByCustomerEmail(@PathVariable String email) {
        return ResponseEntity.ok(inquiryService.getInquiriesByCustomerEmail(email));
    }
    
    @GetMapping("/customer/{name}")
    public ResponseEntity<List<Inquiry>> getInquiriesByCustomerName(@PathVariable String name) {
        return ResponseEntity.ok(inquiryService.getInquiriesByCustomerName(name));
    }
    
    @GetMapping("/type/{inquiryType}")
    public ResponseEntity<List<Inquiry>> getInquiriesByType(@PathVariable String inquiryType) {
        return ResponseEntity.ok(inquiryService.getInquiriesByType(inquiryType));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Inquiry>> getInquiriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(inquiryService.getInquiriesByDateRange(startDate, endDate));
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
    
    @PostMapping("/{inquiryId}/assign/{userId}")
    public ResponseEntity<Inquiry> assignInquiry(
            @PathVariable Long inquiryId, 
            @PathVariable Long userId) {
        Inquiry assignedInquiry = inquiryService.assignInquiry(inquiryId, userId);
        return assignedInquiry != null 
                ? ResponseEntity.ok(assignedInquiry) 
                : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{inquiryId}/respond")
    public ResponseEntity<Inquiry> respondToInquiry(
            @PathVariable Long inquiryId, 
            @RequestBody String response) {
        Inquiry respondedInquiry = inquiryService.respondToInquiry(inquiryId, response);
        return respondedInquiry != null 
                ? ResponseEntity.ok(respondedInquiry) 
                : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{inquiryId}/close")
    public ResponseEntity<Inquiry> closeInquiry(@PathVariable Long inquiryId) {
        Inquiry closedInquiry = inquiryService.closeInquiry(inquiryId);
        return closedInquiry != null 
                ? ResponseEntity.ok(closedInquiry) 
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
    public ResponseEntity<Map<String, Object>> getInquiryStatistics() {
        return ResponseEntity.ok(inquiryService.getInquiryStatistics());
    }
    
    @GetMapping("/pending-sorted")
    public ResponseEntity<List<Inquiry>> getPendingInquiriesSortedByCreationDate() {
        return ResponseEntity.ok(inquiryService.getPendingInquiriesSortedByCreationDate());
    }
    
    @GetMapping("/active/car/{carId}")
    public ResponseEntity<List<Inquiry>> getActiveInquiriesByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(inquiryService.getActiveInquiriesByCar(carId));
    }
}