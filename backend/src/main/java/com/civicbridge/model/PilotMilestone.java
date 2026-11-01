
package com.civicbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pilot_milestones")
public class PilotMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilot_id", nullable = false)
    private Pilot pilot;

    public PilotMilestone() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }
}
