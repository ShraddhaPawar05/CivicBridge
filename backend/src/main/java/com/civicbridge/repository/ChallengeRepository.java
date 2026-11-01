
package com.civicbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.civicbridge.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
