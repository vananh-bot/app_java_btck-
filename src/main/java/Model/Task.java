package Model;
import Enum.Priority;
import Enum.TaskStatus;

import java.time.LocalDateTime;



public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime deadline;
    private int projectId;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(int id, String title, String description, TaskStatus status, Priority priority, LocalDateTime deadline, int projectId, int createdBy, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Task(){

    }

    public Task(String title, String description, TaskStatus status, Priority priority, LocalDateTime deadline, int projectId, int createdBy){
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdBy = createdBy;
    }

    public void updateStatus(TaskStatus status){
        this.status = status;
    }
    public void updateDeadline(LocalDateTime deadline){
        this.deadline = deadline;
    }
}
