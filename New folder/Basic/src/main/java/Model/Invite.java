package Model;

import java.time.LocalDateTime;
import Enum.InviteStatus;

public class Invite {
    private int id;
    private String email;
    private String token;
    private int projectId;
    private InviteStatus status;
    private LocalDateTime expiresAt;

    public boolean isValid(){
        return expiresAt.isAfter(LocalDateTime.now());
    }
}
