
package com.civicbridge.repository;

import com.civicbridge.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByStartupId(Long startupId);

    boolean existsByIdAndStartupId(Long id, Long startupId);
}
