
package com.civicbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_settings")
public class Setting {

    @Id
    private String settingKey;

    @Column(columnDefinition = "TEXT")
    private String settingValue;

    public Setting() {
    }

    public Setting(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
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

