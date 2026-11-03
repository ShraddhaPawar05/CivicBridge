
package com.civicbridge.controller;

import com.civicbridge.model.Setting;
import com.civicbridge.model.UserAccount;
import com.civicbridge.repository.SettingRepository;
import com.civicbridge.repository.UserAccountRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SettingsController {

    private final SettingRepository settingRepository;
    private final UserAccountRepository userAccountRepository;

    public SettingsController(
            SettingRepository settingRepository,
            UserAccountRepository userAccountRepository) {
        this.settingRepository = settingRepository;
        this.userAccountRepository = userAccountRepository;
    }

    // All authenticated roles can view their own settings
    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @GetMapping("/settings")
    public Map<String, Object> getSettings(Authentication authentication) {

        UserAccount user = getAuthenticatedUser(authentication);

        Map<String, Object> settings = new LinkedHashMap<>();

        List<Setting> savedSettings =
                settingRepository.findByUserId(user.getId());

        for (Setting setting : savedSettings) {
            settings.put(
                    setting.getSettingKey(),
                    convertValue(setting.getSettingValue())
            );
        }

        return settings;
    }

    // All authenticated roles can update their own settings
    @PreAuthorize("hasAnyRole('GOV', 'STARTUP', 'EVALUATOR')")
    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> saveSettings(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        UserAccount user = getAuthenticatedUser(authentication);

        for (Map.Entry<String, Object> entry : payload.entrySet()) {

            Setting setting = new Setting(
                    user.getId(),
                    entry.getKey(),
                    null
            );

            setting = settingRepository.findById(
                    new com.civicbridge.model.SettingId(
                            user.getId(),
                            entry.getKey()
                    )
            ).orElse(setting);

            setting.setSettingValue(
                    entry.getValue() == null
                            ? null
                            : entry.getValue() instanceof String
                                    ? (String) entry.getValue()
                                    : convertToJson(entry.getValue())
            );

            settingRepository.save(setting);
        }

        return ResponseEntity.ok(getSettings(authentication));
    }

    private UserAccount getAuthenticatedUser(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        return userAccountRepository.findByEmailIgnoreCase(
                authentication.getName()
        ).orElseThrow(() ->
                new IllegalStateException("Authenticated user not found"));
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
