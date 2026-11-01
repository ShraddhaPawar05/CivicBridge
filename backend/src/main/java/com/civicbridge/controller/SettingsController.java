
package com.civicbridge.controller;

import com.civicbridge.model.Setting;
import com.civicbridge.repository.SettingRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SettingsController {

    private final SettingRepository settingRepository;

    public SettingsController(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {

        Map<String, Object> settings = new LinkedHashMap<>();

        List<Setting> savedSettings = settingRepository.findAll();

        for (Setting setting : savedSettings) {
            settings.put(
                    setting.getSettingKey(),
                    convertValue(setting.getSettingValue())
            );
        }

        return settings;
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> saveSettings(
            @RequestBody Map<String, Object> payload) {

        for (Map.Entry<String, Object> entry : payload.entrySet()) {

            Setting setting = settingRepository
                    .findById(entry.getKey())
                    .orElseGet(() -> new Setting(entry.getKey(), null));

            setting.setSettingValue(
                    entry.getValue() == null
                            ? null
                            : entry.getValue() instanceof String
                                    ? (String) entry.getValue()
                                    : convertToJson(entry.getValue())
            );

            settingRepository.save(setting);
        }

        return ResponseEntity.ok(getSettings());
    }

    private Object convertValue(String value) {

        if (value == null) {
            return null;
        }

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        if (value.startsWith("[") || value.startsWith("{")) {
            try {
                return new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(value, Object.class);
            } catch (Exception ignored) {
                return value;
            }
        }

        return value;
    }

    private String convertToJson(Object value) {

        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid setting value");
        }
    }
}
