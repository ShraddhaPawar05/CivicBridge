
package com.civicbridge.model;

import java.io.Serializable;
import java.util.Objects;

public class SettingId implements Serializable {

    private Long userId;
    private String settingKey;

    public SettingId() {
    }

    public SettingId(Long userId, String settingKey) {
        this.userId = userId;
        this.settingKey = settingKey;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof SettingId)) return false;

        SettingId that = (SettingId) object;

        return Objects.equals(userId, that.userId)
                && Objects.equals(settingKey, that.settingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, settingKey);
    }
}
