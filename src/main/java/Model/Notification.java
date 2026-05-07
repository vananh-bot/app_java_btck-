package Model;

import Enum.NotificationType;

import java.time.LocalDateTime;

public class Notification {

    private int id;

    private int userId;
    private Integer projectId;
    private Integer taskId;

    private String title;
    private String message;

    private NotificationType type;

    private boolean isRead;

    private Integer createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User sender;
    private Project project;
    private Task task;

    public Notification() {
    }

    public Notification(int id, int userId, Integer projectId, Integer taskId,
                        String title, String message,
                        NotificationType type, boolean isRead,
                        Integer createdBy,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Notification(int userId, Integer projectId, Integer taskId,
                        String title, String message,
                        NotificationType type, Integer createdBy) {
        this.userId = userId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdBy = createdBy;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isUnread() {
        return !isRead;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getSender() {
        return sender;
    }

    public Project getProject() {
        return project;
    }

    public Task getTask() {
        return task;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}