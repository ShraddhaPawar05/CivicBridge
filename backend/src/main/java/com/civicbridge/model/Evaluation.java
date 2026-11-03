package com.civicbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long proposalId;

    private Integer score;

    private String recommendation;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String scoresJson;

    @Column(columnDefinition = "TEXT")
    private String comments;

    public Evaluation() {
    }

    public Evaluation(Long proposalId, Integer score, String recommendation, String status) {
        this.proposalId = proposalId;
        this.score = score;
        this.recommendation = recommendation;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScoresJson() {
        return scoresJson;
    }

    public void setScoresJson(String scoresJson) {
        this.scoresJson = scoresJson;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}