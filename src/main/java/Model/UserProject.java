package Model;

import java.time.LocalDateTime;
import Enum.Role;

public class UserProject {

    private int userId;
    private int projectId;
    private Role role; // dùng enum thay vì String
    private LocalDateTime joinedAt;

    public UserProject() {}

    // full constructor
    public UserProject(int userId, int projectId, Role role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.projectId = projectId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // constructor khi thêm user vào project
    public UserProject(int userId, int projectId, Role role) {
        this.userId = userId;
        this.projectId = projectId;
        this.role = role;
    }

    // getter
    public int getUserId() { return userId; }
    public int getProjectId() { return projectId; }
    public Role getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    // setter
    public void setUserId(int userId) { this.userId = userId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public void setRole(Role role) { this.role = role; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    //  tiện dùng
    public boolean isOwner() {
        return role == Role.OWNER;
    }
}