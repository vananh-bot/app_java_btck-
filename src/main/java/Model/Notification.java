package Model;

import java.time.LocalDateTime;
import Enum.NotificationType;

public class Notification {
    private int id;
    private int userId;
    private String content;
    private NotificationType type;
    private boolean is_read;
    private int targetId;
    private String targetType;
    private LocalDateTime created_at;


    public void markAsRead(){
        is_read = true;
   }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = NotificationType.valueOf(type);
    }

    public void setRead(boolean isRead) {
        this.is_read = isRead;
    }

    public void setCreatedAt(LocalDateTime createAt) {
        this.created_at = createAt;
    }

    public int getId() {
        return id;
    }

    public boolean isRead() {
        return is_read;
    }
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type.toString();
    }

    public int getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }
}
