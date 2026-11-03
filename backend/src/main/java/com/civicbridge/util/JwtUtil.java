
package com.civicbridge.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 8;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Provides the signing key for JWT verification
    public SecretKey getSigningKeyForVerification() {
        return getSigningKey();
    }

    // Generate JWT token
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey())
            .compact();
    }

    // Extract email from JWT token
    public String extractSubject(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
