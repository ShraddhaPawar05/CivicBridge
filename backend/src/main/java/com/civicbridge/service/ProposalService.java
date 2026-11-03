
package com.civicbridge.service;

import com.civicbridge.model.Proposal;
import com.civicbridge.model.UserAccount;
import com.civicbridge.repository.ProposalRepository;
import com.civicbridge.repository.UserAccountRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserAccountRepository userAccountRepository;

    public ProposalService(
            ProposalRepository proposalRepository,
            UserAccountRepository userAccountRepository) {
        this.proposalRepository = proposalRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<Proposal> getProposals(
            String role, String org, String userEmail, String userRole) {

        if ("STARTUP".equalsIgnoreCase(userRole)) {
            UserAccount user = getAuthenticatedUser(userEmail);
            return proposalRepository.findByStartupId(user.getId());
        }

        return proposalRepository.findAll().stream()
                .filter(proposal ->
                        org == null || org.isBlank()
                        || org.equalsIgnoreCase("all")
                        || (proposal.getStartupName() != null
                        && proposal.getStartupName().toLowerCase()
                        .contains(org.toLowerCase())))
                .toList();
    }

    public Proposal createProposal(
            Long challengeId,
            String title,
            String summary,
            int budget,
            List<String> technologies,
            String userEmail) {

        UserAccount user = getAuthenticatedUser(userEmail);

        if (!"STARTUP".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException(
                    "Only startups can submit proposals");
        }

        if (challengeId == null || title == null || title.isBlank()
                || summary == null || summary.isBlank() || budget <= 0) {
            throw new IllegalArgumentException(
                    "Challenge ID, title, summary and positive budget are required");
        }

        Proposal proposal = new Proposal();
        proposal.setChallengeId(challengeId);
        proposal.setStartupId(user.getId());
        proposal.setStartupName(user.getStartupName());
        proposal.setTitle(title);
        proposal.setSummary(summary);
        proposal.setStatus("Submitted");
        proposal.setBudget(budget);
        proposal.setTechnologies(
                technologies == null ? new ArrayList<>() : technologies);

        return proposalRepository.save(proposal);
    }

    @Transactional
    public Proposal updateOwnProposal(
            Long id,
            String title,
            String summary,
            int budget,
            List<String> technologies,
            String userEmail) {

        UserAccount user = getAuthenticatedUser(userEmail);

        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Proposal not found"));

        if (!proposal.getStartupId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You can update only your own proposals");
        }

        if (title != null && !title.isBlank()) {
            proposal.setTitle(title);
        }

        if (summary != null && !summary.isBlank()) {
            proposal.setSummary(summary);
        }

        if (budget > 0) {
            proposal.setBudget(budget);
        }

        if (technologies != null) {
            proposal.setTechnologies(technologies);
        }

        return proposalRepository.save(proposal);
    }

    public Proposal updateProposalStatus(Long id, String status) {

        if (status == null || !List.of(
                "Submitted", "Approved", "Rejected"
        ).contains(status)) {
            throw new IllegalArgumentException("Invalid proposal status");
        }

        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Proposal not found"));

        proposal.setStatus(status);
        return proposalRepository.save(proposal);
    }

    private UserAccount getAuthenticatedUser(String email) {
        if (email == null || email.isBlank()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new AccessDeniedException("User account not found"));
    }
}
