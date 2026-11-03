
package com.civicbridge.controller;

import com.civicbridge.service.EvaluationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // Only Evaluators and Government users can view evaluations
    @PreAuthorize("hasAnyRole('EVALUATOR', 'GOV')")
    @GetMapping("/evaluations")
    public List<Map<String, Object>> getEvaluations() {
        return evaluationService.getEvaluations();
    }

    // Only Evaluators can create or update evaluations
    @PreAuthorize("hasRole('EVALUATOR')")
    @PutMapping("/evaluations/{proposalId}")
    public ResponseEntity<Map<String, Object>> saveOrUpdateEvaluation(
            @PathVariable Long proposalId,
            @RequestBody Map<String, Object> payload) {

        return ResponseEntity.ok(
            evaluationService.saveOrUpdateEvaluation(proposalId, payload)
        );
    }
}
