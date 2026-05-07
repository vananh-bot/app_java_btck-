package Model;

import java.sql.Timestamp;

import Enum.JoinMode;


public class InviteLink {
    private int id;
    private int projectId;
    private String token;
    private JoinMode joinMode;
    private boolean isActive;
    private Timestamp createdAt;

    // Getter
    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getToken() {
        return token;
    }

    public JoinMode getJoinMode() {
        return joinMode;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setJoinMode(JoinMode joinMode) {
        this.joinMode = joinMode;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
