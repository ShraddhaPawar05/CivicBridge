package com.civicbridge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MatchController {

    @GetMapping("/matches")
    public List<Map<String, Object>> getMatches(@RequestParam(required = false) Long challengeId) {
        return List.of(
            Map.of("id", 1L, "challengeId", challengeId != null ? challengeId : 101L, "startupId", 2L, "startupName", "EcoTech Solutions", "status", "Matched"),
            Map.of("id", 2L, "challengeId", challengeId != null ? challengeId : 101L, "startupId", 3L, "startupName", "UrbanAI Labs", "status", "Shortlisted")
        );
    }
}
