package com.civicbridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.civicbridge.dto.AuthDto.LoginRequest;
import com.civicbridge.dto.AuthDto.RegisterRequest;
import com.civicbridge.model.UserAccount;
import com.civicbridge.service.AuthService;
import com.civicbridge.service.InMemoryDataStore;
import com.civicbridge.util.JwtUtil;

class AuthServiceTest {

    @Test
    void registerAndLoginShouldWorkEndToEnd() {
        InMemoryDataStore store = new InMemoryDataStore();
        AuthService authService = new AuthService(store, new JwtUtil());

        UserAccount user = authService.register(new RegisterRequest(
            "Alice Example",
            "alice@example.com",
            "StrongPass123",
            "startup",
            "AI",
            "Founder",
            "UrbanAI Labs"
        ));

        assertNotNull(user.getId());
        assertEquals("startup", user.getRole());
        assertEquals("UrbanAI Labs", user.getStartupName());

        var response = authService.login(new LoginRequest("alice@example.com", "StrongPass123"));

        assertEquals("alice@example.com", response.email());
        assertEquals("startup", response.role());
        assertEquals("UrbanAI Labs", response.startupName());
        assertNotNull(response.token());
    }

    @Test
    void duplicateRegistrationShouldBeRejected() {
        InMemoryDataStore store = new InMemoryDataStore();
        AuthService authService = new AuthService(store, new JwtUtil());

        authService.register(new RegisterRequest(
            "Duplicate User",
            "duplicate@example.com",
            "StrongPass123",
            "gov",
            "Urban Department",
            "Director",
            null
        ));

        assertThrows(IllegalArgumentException.class, () -> authService.register(new RegisterRequest(
            "Duplicate User",
            "duplicate@example.com",
            "AnotherPass123",
            "gov",
            "Urban Department",
            "Director",
            null
        )));
    }

    @Test
    void startupRegistrationRequiresStartupName() {
        InMemoryDataStore store = new InMemoryDataStore();
        AuthService authService = new AuthService(store, new JwtUtil());

        assertThrows(IllegalArgumentException.class, () -> authService.register(new RegisterRequest(
            "Startup Founder",
            "founder@example.com",
            "StrongPass123",
            "startup",
            "AI",
            "Founder",
            "   "
        )));
    }

    @Test
    void weakPasswordsAreRejected() {
        InMemoryDataStore store = new InMemoryDataStore();
        AuthService authService = new AuthService(store, new JwtUtil());

        assertThrows(IllegalArgumentException.class, () -> authService.register(new RegisterRequest(
            "Weak User",
            "weak@example.com",
            "short",
            "startup",
            "AI",
            "Founder",
            "Test Startup"
        )));
    }
}
