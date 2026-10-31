package com.civicbridge.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PilotService {

    private final InMemoryDataStore store;

    public PilotService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<Map<String, Object>> getPilots() {
        return new ArrayList<>(store.getPilots());
    }

    public Map<String, Object> updatePilot(Long id, Map<String, Object> payload) {
        for (Map<String, Object> pilot : store.getPilots()) {
            if (pilot.get("id") != null && pilot.get("id").toString().equals(id.toString())) {
                pilot.putAll(payload);
                pilot.put("id", id);
                return pilot;
            }
        }

        Map<String, Object> pilot = new java.util.HashMap<>();
        pilot.put("id", id);
        pilot.putAll(payload);
        store.getPilots().add(pilot);
        return pilot;
    }

    public Map<String, Object> updateMilestones(Long id, List<Map<String, Object>> milestones) {
        for (Map<String, Object> pilot : store.getPilots()) {
            if (pilot.get("id") != null && pilot.get("id").toString().equals(id.toString())) {
                pilot.put("milestones", milestones);
                return pilot;
            }
        }
        throw new IllegalArgumentException("Pilot not found");
    }
}
