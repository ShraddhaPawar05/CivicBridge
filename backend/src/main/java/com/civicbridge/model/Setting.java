
package com.civicbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
@IdClass(SettingId.class)
public class Setting {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "setting_key", length = 255)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;

    public Setting() {
    }

    public Setting(Long userId, String settingKey, String settingValue) {
        this.userId = userId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
