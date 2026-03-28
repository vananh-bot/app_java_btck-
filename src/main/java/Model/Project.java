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

    public boolean isOwner(int userId){
        return ownerId == userId;
    }
}
