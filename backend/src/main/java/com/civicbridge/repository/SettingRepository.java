
package com.civicbridge.repository;

import com.civicbridge.model.Setting;
import com.civicbridge.model.SettingId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository
        extends JpaRepository<Setting, SettingId> {

    List<Setting> findByUserId(Long userId);
}
