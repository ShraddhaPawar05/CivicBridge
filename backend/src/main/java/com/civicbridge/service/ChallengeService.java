package com.civicbridge.service;

import com.civicbridge.model.Challenge;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ChallengeService {

    private final InMemoryDataStore store;

    public ChallengeService(InMemoryDataStore store) {
        this.store = store;
    }

    public List<Challenge> getChallenges(String status, String category, String search) {
        List<Challenge> filtered = new ArrayList<>();
        String normalStatus = status == null ? "" : status.trim();
        String normalCategory = category == null ? "" : category.trim();
        String normalSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        for (Challenge challenge : store.getChallenges()) {
            boolean matchesStatus = normalStatus.isBlank() || normalStatus.equalsIgnoreCase("All") || normalStatus.equalsIgnoreCase(challenge.getStatus());
            boolean matchesCategory = normalCategory.isBlank() || normalCategory.equalsIgnoreCase("All") || normalCategory.equalsIgnoreCase(challenge.getCategory());
            boolean matchesSearch = normalSearch.isBlank()
                || challenge.getTitle().toLowerCase(Locale.ROOT).contains(normalSearch)
                || challenge.getDept().toLowerCase(Locale.ROOT).contains(normalSearch)
                || challenge.getCategory().toLowerCase(Locale.ROOT).contains(normalSearch);

            if (matchesStatus && matchesCategory && matchesSearch) {
                filtered.add(challenge);
            }
        }

        return filtered;
    }

    public Challenge createChallenge(String title, String dept, String category, String deadline, String description, String status, List<String> tags) {
        Challenge challenge = new Challenge();
        challenge.setId(store.nextChallengeId());
        challenge.setTitle(title);
        challenge.setDept(dept);
        challenge.setCategory(category);
        challenge.setDeadline(deadline);
        challenge.setDescription(description);
        challenge.setStatus(status == null || status.isBlank() ? "Open" : status);
        challenge.setBadge("badge-purple");
        challenge.setApplications(0);
        challenge.setTags(tags == null ? new ArrayList<>() : tags);

        store.getChallenges().add(challenge);
        return challenge;
    }

    public Challenge updateChallenge(Long id, String title, String dept, String category, String deadline, String description, String status, List<String> tags) {
        for (Challenge challenge : store.getChallenges()) {
            if (challenge.getId().equals(id)) {
                challenge.setTitle(title);
                challenge.setDept(dept);
                challenge.setCategory(category);
                challenge.setDeadline(deadline);
                challenge.setDescription(description);
                challenge.setStatus(status == null || status.isBlank() ? challenge.getStatus() : status);
                challenge.setTags(tags == null ? challenge.getTags() : tags);
                return challenge;
            }
        }
        throw new IllegalArgumentException("Challenge not found");
    }

    public boolean deleteChallenge(Long id) {
        return store.getChallenges().removeIf(challenge -> challenge.getId().equals(id));
    }
}
