
package com.civicbridge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.civicbridge.model.Match;
import com.civicbridge.model.UserAccount;
import com.civicbridge.repository.MatchRepository;
import com.civicbridge.repository.UserAccountRepository;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchRepository matchRepository;
    private final UserAccountRepository userAccountRepository;

    public MatchController(
            MatchRepository matchRepository,
            UserAccountRepository userAccountRepository) {
        this.matchRepository = matchRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @GetMapping("/matches")
    public List<Map<String, Object>> getMatches(
            @RequestParam(required = false) Long challengeId,
            Authentication authentication) {

        List<Match> matches;

        boolean isStartup = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STARTUP"));

        if (isStartup) {

            UserAccount user = userAccountRepository
                    .findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Startup account not found"));

            if (challengeId != null) {
                matches = matchRepository.findByStartupIdAndChallengeId(
                        user.getId(), challengeId);
            } else {
                matches = matchRepository.findByStartupId(user.getId());
            }

        } else {

            if (challengeId != null) {
                matches = matchRepository.findByChallengeId(challengeId);
            } else {
                matches = matchRepository.findAll();
            }
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
