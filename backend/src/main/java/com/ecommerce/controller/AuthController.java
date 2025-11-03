package com.ecommerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ✅ USER SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String name = request.get("name"); // full name only

            // Validation
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }
            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
            }
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Full name is required"));
            }
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("message", "User already exists. Please sign in."));
            }
            if (!isStrongPassword(password)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 8 characters long and contain 1 number and 1 special character."));
            }

            // ✅ Create user
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            user.setRole("USER");

            // ✅ Register user
            User savedUser = userService.registerUser(user);

            // ✅ JWT Token for instant login
            String token = jwtUtil.generateToken(savedUser.getEmail());

            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully!",
                "token", token,
                "user", Map.of(
                    "id", savedUser.getId(),
                    "email", savedUser.getEmail(),
                    "name", savedUser.getName(),
                    "role", savedUser.getRole()
                )
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    // ✅ USER SIGNIN
    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
            }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtil.generateToken(email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                "message", "Login successful!",
                "token", token,
                "user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()
                )
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }

    // ✅ TOKEN VALIDATION
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.validate(token)) {
                    String email = jwtUtil.extractEmail(token);
                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        return ResponseEntity.ok(Map.of(
                            "valid", true,
                            "user", Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "name", user.getName(),
                                "role", user.getRole()
                            )
                        ));
                    }
                }
            }
            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }

    // ✅ Password strength helper
    private boolean isStrongPassword(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$");
    }
}
