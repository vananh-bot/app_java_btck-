package Model;

import java.time.LocalDateTime;
import Enum.Priority;

public class TaskDashboardDTO {
    private int id;
    private String title;
    private LocalDateTime deadline;
    private Priority priority;

    public TaskDashboardDTO(int id, String title, Priority priority, LocalDateTime deadline){
        this.id = id;
        this.title = title;
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
}
