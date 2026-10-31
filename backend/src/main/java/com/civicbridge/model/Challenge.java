package com.civicbridge.model;

import java.util.ArrayList;
import java.util.List;

public class Challenge {
    private Long id;
    private String title;
    private String dept;
    private String category;
    private String deadline;
    private String description;
    private String status;
    private String badge;
    private int applications;
    private List<String> tags = new ArrayList<>();

    public Challenge() {
    }

    public Challenge(Long id, String title, String dept, String category, String deadline, String description, String status, String badge, int applications, List<String> tags) {
        this.id = id;
        this.title = title;
        this.dept = dept;
        this.category = category;
        this.deadline = deadline;
        this.description = description;
        this.status = status;
        this.badge = badge;
        this.applications = applications;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getApplications() {
        return applications;
    }

    public void setApplications(int applications) {
        this.applications = applications;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
