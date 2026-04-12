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
        lblProjectName.setText(dto.getProject().getName());
        progressIndicator.setProgress(dto.getProgress());

        if (lblTodoCount != null) lblTodoCount.setText(String.valueOf(dto.getTodoCount()));
        if (lblInProgressCount != null) lblInProgressCount.setText(String.valueOf(dto.getInProgressCount()));
        if (lblDoneCount != null) lblDoneCount.setText(String.valueOf(dto.getDoneCount()));
    }
}