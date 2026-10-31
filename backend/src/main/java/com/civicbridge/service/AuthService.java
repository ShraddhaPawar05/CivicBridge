package com.civicbridge.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.civicbridge.dto.AuthDto.AuthResponse;
import com.civicbridge.dto.AuthDto.LoginRequest;
import com.civicbridge.dto.AuthDto.RegisterRequest;
import com.civicbridge.model.UserAccount;
import com.civicbridge.util.JwtUtil;

@Service
public class AuthService {

    private final InMemoryDataStore store;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(InMemoryDataStore store, JwtUtil jwtUtil) {
        this.store = store;
        this.jwtUtil = jwtUtil;
    }

    public UserAccount register(RegisterRequest request) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase(Locale.ROOT);
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (store.findUserByEmail(email) != null) {
            throw new IllegalArgumentException("User already exists");
        }

        UserAccount user = new UserAccount();
        user.setId(store.nextUserId());
        user.setFullName(request.fullName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(normalizeRole(request.role()));
        user.setDepartment(request.department());
        user.setDesignation(request.designation());

        store.getUsers().add(user);
        return user;
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase(Locale.ROOT);
        UserAccount user = store.findUserByEmail(email);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("fullName", user.getFullName());

        String token = jwtUtil.generateToken(user.getEmail(), claims);
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getFullName());
    }

    public void logout() {
        // In this non-DB implementation, logout is a no-op and can be handled by frontend token removal.
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "startup";
        }

        String normalized = role.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("gov") || normalized.equals("government")) {
            return "gov";
        }
        if (normalized.equals("evaluator") || normalized.equals("reviewer")) {
            return "evaluator";
        }
        return "startup";
    }
}
