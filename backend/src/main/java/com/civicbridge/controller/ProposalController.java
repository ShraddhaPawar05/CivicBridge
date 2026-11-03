
package com.civicbridge.controller;

import com.civicbridge.model.Proposal;
import com.civicbridge.service.ProposalService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @GetMapping("/proposals")
    public List<Proposal> getProposals(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String org,
            Authentication authentication) {

        return proposalService.getProposals(
                role,
                org,
                authentication.getName(),
                authentication.getAuthorities().iterator().next()
                        .getAuthority().replace("ROLE_", "")
        );
    }

    @PreAuthorize("hasRole('STARTUP')")
    @PostMapping("/proposals")
    public ResponseEntity<?> createProposal(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        Proposal proposal = proposalService.createProposal(
                payload.get("challengeId") == null ? null :
                        Long.valueOf(payload.get("challengeId").toString()),

                payload.get("title") == null ? null :
                        payload.get("title").toString(),

                payload.get("summary") == null ? null :
                        payload.get("summary").toString(),

                payload.get("budget") == null ? 0 :
                        Integer.parseInt(payload.get("budget").toString()),

                payload.get("technologies") instanceof List<?> technologies
                        ? new ArrayList<>(
                                technologies.stream()
                                        .map(String::valueOf)
                                        .toList())
                        : new ArrayList<>(),

                authentication.getName()
        );

        return ResponseEntity.ok(proposal);
    }

    @PreAuthorize("hasRole('STARTUP')")
    @PutMapping("/proposals/{id}")
    public ResponseEntity<?> updateOwnProposal(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        Proposal proposal = proposalService.updateOwnProposal(
                id,

                payload.get("title") == null ? null :
                        payload.get("title").toString(),

                payload.get("summary") == null ? null :
                        payload.get("summary").toString(),

                payload.get("budget") == null ? -1 :
                        Integer.parseInt(payload.get("budget").toString()),

                payload.get("technologies") instanceof List<?> technologies
                        ? new ArrayList<>(
                                technologies.stream()
                                        .map(String::valueOf)
                                        .toList())
                        : null,

                authentication.getName()
        );

        return ResponseEntity.ok(proposal);
    }

    @PreAuthorize("hasRole('EVALUATOR')")
    @PutMapping("/proposals/{id}/status")
    public ResponseEntity<?> updateProposalStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        Proposal proposal = proposalService.updateProposalStatus(
                id,
                payload.get("status")
        );

        return ResponseEntity.ok(proposal);
    }
}
