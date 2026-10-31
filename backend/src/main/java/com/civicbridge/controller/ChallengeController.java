package com.civicbridge.controller;

import com.civicbridge.dto.ChallengeDto.CreateChallengeRequest;
import com.civicbridge.dto.ChallengeDto.UpdateChallengeRequest;
import com.civicbridge.model.Challenge;
import com.civicbridge.service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/challenges")
    public List<Challenge> getChallenges(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String search
    ) {
        return challengeService.getChallenges(status, category, search);
    }

    @PostMapping("/challenges")
    public ResponseEntity<?> createChallenge(@RequestBody CreateChallengeRequest request) {
        Challenge challenge = challengeService.createChallenge(
            request.title(),
            request.dept(),
            request.category(),
            request.deadline(),
            request.description(),
            request.status(),
            request.tags()
        );

        return ResponseEntity.ok(challenge);
    }

    @PutMapping("/challenges/{id}")
    public ResponseEntity<?> updateChallenge(@PathVariable Long id, @RequestBody UpdateChallengeRequest request) {
        try {
            Challenge challenge = challengeService.updateChallenge(
                id,
                request.title(),
                request.dept(),
                request.category(),
                request.deadline(),
                request.description(),
                request.status(),
                request.tags()
            );
            return ResponseEntity.ok(challenge);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/challenges/{id}")
    public ResponseEntity<Map<String, Object>> deleteChallenge(@PathVariable Long id) {
        boolean deleted = challengeService.deleteChallenge(id);
        return ResponseEntity.ok(Map.of("deleted", deleted, "id", id));
    }
}
