package com.civicbridge.controller;

import com.civicbridge.service.InMemoryDataStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SettingsController {

    private final InMemoryDataStore store;

    public SettingsController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        return store.getSettings();
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> saveSettings(@RequestBody Map<String, Object> payload) {
        store.getSettings().putAll(payload);
        return ResponseEntity.ok(store.getSettings());
    }
}
