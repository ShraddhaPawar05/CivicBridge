
package com.civicbridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civicbridge.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByChallengeId(Long challengeId);

    List<Match> findByStartupId(Long startupId);

    List<Match> findByStartupIdAndChallengeId(Long startupId, Long challengeId);
}
