
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
import com.civicbridge.repository.UserAccountRepository;
import com.civicbridge.util.JwtUtil;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserAccountRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public UserAccount register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String fullName = request.fullName() == null ? "" : request.fullName().trim();
        if (fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        String email = request.email() == null ? "" : request.email().trim().toLowerCase(Locale.ROOT);
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String password = request.password() == null ? "" : request.password();
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain both letters and numbers");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("User already exists");
        }

        UserAccount user = new UserAccount();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(normalizeRole(request.role()));
        user.setDepartment(request.department());
        user.setDesignation(request.designation());

        if ("startup".equals(user.getRole())) {
            String startupName = request.startupName() == null ? "" : request.startupName().trim();

            if (startupName.isBlank()) {
                throw new IllegalArgumentException("Startup name is required");
            }

            user.setStartupName(startupName);
        }

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new BadCredentialsException("Request body is required");
        }

        String email = request.email() == null ? "" : request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password() == null ? "" : request.password();

        if (email.isBlank() || password.isBlank()) {
            throw new BadCredentialsException("Email and password are required");
        }

        UserAccount user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("fullName", user.getFullName());
        claims.put("startupName", user.getStartupName());

        String token = jwtUtil.generateToken(user.getEmail(), claims);

        return new AuthResponse(
            token,
            user.getEmail(),
            user.getRole(),
            user.getFullName(),
            user.getStartupName()
        );
    }

    public void logout() {
        // The frontend removes the JWT token during logout.
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

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasLetter && hasDigit;
    }
}
