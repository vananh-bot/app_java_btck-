package Controller;

import DTO.ProjectCardDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ProjectCardController {
    @FXML private Label lblProjectName;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblTodoCount;
    @FXML private Label lblInProgressCount;
    @FXML private Label lblDoneCount;

    public void setProjectData(ProjectCardDTO dto) {
        if (dto == null || dto.getProject() == null) return;

        // Dùng Platform.runLater để đảm bảo UI đã sẵn sàng
        javafx.application.Platform.runLater(() -> {
            if (lblProjectName != null) lblProjectName.setText(dto.getProject().getName());

            if (progressIndicator != null) {
                progressIndicator.setProgress(dto.getProgress());
            }

            if (lblTodoCount != null) lblTodoCount.setText(String.valueOf(dto.getTodoCount()));
            if (lblInProgressCount != null) lblInProgressCount.setText(String.valueOf(dto.getInProgressCount()));
            if (lblDoneCount != null) lblDoneCount.setText(String.valueOf(dto.getDoneCount()));
        });
    }
}