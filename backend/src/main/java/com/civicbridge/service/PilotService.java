
package com.civicbridge.service;

import com.civicbridge.model.Pilot;
import com.civicbridge.model.PilotMilestone;
import com.civicbridge.repository.PilotRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PilotService {

    private final PilotRepository pilotRepository;

    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    public List<Map<String, Object>> getPilots() {

        List<Map<String, Object>> pilots = new ArrayList<>();

        for (Pilot pilot : pilotRepository.findAll()) {
            pilots.add(convertToMap(pilot));
        }

        return pilots;
    }

    public Map<String, Object> updatePilot(Long id, Map<String, Object> payload) {

        Pilot pilot = pilotRepository.findById(id)
                .orElseGet(() -> {
                    Pilot newPilot = new Pilot();
                    newPilot.setId(id);
                    return newPilot;
                });

        if (payload.containsKey("challengeId")) {
            pilot.setChallengeId(
                    toLong(payload.get("challengeId")));
        }

        if (payload.containsKey("startupId")) {
            pilot.setStartupId(
                    toLong(payload.get("startupId")));
        }

        if (payload.containsKey("status")) {
            pilot.setStatus((String) payload.get("status"));
        }

        if (payload.containsKey("progress")) {
            pilot.setProgress(
                    toInteger(payload.get("progress")));
        }

        if (payload.containsKey("notes")) {
            pilot.setNotes((String) payload.get("notes"));
        }

        if (payload.containsKey("milestones")) {
            updateMilestoneList(pilot, payload.get("milestones"));
        }

        Pilot savedPilot = pilotRepository.save(pilot);

        return convertToMap(savedPilot);
    }

    public Map<String, Object> updateMilestones(
            Long id,
            List<Map<String, Object>> milestones) {

        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pilot not found"));

        updateMilestoneList(pilot, milestones);

        Pilot savedPilot = pilotRepository.save(pilot);

        return convertToMap(savedPilot);
    }

    private void updateMilestoneList(Pilot pilot, Object milestoneData) {

        if (!(milestoneData instanceof List<?>)) {
            throw new IllegalArgumentException("Milestones must be a list");
        }

        List<?> items = (List<?>) milestoneData;

        pilot.getMilestones().clear();

        for (Object item : items) {

            if (!(item instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("Invalid milestone data");
            }

            Map<?, ?> milestoneMap = (Map<?, ?>) item;

            PilotMilestone milestone = new PilotMilestone();

            milestone.setTitle(
                    (String) milestoneMap.get("title"));

            milestone.setStatus(
                    (String) milestoneMap.get("status"));

            milestone.setPilot(pilot);

            pilot.getMilestones().add(milestone);
        }
    }

    private Map<String, Object> convertToMap(Pilot pilot) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", pilot.getId());
        result.put("challengeId", pilot.getChallengeId());
        result.put("startupId", pilot.getStartupId());
        result.put("status", pilot.getStatus());
        result.put("progress", pilot.getProgress());
        result.put("notes", pilot.getNotes());

        List<Map<String, Object>> milestones = new ArrayList<>();

        for (PilotMilestone milestone : pilot.getMilestones()) {

            Map<String, Object> milestoneMap = new LinkedHashMap<>();

            milestoneMap.put("title", milestone.getTitle());
            milestoneMap.put("status", milestone.getStatus());

            milestones.add(milestoneMap);
        }

        result.put("milestones", milestones);

        return result;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        return value instanceof Number
                ? ((Number) value).longValue()
                : Long.parseLong(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        return value instanceof Number
                ? ((Number) value).intValue()
                : Integer.parseInt(value.toString());
    }
}
