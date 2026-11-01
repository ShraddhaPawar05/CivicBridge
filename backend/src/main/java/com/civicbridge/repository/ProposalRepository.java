
package com.civicbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.civicbridge.model.Proposal;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
