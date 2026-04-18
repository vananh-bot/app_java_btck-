package Controller;

import DAO.TaskDAO;
import DTO.ProjectCardDTO;
import Model.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.animation.Interpolator;

public class ProjectCardController {
    @FXML private Label lblProjectName;
    @FXML private Label lblTodoCount;
    @FXML private Label lblInProgressCount;
    @FXML private Label lblDoneCount;

    @FXML private Circle progressCircle;
    @FXML private Label percentLabel;

    private final double RADIUS = 45;
    private final double CIRCUMFERENCE = 2 * Math.PI * RADIUS;
    private Timeline timeline;
    private Timeline numberAnim;
    private ProjectCardDTO dto;
    private Runnable onDataUpdated;

    public void setOnDataUpdated(Runnable r) {
        this.onDataUpdated = r;
    }
    public void setProgress(double progress) {
        // 👉 fix dữ liệu
        progress = Math.max(0, Math.min(progress, 1));
        Color startColor;
        Color endColor;
        Color glowColor;

// 👉 đổi màu theo tiến độ
        if (progress < 0.5) {
            startColor = Color.web("#F87171"); // đỏ
            endColor = Color.web("#DC2626");
            glowColor = Color.rgb(220, 38, 38, 0.6);
        } else if (progress < 0.8) {
            startColor = Color.web("#FACC15"); // vàng
            endColor = Color.web("#F59E0B");
            glowColor = Color.rgb(245, 158, 11, 0.6);
        } else {
            startColor = Color.web("#4ADE80"); // xanh
            endColor = Color.web("#166534");
            glowColor = Color.rgb(34, 197, 94, 0.6);
        }

// 👉 áp màu vào vòng tròn
        progressCircle.setStroke(
                new LinearGradient(
                        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, startColor),
                        new Stop(1, endColor)
                )
        );
        progressCircle.setRotate(-90 );

// 👉 glow theo màu
     //   progressCircle.setEffect(
     //           new javafx.scene.effect.DropShadow(15, glowColor)
     //   );
        progressCircle.setEffect(null);
        // 👉 case 0%
        if (progress <= 0) {
            if (timeline != null) timeline.stop();
            if (numberAnim != null) numberAnim.stop();

            progressCircle.setStrokeDashOffset(CIRCUMFERENCE);
            percentLabel.setText("0%");
            return;
        }


        double targetOffset = CIRCUMFERENCE * (1 - progress);

        double currentOffset = progressCircle.getStrokeDashOffset();
        if (currentOffset <= 0) {
            currentOffset = CIRCUMFERENCE;
        }

        if (timeline != null) timeline.stop();
        if (numberAnim != null) numberAnim.stop();

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressCircle.strokeDashOffsetProperty(), currentOffset)
                ),
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(progressCircle.strokeDashOffsetProperty(), targetOffset,
                                Interpolator.EASE_BOTH)
                )
        );

        numberAnim = new Timeline();
        int steps = 30;

        for (int i = 0; i <= steps; i++) {
            int percent = (int) (progress * 100 * i / steps);

            numberAnim.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1.2 * i / steps),
                            e -> percentLabel.setText(percent + "%"))
            );
        }

        timeline.play();
        numberAnim.play();
    }
    @FXML
    public void initialize() {
        progressCircle.getStrokeDashArray().clear();
        progressCircle.getStrokeDashArray().setAll(CIRCUMFERENCE, CIRCUMFERENCE);
        progressCircle.setStroke(
                new LinearGradient(
                        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#4ADE80")),
                        new Stop(1, Color.web("#166534"))
                )
        );

        progressCircle.setEffect(
                new javafx.scene.effect.DropShadow(
                        15,
                        Color.rgb(34, 197, 94, 0.6)
                )
        );
    }
    public void setProjectData(ProjectCardDTO dto) {
        if (dto == null || dto.getProject() == null) return;
        this.dto=dto;

        Platform.runLater(() -> {
            lblProjectName.setText(dto.getProject().getName());

            lblTodoCount.setText("...");
            lblInProgressCount.setText("...");
            lblDoneCount.setText("...");

            percentLabel.setText("...");
            progressCircle.setStrokeDashOffset(CIRCUMFERENCE);
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

        int total = todo + inProgress + done;
        double targetProgress = (total == 0) ? 0 : (double) done / total;

        // 👉 gọi custom progress
        setProgress(targetProgress);
        if (dto != null) {
            dto.setTodoCount(todo);
            dto.setInProgressCount(inProgress);
            dto.setDoneCount(done);
        }

        if (onDataUpdated != null) {
            onDataUpdated.run();
        }
    }

}