package com.civicbridge.service;

import com.civicbridge.model.Proposal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalService {

    private final InMemoryDataStore store;

    public ProposalService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<Proposal> getProposals(String role, String org) {
        List<Proposal> filtered = new ArrayList<>();
        for (Proposal proposal : store.getProposals()) {
            boolean matchesRole = role == null || role.isBlank() || role.equalsIgnoreCase("all") || role.equalsIgnoreCase("startup");
            boolean matchesOrg = org == null || org.isBlank() || org.equalsIgnoreCase("all") || proposal.getStartupName().contains(org);
            if (matchesRole && matchesOrg) {
                filtered.add(proposal);
            }
        }
        return filtered;
    }

    public Proposal createProposal(Long challengeId, Long startupId, String startupName, String title, String summary, String status, int budget, List<String> technologies) {
        Proposal proposal = new Proposal();
        proposal.setId(store.nextProposalId());
        proposal.setChallengeId(challengeId);
        proposal.setStartupId(startupId);
        proposal.setStartupName(startupName);
        proposal.setTitle(title);
        proposal.setSummary(summary);
        proposal.setStatus(status == null || status.isBlank() ? "Submitted" : status);
        proposal.setBudget(budget);
        proposal.setTechnologies(technologies == null ? new ArrayList<>() : technologies);
        store.getProposals().add(proposal);
        return proposal;
    }

    public Proposal updateProposal(Long id, String title, String summary, String status, int budget, List<String> technologies) {
        for (Proposal proposal : store.getProposals()) {
            if (proposal.getId().equals(id)) {
                if (title != null) proposal.setTitle(title);
                if (summary != null) proposal.setSummary(summary);
                if (status != null) proposal.setStatus(status);
                if (budget >= 0) proposal.setBudget(budget);
                if (technologies != null) proposal.setTechnologies(technologies);
                return proposal;
            }
        }
        throw new IllegalArgumentException("Proposal not found");
    }
}
