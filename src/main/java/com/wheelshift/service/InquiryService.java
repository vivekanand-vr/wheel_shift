package com.wheelshift.service;

import com.wheelshift.model.Car;
import com.wheelshift.model.Inquiry;
import com.wheelshift.model.User;
import com.wheelshift.repository.CarRepository;
import com.wheelshift.repository.InquiryRepository;
import com.wheelshift.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {
    
    private final InquiryRepository inquiryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    
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

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }
    
    public Optional<Inquiry> getInquiryById(Long id) {
        return inquiryRepository.findById(id);
    }
    
    @Transactional
    public Inquiry saveInquiry(Inquiry inquiry) {
        // Set initial status if not provided
        if (inquiry.getStatus() == null) {
            inquiry.setStatus("PENDING");
        }
        
        return inquiryRepository.save(inquiry);
    }
    
    @Transactional
    public Inquiry updateInquiry(Long id, Inquiry updatedInquiry) {
        return inquiryRepository.findById(id)
                .map(inquiry -> {
                    inquiry.setCar(updatedInquiry.getCar());
                    inquiry.setCustomerName(updatedInquiry.getCustomerName());
                    inquiry.setCustomerEmail(updatedInquiry.getCustomerEmail());
                    inquiry.setCustomerPhone(updatedInquiry.getCustomerPhone());
                    inquiry.setInquiryType(updatedInquiry.getInquiryType());
                    inquiry.setMessage(updatedInquiry.getMessage());
                    inquiry.setStatus(updatedInquiry.getStatus());
                    inquiry.setAssignedTo(updatedInquiry.getAssignedTo());
                    inquiry.setResponse(updatedInquiry.getResponse());
                    inquiry.setResponseDate(updatedInquiry.getResponseDate());
                    
                    return inquiryRepository.save(inquiry);
                })
                .orElse(null);
    }
    
    @Transactional
    public void deleteInquiry(Long id) {
        inquiryRepository.deleteById(id);
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
    
    public List<Inquiry> getInquiriesByCar(Long carId) {
        Optional<Car> car = carRepository.findById(carId);
        return car.map(inquiryRepository::findByCar).orElse(List.of());
    }
    
    public List<Inquiry> getInquiriesByStatus(String status) {
        return inquiryRepository.findByStatus(status);
    }
    
    public List<Inquiry> getInquiriesByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(inquiryRepository::findByAssignedTo).orElse(List.of());
    }
    
    public List<Inquiry> getInquiriesByCustomerEmail(String email) {
        return inquiryRepository.findByCustomerEmailContainingIgnoreCase(email);
    }
    
    public List<Inquiry> getInquiriesByCustomerName(String name) {
        return inquiryRepository.findByCustomerNameContainingIgnoreCase(name);
    }
    
    public List<Inquiry> getInquiriesByType(String inquiryType) {
        return inquiryRepository.findByInquiryType(inquiryType);
    }
    
    public List<Inquiry> getInquiriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inquiryRepository.findByCreatedAtBetween(startDate, endDate);
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
    
    @Transactional
    public Inquiry assignInquiry(Long inquiryId, Long userId) {
        Optional<Inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (inquiryOpt.isPresent() && userOpt.isPresent()) {
            Inquiry inquiry = inquiryOpt.get();
            inquiry.setAssignedTo(userOpt.get());
            inquiry.setStatus("ASSIGNED");
            return inquiryRepository.save(inquiry);
        }
        
        return null;
    }
    
    @Transactional
    public Inquiry respondToInquiry(Long inquiryId, String response) {
        return inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    inquiry.setResponse(response);
                    inquiry.setResponseDate(LocalDateTime.now());
                    inquiry.setStatus("RESPONDED");
                    return inquiryRepository.save(inquiry);
                })
                .orElse(null);
    }
    
    @Transactional
    public Inquiry closeInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    inquiry.setStatus("CLOSED");
                    return inquiryRepository.save(inquiry);
                })
                .orElse(null);
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
    
    public Map<String, Object> getInquiryStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Status counts
        statistics.put("pendingCount", inquiryRepository.countByStatus("PENDING"));
        statistics.put("assignedCount", inquiryRepository.countByStatus("ASSIGNED"));
        statistics.put("respondedCount", inquiryRepository.countByStatus("RESPONDED"));
        statistics.put("closedCount", inquiryRepository.countByStatus("CLOSED"));
        
        // Inquiry types distribution
        Map<String, Long> typeDistribution = inquiryRepository.countByInquiryType().stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
        statistics.put("typeDistribution", typeDistribution);
        
        return statistics;
    }
    
    public List<Inquiry> getPendingInquiriesSortedByCreationDate() {
        return inquiryRepository.findPendingInquiriesSortedByCreationDate();
    }
    
    public List<Inquiry> getActiveInquiriesByCar(Long carId) {
        return inquiryRepository.findActiveInquiriesByCar(carId);
    }
}