package Model;
import Enum.Priority;
import Enum.TaskStatus;

import java.time.LocalDateTime;



public class Task {
    private int id;
    private String title;
    private String description;
    private String note;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime deadline;
    private int projectId;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(int id, String title, String description, String note, TaskStatus status, Priority priority, LocalDateTime deadline, int projectId, int createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.note = note;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Task() {

    }

    public Task(String title, String description, TaskStatus status, Priority priority, LocalDateTime deadline, int projectId, int createdBy) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProjectId() {
        return projectId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}