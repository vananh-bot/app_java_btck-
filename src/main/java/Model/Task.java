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

    public void updateStatus(TaskStatus status){
        this.status = status;
    }
    public void updateDeadline(LocalDateTime deadline){
        this.deadline = deadline;
    }
}
