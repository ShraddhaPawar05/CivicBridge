package com.civicbridge.controller;

import com.civicbridge.model.Proposal;
import com.civicbridge.service.ProposalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @GetMapping("/proposals")
    public List<Proposal> getProposals(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String org
    ) {
        return proposalService.getProposals(role, org);
    }

    @PostMapping("/proposals")
    public ResponseEntity<?> createProposal(@RequestBody Map<String, Object> payload) {
        Proposal proposal = proposalService.createProposal(
            payload.get("challengeId") == null ? null : Long.valueOf(payload.get("challengeId").toString()),
            payload.get("startupId") == null ? null : Long.valueOf(payload.get("startupId").toString()),
            payload.get("startupName") == null ? null : payload.get("startupName").toString(),
            payload.get("title") == null ? null : payload.get("title").toString(),
            payload.get("summary") == null ? null : payload.get("summary").toString(),
            payload.get("status") == null ? null : payload.get("status").toString(),
            payload.get("budget") == null ? 0 : Integer.parseInt(payload.get("budget").toString()),
            payload.get("technologies") instanceof List<?> technologies ? technologies.stream().map(String::valueOf).toList() : List.of()
        );

        return ResponseEntity.ok(proposal);
    }

    @PutMapping("/proposals/{id}")
    public ResponseEntity<?> updateProposal(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Proposal proposal = proposalService.updateProposal(
                id,
                payload.get("title") == null ? null : payload.get("title").toString(),
                payload.get("summary") == null ? null : payload.get("summary").toString(),
                payload.get("status") == null ? null : payload.get("status").toString(),
                payload.get("budget") == null ? -1 : Integer.parseInt(payload.get("budget").toString()),
                payload.get("technologies") instanceof List<?> technologies ? technologies.stream().map(String::valueOf).toList() : null
            );
            return ResponseEntity.ok(proposal);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
