package Controller;

import DAO.TaskDAO;
import DTO.ProjectCardDTO;
import Model.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

public class ProjectCardController {
    @FXML private Label lblProjectName;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblTodoCount;
    @FXML private Label lblInProgressCount;
    @FXML private Label lblDoneCount;

    public void setProjectData(ProjectCardDTO dto) {
        if (dto == null || dto.getProject() == null) return;

        Platform.runLater(() -> {
            // 👉 Tên project
            if (lblProjectName != null) {
                lblProjectName.setText(dto.getProject().getName());
            }

            // 👉 HIỂN THỊ LOADING
            if (lblTodoCount != null) lblTodoCount.setText("...");
            if (lblInProgressCount != null) lblInProgressCount.setText("...");
            if (lblDoneCount != null) lblDoneCount.setText("...");

            // 👉 progress có thể set tạm
            if (progressIndicator != null) {
                progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            }
        });
    }
    public void loadTaskStatsAsync(int projectId) {
        new Thread(() -> {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getTasksByProjectId(projectId);

            int todo = 0, inProgress = 0, done = 0;

            for (Task t : tasks) {
                if (t.getStatus() != null) {
                    switch (t.getStatus()) {
                        case TODO -> todo++;
                        case IN_PROGRESS -> inProgress++;
                        case DONE -> done++;
                    }
                }
            }

            int finalTodo = todo;
            int finalInProgress = inProgress;
            int finalDone = done;

            Platform.runLater(() -> {
                updateTaskUI(finalTodo, finalInProgress, finalDone);
            });
        }).start();
    }
    private void updateTaskUI(int todo, int inProgress, int done) {
        lblTodoCount.setText(String.valueOf(todo));
        lblInProgressCount.setText(String.valueOf(inProgress));
        lblDoneCount.setText(String.valueOf(done));

        progressIndicator.setProgress(
                (double) done / (todo + inProgress + done)
        );
    }
}