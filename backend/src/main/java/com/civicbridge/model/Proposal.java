
package com.civicbridge.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposals")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long challengeId;
    private Long startupId;
    private String startupName;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String status;
    private int budget;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "proposal_technologies",
        joinColumns = @JoinColumn(name = "proposal_id")
    )
    @Column(name = "technology")
    private List<String> technologies = new ArrayList<>();

    public Proposal() {
    }

    public Proposal(Long id, Long challengeId, Long startupId,
            String startupName, String title, String summary,
            String status, int budget, List<String> technologies) {

        this.id = id;
        this.challengeId = challengeId;
        this.startupId = startupId;
        this.startupName = startupName;
        this.title = title;
        this.summary = summary;
        this.status = status;
        this.budget = budget;
        this.technologies = technologies == null
                ? new ArrayList<>()
                : technologies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Long getStartupId() {
        return startupId;
    }

    public void setStartupId(Long startupId) {
        this.startupId = startupId;
    }

    public String getStartupName() {
        return startupName;
    }

    public void setStartupName(String startupName) {
        this.startupName = startupName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies == null
                ? new ArrayList<>()
                : technologies;
    }
}
