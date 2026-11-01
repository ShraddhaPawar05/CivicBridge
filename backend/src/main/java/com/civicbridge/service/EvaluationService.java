
package com.civicbridge.service;

import com.civicbridge.model.Evaluation;
import com.civicbridge.repository.EvaluationRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public List<Map<String, Object>> getEvaluations() {

        List<Map<String, Object>> evaluations = new ArrayList<>();

        for (Evaluation evaluation : evaluationRepository.findAll()) {
            evaluations.add(convertToMap(evaluation));
        }

        return evaluations;
    }

    public Map<String, Object> saveOrUpdateEvaluation(
            Long proposalId,
            Map<String, Object> payload) {

        Evaluation evaluation = evaluationRepository
                .findByProposalId(proposalId)
                .orElseGet(() -> new Evaluation());

        evaluation.setProposalId(proposalId);

        if (payload.containsKey("score")) {
            Object score = payload.get("score");

            if (score instanceof Number) {
                evaluation.setScore(((Number) score).intValue());
            } else if (score != null) {
                evaluation.setScore(Integer.parseInt(score.toString()));
            }
        }

        if (payload.containsKey("recommendation")) {
            evaluation.setRecommendation(
                    (String) payload.get("recommendation"));
        }

        if (payload.containsKey("status")) {
            evaluation.setStatus((String) payload.get("status"));
        }

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        return convertToMap(savedEvaluation);
    }

    private Map<String, Object> convertToMap(Evaluation evaluation) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("proposalId", evaluation.getProposalId());
        result.put("score", evaluation.getScore());
        result.put("recommendation", evaluation.getRecommendation());
        result.put("status", evaluation.getStatus());

        return result;
    }
}
