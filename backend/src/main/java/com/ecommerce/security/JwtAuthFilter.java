package com.ecommerce.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        
        // ✅ Skip JWT validation for public endpoints
        if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Extract token from Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtUtil.validate(token)) {
                    String email = jwtUtil.extractEmail(token);
                    User user = userRepository.findByEmail(email).orElse(null);

                    if (user != null) {
                        // ✅ FIX: Use the role from user entity
                        String userRole = user.getRole() != null ? user.getRole() : "USER";
                        List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + userRole.toUpperCase())
                        );
                        
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("✅ Authenticated user: " + user.getEmail() + " with role: " + userRole);
                    } else {
                        System.out.println("❌ User not found in database");
                        sendErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                } else {
                    System.out.println("❌ Invalid JWT Token");
                    sendErrorResponse(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (Exception e) {
                System.out.println("❌ JWT processing error: " + e.getMessage());
                sendErrorResponse(response, "Token processing error", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // ✅ FIX: Return error for protected endpoints without token
            if (!path.startsWith("/api/auth/") && !path.startsWith("/api/public/")) {
                System.out.println("⚠️ No JWT token found for protected endpoint: " + path);
                sendErrorResponse(response, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\", \"status\": " + status + "}");
    }
}