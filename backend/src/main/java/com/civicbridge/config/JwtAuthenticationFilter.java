
package com.civicbridge.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.civicbridge.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                // Parse and verify the JWT only once
                Jws<Claims> parsedToken = Jwts.parser()
                        .verifyWith(jwtUtil.getSigningKeyForVerification())
                        .build()
                        .parseSignedClaims(token);

                Claims claims = parsedToken.getPayload();

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if (email != null && !email.isBlank()
                        && role != null && !role.isBlank()
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    String authority = "ROLE_" + role.toUpperCase();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    List.of(new SimpleGrantedAuthority(authority))
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }

            } catch (Exception ex) {

                System.err.println(
                        "JWT authentication error: "
                        + ex.getClass().getSimpleName()
                        + " - " + ex.getMessage()
                );

                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
