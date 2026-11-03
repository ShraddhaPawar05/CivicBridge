
package com.civicbridge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.civicbridge.dto.AuthDto.LoginRequest;
import com.civicbridge.dto.AuthDto.RegisterRequest;
import com.civicbridge.model.UserAccount;
import com.civicbridge.repository.UserAccountRepository;
import com.civicbridge.service.AuthService;
import com.civicbridge.util.JwtUtil;

class AuthServiceTest {

    private JwtUtil createTestJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(
            jwtUtil,
            "secret",
            "civicbridge-test-secret-key-1234567890"
        );

        return jwtUtil;
    }

    @Test
    void registerAndLoginShouldWorkEndToEnd() {
        UserAccountRepository repository = mock(UserAccountRepository.class);
        AtomicReference<UserAccount> savedUser = new AtomicReference<>();

        when(repository.existsByEmailIgnoreCase("alice@example.com"))
            .thenReturn(false);

        when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount user = invocation.getArgument(0);
            user.setId(1L);
            savedUser.set(user);
            return user;
        });

        when(repository.findByEmailIgnoreCase("alice@example.com"))
            .thenAnswer(invocation -> Optional.ofNullable(savedUser.get()));

        AuthService authService = new AuthService(repository, createTestJwtUtil());

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

        var response = authService.login(
            new LoginRequest("alice@example.com", "StrongPass123")
        );

        assertEquals("alice@example.com", response.email());
        assertEquals("startup", response.role());
        assertEquals("UrbanAI Labs", response.startupName());
        assertNotNull(response.token());
    }

    @Test
    void duplicateRegistrationShouldBeRejected() {
        UserAccountRepository repository = mock(UserAccountRepository.class);

        when(repository.existsByEmailIgnoreCase("duplicate@example.com"))
            .thenReturn(true);

        AuthService authService = new AuthService(repository, createTestJwtUtil());

        assertThrows(IllegalArgumentException.class, () ->
            authService.register(new RegisterRequest(
                "Duplicate User",
                "duplicate@example.com",
                "StrongPass123",
                "gov",
                "Urban Department",
                "Director",
                null
            ))
        );
    }

    @Test
    void startupRegistrationRequiresStartupName() {
        UserAccountRepository repository = mock(UserAccountRepository.class);

        when(repository.existsByEmailIgnoreCase("founder@example.com"))
            .thenReturn(false);

        AuthService authService = new AuthService(repository, createTestJwtUtil());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(new RegisterRequest(
                "Startup Founder",
                "founder@example.com",
                "StrongPass123",
                "startup",
                "AI",
                "Founder",
                "   "
            ))
        );

        assertEquals("Startup name is required", exception.getMessage());
    }

    @Test
    void weakPasswordsAreRejected() {
        UserAccountRepository repository = mock(UserAccountRepository.class);

        AuthService authService = new AuthService(repository, createTestJwtUtil());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(new RegisterRequest(
                "Weak User",
                "weak@example.com",
                "short",
                "startup",
                "AI",
                "Founder",
                "Test Startup"
            ))
        );

        assertEquals(
            "Password must be at least 8 characters long and contain both letters and numbers",
            exception.getMessage()
        );
    }
}
