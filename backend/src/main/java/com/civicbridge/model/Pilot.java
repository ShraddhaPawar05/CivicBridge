
package com.civicbridge.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pilots")
public class Pilot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long challengeId;

    private Long startupId;

    private String status;

    private Integer progress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(
        mappedBy = "pilot",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<PilotMilestone> milestones = new ArrayList<>();

    public Pilot() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PilotMilestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<PilotMilestone> milestones) {
        this.milestones = milestones;
    }
}
