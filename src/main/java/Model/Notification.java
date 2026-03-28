package Model;

import java.time.LocalDateTime;
import Enum.NotificationType;

public class Notification {
    private int id;
    private int userId;
    private String content;
    private NotificationType type;
    private boolean isRead;
    private int targetId;
    private String targetType;
    private LocalDateTime createdAt;

    public void markAsRead(){
        isRead = true;
    }
}
