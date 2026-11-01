
package com.civicbridge.service;

import com.civicbridge.model.Proposal;
import com.civicbridge.repository.ProposalRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public ProposalService(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public List<Proposal> getProposals(String role, String org) {

        List<Proposal> filtered = new ArrayList<>();

        for (Proposal proposal : proposalRepository.findAll()) {

            boolean matchesRole =
                    role == null
                    || role.isBlank()
                    || role.equalsIgnoreCase("all")
                    || role.equalsIgnoreCase("startup");

            boolean matchesOrg =
                    org == null
                    || org.isBlank()
                    || org.equalsIgnoreCase("all")
                    || (proposal.getStartupName() != null
                        && proposal.getStartupName().toLowerCase()
                                .contains(org.toLowerCase()));

            if (matchesRole && matchesOrg) {
                filtered.add(proposal);
            }
        }

        return filtered;
    }

    public Proposal createProposal(
            Long challengeId,
            Long startupId,
            String startupName,
            String title,
            String summary,
            String status,
            int budget,
            List<String> technologies) {

        Proposal proposal = new Proposal();

        proposal.setChallengeId(challengeId);
        proposal.setStartupId(startupId);
        proposal.setStartupName(startupName);
        proposal.setTitle(title);
        proposal.setSummary(summary);

        proposal.setStatus(
                status == null || status.isBlank()
                        ? "Submitted"
                        : status);

        proposal.setBudget(budget);

        proposal.setTechnologies(
                technologies == null
                        ? new ArrayList<>()
                        : technologies);

        return proposalRepository.save(proposal);
    }

    public Proposal updateProposal(
            Long id,
            String title,
            String summary,
            String status,
            int budget,
            List<String> technologies) {

        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Proposal not found"));

        if (title != null) {
            proposal.setTitle(title);
        }

        if (summary != null) {
            proposal.setSummary(summary);
        }

        if (status != null) {
            proposal.setStatus(status);
        }

        if (budget >= 0) {
            proposal.setBudget(budget);
        }

        if (technologies != null) {
            proposal.setTechnologies(technologies);
        }

        return proposalRepository.save(proposal);
    }
}
