package com.wheelshift.service;

import com.wheelshift.model.User;
import com.wheelshift.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
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
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional
    public User createUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
   
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        
        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(userDetails.getUsername()) && 
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDetails.getUsername());
        }
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDetails.getEmail());
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        user.setIsActive(userDetails.getIsActive());
        
        // Only update password if it's provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.getIsActive()) {
                return Optional.empty();
            }
            
            // Verify password
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Update last login time
                updateLastLogin(user.getId());
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
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

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public List<User> findInactiveUsersSince(LocalDateTime date) {
        return userRepository.findInactiveUsersSince(date);
    }

    public List<User> findUsersByActiveStatus(Boolean isActive) {
        return userRepository.findByIsActive(isActive);
    }

    public List<User> findUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    public Map<User, Long> getUsersWithInquiryCount() {
        List<Object[]> results = userRepository.findUsersWithInquiryCount();
        Map<User, Long> userInquiryCounts = new HashMap<>();
        
        for (Object[] result : results) {
            User user = (User) result[0];
            Long count = (Long) result[1];
            userInquiryCounts.put(user, count);
        }
        
        return userInquiryCounts;
    }
    
    public List<User> findUsersWithInquiriesInStatus(String status) {
        return userRepository.findUsersWithInquiriesInStatus(status);
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    public long countActiveUsers() {
        return userRepository.findByIsActive(true).size();
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
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<Map<String, Object>> getTopUsersByInquiryCount(int limit) {
        List<Object[]> results = userRepository.findTopUsersByInquiryCount(PageRequest.of(0, limit));
        
        return results.stream().map(row -> {
            Map<String, Object> userStats = new HashMap<>();
            User user = (User) row[0];
            Long count = (Long) row[1];
            
            userStats.put("userId", user.getId());
            userStats.put("username", user.getUsername());
            userStats.put("fullName", user.getFullName());
            userStats.put("inquiryCount", count);
            
            return userStats;
        }).toList();
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
    
    public Map<String, Object> getUserActivityStatistics() {
        List<User> allUsers = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        
        long activeInLastWeek = allUsers.stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(oneWeekAgo))
                .count();
                
        long activeInLastMonth = allUsers.stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(oneMonthAgo))
                .count();
                
        long neverLoggedIn = allUsers.stream()
                .filter(u -> u.getLastLogin() == null)
                .count();
                
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", userRepository.findByIsActive(true).size());
        stats.put("inactiveUsers", userRepository.findByIsActive(false).size());
        stats.put("activeInLastWeek", activeInLastWeek);
        stats.put("activeInLastMonth", activeInLastMonth);
        stats.put("neverLoggedIn", neverLoggedIn);
        
        return stats;
    }
}