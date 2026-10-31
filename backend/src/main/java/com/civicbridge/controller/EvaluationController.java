package com.civicbridge.controller;

import com.civicbridge.service.EvaluationService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/evaluations")
    public List<Map<String, Object>> getEvaluations() {
        return evaluationService.getEvaluations();
    }

    @PutMapping("/evaluations/{proposalId}")
    public ResponseEntity<Map<String, Object>> saveOrUpdateEvaluation(@PathVariable Long proposalId, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(evaluationService.saveOrUpdateEvaluation(proposalId, payload));
    }
}
