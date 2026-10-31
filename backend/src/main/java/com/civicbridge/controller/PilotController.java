package com.civicbridge.controller;

import com.civicbridge.service.PilotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PilotController {

    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @GetMapping("/pilots")
    public List<Map<String, Object>> getPilots() {
        return pilotService.getPilots();
    }

    @PutMapping("/pilots/{id}")
    public ResponseEntity<Map<String, Object>> updatePilot(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pilotService.updatePilot(id, payload));
    }

    @PutMapping("/pilots/{id}/milestones")
    public ResponseEntity<Map<String, Object>> updateMilestones(@PathVariable Long id, @RequestBody List<Map<String, Object>> milestones) {
        return ResponseEntity.ok(pilotService.updateMilestones(id, milestones));
    }
}
