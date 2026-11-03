
package com.civicbridge.controller;

import com.civicbridge.service.PilotService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PilotController {

    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    // All three roles can view pilots
    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @GetMapping("/pilots")
    public List<Map<String, Object>> getPilots(
            Authentication authentication) {

        boolean canViewAll = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_GOV")
                        || role.equals("ROLE_EVALUATOR"));

        return pilotService.getPilots(
                authentication.getName(), canViewAll);
    }

    // Government and Evaluators can update pilot details
    @PreAuthorize("hasAnyRole('GOV', 'EVALUATOR')")
    @PutMapping("/pilots/{id}")
    public ResponseEntity<Map<String, Object>> updatePilot(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        return ResponseEntity.ok(
            pilotService.updatePilot(id, payload)
        );
    }

    // Startups can update their own pilots.
    // Evaluators can update all pilots.
    @PreAuthorize("hasAnyRole('STARTUP', 'EVALUATOR')")
    @PutMapping("/pilots/{id}/milestones")
    public ResponseEntity<Map<String, Object>> updateMilestones(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        boolean canUpdateAll = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_EVALUATOR"));

        return ResponseEntity.ok(
            pilotService.updateMilestones(
                id,
                payload.get("milestones"),
                payload.get("notes") == null
                    ? ""
                    : payload.get("notes").toString(),
                payload.get("status") == null
                    ? null
                    : payload.get("status").toString(),
                payload.get("progress") == null
                    ? null
                    : Integer.valueOf(
                        payload.get("progress").toString()
                    ),
                authentication.getName(),
                canUpdateAll
            )
        );
    }
}
    