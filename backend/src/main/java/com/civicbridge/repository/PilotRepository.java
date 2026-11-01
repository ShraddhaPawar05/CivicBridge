
package com.civicbridge.repository;

import com.civicbridge.model.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
}
