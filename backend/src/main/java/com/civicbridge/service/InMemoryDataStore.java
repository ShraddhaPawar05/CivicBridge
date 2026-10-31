package com.civicbridge.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.civicbridge.model.Challenge;
import com.civicbridge.model.Proposal;
import com.civicbridge.model.UserAccount;

@Component
public class InMemoryDataStore {
    private final AtomicLong userSequence = new AtomicLong(1L);
    private final AtomicLong challengeSequence = new AtomicLong(101L);
    private final AtomicLong proposalSequence = new AtomicLong(201L);

    private final List<UserAccount> users = new ArrayList<>();
    private final List<Challenge> challenges = new ArrayList<>();
    private final List<Proposal> proposals = new ArrayList<>();
    private final List<Map<String, Object>> evaluations = new ArrayList<>();
    private final List<Map<String, Object>> pilots = new ArrayList<>();
    private final Map<String, Object> settings = new ConcurrentHashMap<>();

    public InMemoryDataStore() {
        seedDefaults();
    }

    private void seedDefaults() {
        users.add(new UserAccount(1L, "Government Admin", "gov@civicbridge.io", "$2a$10$8gLQcaDrW7nW6jB5fJ9M0ONgYNJ0Wz6iuRzE3dR0nYX9dK4hApwD6", "gov", "Urban Department", "Program Director"));
        users.add(new UserAccount(2L, "Startup Lead", "startup@civicbridge.io", "$2a$10$8gLQcaDrW7nW6jB5fJ9M0ONgYNJ0Wz6iuRzE3dR0nYX9dK4hApwD6", "startup", "Product", "Founder"));
        users.add(new UserAccount(3L, "Evaluator User", "evaluator@civicbridge.io", "$2a$10$8gLQcaDrW7nW6jB5fJ9M0ONgYNJ0Wz6iuRzE3dR0nYX9dK4hApwD6", "evaluator", "Impact", "Analyst"));

        challenges.add(new Challenge(101L, "Smart Waste Collection", "Sanitation Department", "Smart Cities", "2026-10-30", "Optimize city waste collection using AI and smart bins.", "Open", "badge-blue", 12, List.of("AI", "IoT", "Smart City")));
        challenges.add(new Challenge(102L, "Urban Water Quality Monitoring", "Water Authority", "Environment", "2026-11-15", "Track water quality across the municipal network.", "Evaluation", "badge-amber", 9, List.of("IoT", "Analytics", "Environment")));
        challenges.add(new Challenge(103L, "Traffic Congestion Monitoring", "Transport Department", "Transport", "2026-11-20", "Reduce congestion with adaptive traffic signals.", "Pilot", "badge-teal", 11, List.of("AI", "Computer Vision", "Transport")));

        proposals.add(new Proposal(201L, 101L, 2L, "EcoTech Solutions", "AI Route Optimizer", "Smart routing and sensor-based pickup optimization.", "Submitted", 340000, List.of("AI", "IoT", "Python")));
        proposals.add(new Proposal(202L, 101L, 3L, "UrbanAI Labs", "Predictive Fleet Planning", "City-wide waste planning with predictive analytics.", "Under Review", 290000, List.of("Analytics", "Cloud", "ML")));

        evaluations.add(Map.of(
            "proposalId", 201L,
            "score", 87,
            "recommendation", "Recommended",
            "status", "Reviewed"
        ));

        pilots.add(Map.of(
            "id", 301L,
            "challengeId", 103L,
            "startupId", 2L,
            "status", "In Progress",
            "progress", 42,
            "notes", "Pilot deployment is progressing on schedule.",
            "milestones", List.of(
                Map.of("title", "Sandbox setup", "status", "Completed"),
                Map.of("title", "Field testing", "status", "In Progress")
            )
        ));

        settings.put("theme", "dark");
        settings.put("notifications", true);
        settings.put("preferredCategories", List.of("Smart Cities", "Environment"));
    }

    public List<UserAccount> getUsers() {
        return users;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public List<Proposal> getProposals() {
        return proposals;
    }

    public List<Map<String, Object>> getEvaluations() {
        return evaluations;
    }

    public List<Map<String, Object>> getPilots() {
        return pilots;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public Long nextUserId() {
        return userSequence.getAndIncrement();
    }

    public Long nextChallengeId() {
        return challengeSequence.getAndIncrement();
    }

    public Long nextProposalId() {
        return proposalSequence.getAndIncrement();
    }

    public UserAccount findUserByEmail(String email) {
        for (UserAccount user : users) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
}
