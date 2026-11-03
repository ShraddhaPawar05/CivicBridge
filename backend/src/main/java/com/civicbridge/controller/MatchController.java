
package com.civicbridge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.civicbridge.model.Match;
import com.civicbridge.repository.MatchRepository;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // All three roles can view matching results
    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @GetMapping("/matches")
    public List<Map<String, Object>> getMatches(
            @RequestParam(required = false) Long challengeId) {

        List<Match> matches;

        if (challengeId != null) {
            matches = matchRepository.findByChallengeId(challengeId);
        } else {
            matches = matchRepository.findAll();
        }

        return matches.stream()
                .map(match -> Map.<String, Object>of(
                        "id", match.getId(),
                        "challengeId", match.getChallengeId(),
                        "startupId", match.getStartupId(),
                        "startupName", match.getStartupName(),
                        "status", match.getStatus()
                ))
                .toList();
    }
}
