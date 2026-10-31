package com.civicbridge.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    private final InMemoryDataStore store;

    public EvaluationService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<Map<String, Object>> getEvaluations() {
        return new ArrayList<>(store.getEvaluations());
    }

    public Map<String, Object> saveOrUpdateEvaluation(Long proposalId, Map<String, Object> payload) {
        for (Map<String, Object> item : store.getEvaluations()) {
            if (item.get("proposalId") != null && item.get("proposalId").toString().equals(proposalId.toString())) {
                item.putAll(payload);
                item.put("proposalId", proposalId);
                return item;
            }
        }

        Map<String, Object> evaluation = new java.util.HashMap<>();
        evaluation.put("proposalId", proposalId);
        evaluation.putAll(payload);
        store.getEvaluations().add(evaluation);
        return evaluation;
    }
}
