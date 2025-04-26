package com.wheelshift.service;

import com.wheelshift.model.Car;
import com.wheelshift.model.Client;
import com.wheelshift.model.Reservation;
import com.wheelshift.projection.ReservationProjection;
import com.wheelshift.repository.CarRepository;
import com.wheelshift.repository.ClientRepository;
import com.wheelshift.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    
    /**
     *     _____ _____  _    _ _____  
     *    / ____|  __ \| |  | |  __ \ 
     *   | |    | |__) | |  | | |  | |
     *   | |    |  _  /| |  | | |  | |
     *   | |____| | \ \| |__| | |__| |
     *    \_____|_|  \_\\____/|_____/ 
     *                                                   
     *              CRUD OPERATIONS
     */

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    public List<Reservation> getAllReservationsWithDetails() {
        return reservationRepository.findAllWithCarAndClient();
    }
    
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }
    
    public Optional<Reservation> getReservationWithDetails(Long id) {
        return reservationRepository.findByIdWithCarAndClient(id);
    }
    
    // Methods for projection-based fetching
    
    public Optional<ReservationProjection> getReservationProjectionById(Long id) {
        return reservationRepository.findProjectionByIdWithDetails(id);
    }
    
    public List<ReservationProjection> getAllReservationProjections() {
        return reservationRepository.findAllProjectionsWithDetails();
    }
    
    public List<ReservationProjection> getReservationProjectionsByCarId(Long carId) {
        return reservationRepository.findProjectionsByCarId(carId);
    }
    
    public List<ReservationProjection> getReservationProjectionsByClientId(Long clientId) {
        return reservationRepository.findProjectionsByClientId(clientId);
    }
    
    public List<ReservationProjection> getReservationProjectionsByStatus(String status) {
        return reservationRepository.findProjectionsByStatus(status);
    }
    
    @Transactional
    public Reservation save(Reservation reservation) {
        // Handle car and client references properly
        if (reservation.getCar() != null && reservation.getCar().getId() != null) {
            Car car = carRepository.findById(reservation.getCar().getId())
                    .orElseThrow(() -> new RuntimeException("Car not found"));
            reservation.setCar(car);
            
            boolean available = isCarAvailable(car.getId());
            if(!available) {
            	throw new RuntimeException("Car is already reserved");
            }
            
            // Update car status
            car.setCurrentStatus("Reserved");
            car.setReservation(reservation);
            carRepository.save(car);
        }
        
        if (reservation.getClient() != null && reservation.getClient().getId() != null) {
            Client client = clientRepository.findById(reservation.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            reservation.setClient(client);
        }
        
        return reservationRepository.save(reservation);
    }
    
    @Transactional
    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setCar(updatedReservation.getCar());
                    reservation.setClient(updatedReservation.getClient());
                    reservation.setReservationDate(updatedReservation.getReservationDate());
                    reservation.setExpiryDate(updatedReservation.getExpiryDate());
                    reservation.setStatus(updatedReservation.getStatus());
                    reservation.setDepositAmount(updatedReservation.getDepositAmount());
                    reservation.setDepositPaid(updatedReservation.getDepositPaid());
                    reservation.setNotes(updatedReservation.getNotes());
                    
                    return reservationRepository.save(reservation);
                })
                .orElse(null);
    }
    
    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            // Update car status back to available if the reservation was active
            if ("ACTIVE".equals(reservation.getStatus())) {
                Car car = reservation.getCar();
                car.setCurrentStatus("AVAILABLE");
                car.setReservation(null);
                carRepository.save(car);
            }
            
            reservationRepository.deleteById(id);
        });
    }
    
    /**
     *     _____ ______          _____   _____ _    _ 
     *    / ____|  ____|   /\   |  __ \ / ____| |  | |
     *   | (___ | |__     /  \  | |__) | |    | |__| |
     *    \___ \|  __|   / /\ \ |  _  /| |    |  __  |
     *    ____) | |____ / ____ \| | \ \| |____| |  | |
     *   |_____/|______/_/    \_\_|  \_\\_____|_|  |_|
     *                                              
     *              SEARCH & FILTERS OPERATIONS
     */
    
    public List<Reservation> getReservationsByCar(Long carId) {
        Optional<Car> car = carRepository.findById(carId);
        return car.map(reservationRepository::findByCar).orElse(List.of());
    }
    
    public List<Reservation> getReservationsByStatus(String status) {
        return reservationRepository.findByStatus(status);
    }
    
    public List<Reservation> getReservationsByDepositStatus(Boolean depositPaid) {
        return reservationRepository.findByDepositPaid(depositPaid);
    }
    
    public List<Reservation> getReservationsByClient(Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        return client.map(reservationRepository::findByClient).orElse(List.of());
    }
    
    public List<Reservation> getReservationsByClientEmail(String email) {
        return reservationRepository.findByClientEmailContainingIgnoreCase(email);
    }
    
    public List<Reservation> getReservationsByClientName(String name) {
        return reservationRepository.findByClientNameContainingIgnoreCase(name);
    }
    
    public List<Reservation> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findByReservationDateBetween(startDate, endDate);
    } 
    
    /**
     *    ____  _    _  _____ _____ _   _ ______  _____ _____   _      ____   _____ _____ _____ 
     *   |  _ \| |  | |/ ____|_   _| \ | |  ____|/ ____/ ____| | |    / __ \ / ____|_   _/ ____|
     *   | |_) | |  | | (___   | | |  \| | |__  | (___| (___   | |   | |  | | |  __  | || |     
     *   |  _ <| |  | |\___ \  | | | . ` |  __|  \___ \\___ \  | |   | |  | | | |_ | | || |     
     *   | |_) | |__| |____) |_| |_| |\  | |____ ____) |___) | | |___| |__| | |__| |_| || |____ 
     *   |____/ \____/|_____/|_____|_| \_|______|_____/_____/  |______\____/ \_____|_____\_____|
     *                                                                                   
     *              BUSINESS LOGIC & TRANSACTIONS                                                                                   
     */
    
    @Transactional
    public Reservation markDepositPaid(Long reservationId, BigDecimal amount) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservation.setDepositAmount(amount);
                    reservation.setDepositPaid(true);
                    return reservationRepository.save(reservation);
                })
                .orElse(null);
    }
    
    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus("CANCELLED");
            
            // Update car status
            Car car = reservation.getCar();
            car.setCurrentStatus("AVAILABLE");
            carRepository.save(car);
            
            return reservationRepository.save(reservation);
        }
        
        return null;
    }
    
    @Transactional
    public Reservation completeReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservation.setStatus("COMPLETED");
                    return reservationRepository.save(reservation);
                })
                .orElse(null);
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    @Transactional
    public void updateExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndExpiryDateBefore("ACTIVE", now);
        
        for (Reservation reservation : expiredReservations) {
            reservation.setStatus("EXPIRED");
            
            // Update car status back to available
            Car car = reservation.getCar();
            car.setCurrentStatus("AVAILABLE");
            carRepository.save(car);
            
            reservationRepository.save(reservation);
        }
    }
    
    /**
     *      ____ _______    _______ _____ 
     *    / ____|__   __|/\|__   __/ ____|
     *   | (___    | |  /  \  | | | (___  
     *    \___ \   | | / /\ \ | |  \___ \ 
     *    ____) |  | |/ ____ \| |  ____) |
     *   |_____/   |_/_/    \_\_| |_____/ 
     *
     *              STATISTICS AND ANALYTICS
     */
    
    public Map<String, Object> getReservationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Status counts
        statistics.put("activeCount", reservationRepository.countByStatus("ACTIVE"));
        statistics.put("completedCount", reservationRepository.countByStatus("COMPLETED"));
        statistics.put("cancelledCount", reservationRepository.countByStatus("CANCELLED"));
        statistics.put("expiredCount", reservationRepository.countByStatus("EXPIRED"));
        
        // Monthly distribution for current year
        int currentYear = LocalDateTime.now().getYear();
        Map<Integer, Long> monthlyDistribution = reservationRepository.countReservationsByMonth(currentYear).stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],
                        arr -> (Long) arr[1]
                ));
        statistics.put("monthlyDistribution", monthlyDistribution);
        
        // Deposit statistics
        List<Reservation> activeReservations = reservationRepository.findByStatus("ACTIVE");
        BigDecimal totalDeposits = activeReservations.stream()
                .filter(r -> r.getDepositPaid() && r.getDepositAmount() != null)
                .map(Reservation::getDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statistics.put("totalDeposits", totalDeposits);
        statistics.put("depositPaidCount", activeReservations.stream().filter(Reservation::getDepositPaid).count());
        statistics.put("depositPendingCount", activeReservations.stream().filter(r -> !r.getDepositPaid()).count());
        
        return statistics;
    }
    
    public List<Reservation> getActiveReservationsByCar(Long carId) {
        return reservationRepository.findByCarIdAndStatus(carId, "ACTIVE");
    }

    public List<Reservation> getExpiringReservations(int daysToExpiry) {
        LocalDateTime cutoffDate = LocalDateTime.now().plusDays(daysToExpiry);
        return reservationRepository.findByStatusAndExpiryDateBefore("ACTIVE", cutoffDate);
    }
    
    /**
     *    _    _ ______ _      _____  ______ _____  
     *   | |  | |  ____| |    |  __ \|  ____|  __ \ 
     *   | |__| | |__  | |    | |__) | |__  | |__) |
     *   |  __  |  __| | |    |  ___/|  __| |  _  / 
     *   | |  | | |____| |____| |    | |____| | \ \ 
     *   |_|  |_|______|______|_|    |______|_|  \_\
     *              
     *              HELPER FUNCTIONS                         
     */
    
    private boolean isCarAvailable(Long carId) {
        List<Reservation> activeReservations = reservationRepository.findByCarIdAndStatus(carId, "ACTIVE");
        
        // Check if car is already reserved
        if (!activeReservations.isEmpty()) {
            return false;
        }
        
        // Check if car status allows reservation
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            String status = carOpt.get().getCurrentStatus();
            return !"SOLD".equals(status) && !"RESERVED".equals(status);
        }
        
        return false;
    }
}