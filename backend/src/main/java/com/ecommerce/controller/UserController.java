package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.UserDTO;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;
//i have added  a stronger authentication with 1 uppercase, 1 lowercase, 1 number, i special character.
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            
            // ✅ Check if user is authenticated
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                System.out.println("❌ User not authenticated");
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            
            String email = auth.getName();
            System.out.println("🔍 Extracted email: '" + email + "'");
            
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                System.out.println("❌ User not found in database for email: " + email);
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            System.out.println("✅ User found: " + user.getName() + ", " + user.getEmail());
            
            // ✅ Return UserDTO
            UserDTO userDTO = new UserDTO(user.getName(), user.getEmail());
            return ResponseEntity.ok(userDTO);
            
        } catch (Exception e) {
            System.out.println("❌ Error in getCurrentUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}
