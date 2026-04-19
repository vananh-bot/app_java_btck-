package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Project {

    private int id;
    private String name;
    private String description;
    private int ownerId;
    private String inviteCode;
    private LocalDateTime createdAt;

    private List<User> members;

    public Project() {}

    // full constructor (lấy từ DB)
    public Project(int id, String name, String description, int ownerId,
                   String inviteCode, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.inviteCode = inviteCode;
        this.createdAt = createdAt;
    }

    // constructor tạo mới
    public Project(String name, String description, int ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    // 🔥 check owner
    public boolean isOwner(int userId) {
        return this.ownerId == userId;
    }

    // getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getOwnerId() { return ownerId; }
    public String getInviteCode() { return inviteCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<User> getMembers() { return members; }

    // setter
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setMembers(List<User> members) { this.members = members; }
}