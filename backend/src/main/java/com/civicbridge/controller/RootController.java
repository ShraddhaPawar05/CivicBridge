package com.civicbridge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping({"/", ""})
    public Map<String, Object> root() {
        return Map.of(
            "service", "CivicBridge API",
            "status", "UP",
            "health", "/api/health",
            "endpoints", List.of(
                "/api/auth/login",
                "/api/auth/register",
                "/api/challenges"
            )
        );
    }
}
