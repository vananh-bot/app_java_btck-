package Controller;

import Model.ProjectDashboardDTO;
import Utils.ScreenManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import Enum.Screen;


public class DashboardProjectCardController {

    @FXML
    private Circle bgCircle;

    @FXML
    private Label lblDoneCount;

    @FXML
    private Label lblInProgressCount;

    @FXML
    private Label lblProjectName;

    @FXML
    private Label lblTodoCount;

    @FXML
    private Label percentLabel;

    @FXML
    private Circle progressCircle;

    @FXML
    private VBox projectCard;

    private int projectId;

    @FXML
    public void initialize(){
        // chuẩn bị vòng tròn progress ban đầu (0%)
        double radius = progressCircle.getRadius();
        double circumference = 2 * Math.PI * radius;

        progressCircle.getStrokeDashArray().setAll(circumference);
        progressCircle.setStrokeDashOffset(circumference);
    }

    public void setData(ProjectDashboardDTO project){
        projectId = project.getId();

        lblProjectName.setText(project.getName());
        lblTodoCount.setText(project.getToDoCount() + "");
        lblInProgressCount.setText(project.getInProgressCount() + "");
        lblDoneCount.setText(project.getDoneCount() + "");

        double progress = project.getProgress();
        percentLabel.setText((int)(progress * 100) + "%");

        setCircleProgress(progress);

    }

    public void setCircleProgress(double progress){
        double radius = progressCircle.getRadius();
        double circumference = 2 * Math.PI * radius;

        progressCircle.getStrokeDashArray().setAll(circumference);
        progressCircle.getStyleClass().removeAll(
                "progress-high",
                "progress-medium",
                "progress-low"
        );

        if(progress < 0.3)
            progressCircle.getStyleClass().add("progress-high");
        else if(progress < 0.7)
            progressCircle.getStyleClass().add("progress-medium");
        else
            progressCircle.getStyleClass().add("progress-low");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(
                                progressCircle.strokeDashOffsetProperty(),
                                circumference * (1 - progress)
                        )
                )
        );

        timeline.play();
    }

    public String getProjectName(){
        return lblProjectName.getText();
    }

    @FXML
    void goToProjectAfterClickCard(MouseEvent event) {
        ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW, projectId);
    }

}
