
package com.civicbridge.service;

import com.civicbridge.model.Pilot;
import com.civicbridge.model.PilotMilestone;
import com.civicbridge.repository.PilotRepository;
import com.civicbridge.repository.UserAccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PilotService {

    private final PilotRepository pilotRepository;
    private final UserAccountRepository userRepository;

    public PilotService(
            PilotRepository pilotRepository,
            UserAccountRepository userRepository) {
        this.pilotRepository = pilotRepository;
        this.userRepository = userRepository;
    }

    // Startups see only their own pilots.
    // Government and Evaluators can see all pilots.
    public List<Map<String, Object>> getPilots(
            String email, boolean canViewAll) {

        List<Pilot> pilotRecords;

        if (canViewAll) {
            pilotRecords = pilotRepository.findAll();
        } else {
            Long startupId = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found"))
                    .getId();

            pilotRecords = pilotRepository.findByStartupId(startupId);
        }

        List<Map<String, Object>> pilots = new ArrayList<>();

        for (Pilot pilot : pilotRecords) {
            pilots.add(convertToMap(pilot));
        }

        return pilots;
    }

    // Government and Evaluators can update pilot details.
    public Map<String, Object> updatePilot(
            Long id, Map<String, Object> payload) {

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

    // Save startup milestone updates, notes, status and progress.
    // Startups can update only their own pilots.
    // Evaluators can update all pilots.
    public Map<String, Object> updateMilestones(
            Long id,
            Object milestones,
            String notes,
            String status,
            Integer progress,
            String email,
            boolean canUpdateAll) {

        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pilot not found"));

        // Check pilot ownership for Startup accounts.
        if (!canUpdateAll) {

            Long startupId = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found"))
                    .getId();

            if (!startupId.equals(pilot.getStartupId())) {
                throw new AccessDeniedException(
                        "You are not allowed to update this pilot");
            }
        }

        updateMilestoneList(pilot, milestones);

        pilot.setNotes(notes);

        if (status != null) {
            pilot.setStatus(status);
        }

        if (progress != null) {
            pilot.setProgress(progress);
        }

        Pilot savedPilot = pilotRepository.save(pilot);

        return convertToMap(savedPilot);
    }

    // Update milestone records.
    private void updateMilestoneList(
            Pilot pilot, Object milestoneData) {

        if (!(milestoneData instanceof List<?>)) {
            throw new IllegalArgumentException(
                    "Milestones must be a list");
        }

        List<?> items = (List<?>) milestoneData;

        pilot.getMilestones().clear();

        for (Object item : items) {

            if (!(item instanceof Map<?, ?>)) {
                throw new IllegalArgumentException(
                        "Invalid milestone data");
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

    // Convert pilot entity into API response.
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
