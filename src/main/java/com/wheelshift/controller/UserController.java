package com.wheelshift.controller;

import com.wheelshift.model.User;
import com.wheelshift.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        
        Optional<User> userOpt = userService.login(username, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // For security, don't return the password
            user.setPassword(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials or account is inactive");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> {
            u.setPassword(null); // Don't expose password
            return ResponseEntity.ok(u);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Don't expose passwords
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            createdUser.setPassword(null); // Don't expose password
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            updatedUser.setPassword(null); // Don't expose password
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
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
    
    @GetMapping("/search/username")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(u -> {
            u.setPassword(null); // Don't expose password
            return ResponseEntity.ok(u);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(u -> {
            u.setPassword(null); // Don't expose password
            return ResponseEntity.ok(u);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<User>> getUsersByName(@RequestParam String name) {
        List<User> users = userService.findUsersByName(name);
        users.forEach(user -> user.setPassword(null)); // Don't expose passwords
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/active")
    public ResponseEntity<List<User>> getUsersByActiveStatus(@RequestParam Boolean isActive) {
        List<User> users = userService.findUsersByActiveStatus(isActive);
        users.forEach(user -> user.setPassword(null)); // Don't expose passwords
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/inactive-since")
    public ResponseEntity<List<User>> getInactiveUsersSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<User> users = userService.findInactiveUsersSince(date);
        users.forEach(user -> user.setPassword(null)); // Don't expose passwords
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/inquiries/status")
    public ResponseEntity<List<User>> getUsersWithInquiriesInStatus(@RequestParam String status) {
        List<User> users = userService.findUsersWithInquiriesInStatus(status);
        users.forEach(user -> user.setPassword(null)); // Don't expose passwords
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check/username")
    public ResponseEntity<Map<String, Boolean>> isUsernameAvailable(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        Map<String, Boolean> response = Map.of("available", available);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/email")
    public ResponseEntity<Map<String, Boolean>> isEmailAvailable(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        Map<String, Boolean> response = Map.of("available", available);
        return ResponseEntity.ok(response);
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
     
    @GetMapping("/statistics/activity")
    public ResponseEntity<Map<String, Object>> getUserActivityStatistics() {
        Map<String, Object> stats = userService.getUserActivityStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/top-inquiries")
    public ResponseEntity<List<Map<String, Object>>> getTopUsersByInquiryCount(
            @RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> topUsers = userService.getTopUsersByInquiryCount(limit);
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Map<String, Long>> countActiveUsers() {
        long count = userService.countActiveUsers();
        Map<String, Long> response = Map.of("activeUsers", count);
        return ResponseEntity.ok(response);
    }
}