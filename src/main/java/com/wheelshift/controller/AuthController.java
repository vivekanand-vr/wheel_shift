package com.wheelshift.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	
	 @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
	        
	        // Hardcoded credentials for validation
	        String hardcodedEmail = "admin@ws.com";
	        String hardcodedPassword = "password";

	        if (loginRequest.getEmail().equals(hardcodedEmail) &&
	            loginRequest.getPassword().equals(hardcodedPassword)) {
	            
	            // Hardcoded user details
	            UserResponse user = new UserResponse("Admin Vivek", 
	                                                 "https://vivekanand-vr.netlify.app/V_Image.jpg", 
	                                                 "vivek@admin.com", 
	                                                 "8073906734", 
	                                                 "Dharwad, Karnataka");
	            return new ResponseEntity<>(user, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
	        }
	    }

	    // Request DTO
	    static class LoginRequest {
	        private String email;
	        private String password;

	        public String getEmail() { return email; }
	        public void setEmail(String email) { this.email = email; }

	        public String getPassword() { return password; }
	        public void setPassword(String password) { this.password = password; }
	    }

	    // Response DTO
	    static class UserResponse {
	        private String name;
	        private String photoUrl;
	        private String email;
	        private String phone;
	        private String location;

	        public UserResponse(String name, String photoUrl, String email, String phone, String location) {
	            this.name = name;
	            this.photoUrl = photoUrl;
	            this.email = email;
	            this.phone = phone;
	            this.location = location;
	        }

	        public String getName() { return name; }
	        public String getPhotoUrl() { return photoUrl; }
	        public String getEmail() { return email; }
	        public String getPhone() { return phone; }
	        public String getLocation() { return location; }
	    }
}
