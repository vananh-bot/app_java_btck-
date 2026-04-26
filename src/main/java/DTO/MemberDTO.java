package DTO;

import Enum.Role;

public class MemberDTO {
    private int userId;
    private String name;
    private String email;
    private Role role;
    private String avatarUrl; // Sẽ dùng link pravatar ở đây

    public MemberDTO(int userId, String name, String email, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        // Tự động tạo link avatar dựa trên ID
        this.avatarUrl = "https://i.pravatar.cc/150?u=" + userId;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getAvatarUrl() { return avatarUrl; }
}