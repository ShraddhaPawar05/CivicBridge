
package com.civicbridge.service;

import com.civicbridge.model.Evaluation;
import com.civicbridge.repository.EvaluationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ObjectMapper objectMapper;

    public EvaluationService(
            EvaluationRepository evaluationRepository,
            ObjectMapper objectMapper) {
        this.evaluationRepository = evaluationRepository;
        this.objectMapper = objectMapper;
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
                .orElseGet(Evaluation::new);

        evaluation.setProposalId(proposalId);

        if (payload.containsKey("score")) {
            Object score = payload.get("score");

            if (score instanceof Number) {
                evaluation.setScore(((Number) score).intValue());
            } else if (score != null) {
                evaluation.setScore(Integer.parseInt(score.toString()));
            }
        }

        if (payload.containsKey("scores")) {
            try {
                String scoresJson = objectMapper.writeValueAsString(
                        payload.get("scores")
                );
                evaluation.setScoresJson(scoresJson);
            } catch (Exception e) {
                throw new RuntimeException("Unable to save evaluation scores", e);
            }
        }

        if (payload.containsKey("comments")) {
            Object comments = payload.get("comments");
            evaluation.setComments(
                    comments == null ? null : comments.toString()
            );
        }

        if (payload.containsKey("recommendation")) {
            evaluation.setRecommendation(
                    (String) payload.get("recommendation")
            );
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

        Map<String, Object> scores = new LinkedHashMap<>();

        if (evaluation.getScoresJson() != null
                && !evaluation.getScoresJson().isBlank()) {
            try {
                scores = objectMapper.readValue(
                        evaluation.getScoresJson(),
                        new TypeReference<Map<String, Object>>() {}
                );
            } catch (Exception e) {
                throw new RuntimeException("Unable to read evaluation scores", e);
            }
        }

        result.put("scores", scores);
        result.put("comments", evaluation.getComments());
        result.put("recommendation", evaluation.getRecommendation());
        result.put("status", evaluation.getStatus());

        return result;
    }
}
