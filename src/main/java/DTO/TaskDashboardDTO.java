package DTO;

import java.time.LocalDateTime;
import Enum.Priority;
import Enum.TaskStatus;

public class TaskDashboardDTO {
    private int id;
    private String title;
    private LocalDateTime deadline;
    private TaskStatus status;
    private Priority priority;
    private String projectName;
    private String userEmail;

    public TaskDashboardDTO(int id, String title, String projectName, TaskStatus status, Priority priority, LocalDateTime deadline){
        this.id = id;
        this.title = title;
        this.projectName = projectName;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }
    public String getUserEmail(){
        return userEmail;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
