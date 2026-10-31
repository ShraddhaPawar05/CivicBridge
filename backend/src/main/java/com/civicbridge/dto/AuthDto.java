package com.civicbridge.dto;

public class AuthDto {

    public record RegisterRequest(
        String fullName,
        String email,
        String password,
        String role,
        String department,
        String designation
    ) {}

    public record LoginRequest(
        String email,
        String password
    ) {}

    public record AuthResponse(
        String token,
        String email,
        String role,
        String fullName
    ) {}
}
