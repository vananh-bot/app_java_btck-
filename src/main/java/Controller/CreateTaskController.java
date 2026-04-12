package Controller;

public class CreateTaskController {
    private int projectId;
    private Runnable onTaskCreated;

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setOnTaskCreated(Runnable onTaskCreated) {
        this.onTaskCreated = onTaskCreated;
    }
}
