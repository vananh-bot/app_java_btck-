package Controller;

import Model.Task;
import Service.helper.TaskUIHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TaskCardController {

    @FXML private VBox cardRoot;
    @FXML private Label priorityLabel;
    @FXML private TextFlow titleBox;
    @FXML private TextFlow descBox;
    @FXML private HBox deadlineHBoxOuter;
    @FXML private Label dateLabel;

    private Task currentTask;

    @FXML
    public void initialize() {
        setupDragAndDropSource();
        setupDoubleClick();
    }

    public void updateUI(Task task, String searchKey) {
        this.currentTask = task;
        cardRoot.setUserData(task); // Quan trọng cho logic Drop ở Column

        // 1. Deadline Style
        TaskUIHelper.applyDeadlineStyle(cardRoot, task);
        dateLabel.setText(TaskUIHelper.calculateDaysRemaining(task.getDeadline()));

        // 2. Priority Label
        priorityLabel.getStyleClass().removeAll("priority-high", "priority-medium", "priority-low");
        cardRoot.getStyleClass().removeAll("card-high", "card-medium", "card-low"); // <--- THÊM DÒNG NÀY

        // XỬ LÝ MÀU SẮC THEO PRIORITY
        if (task.getPriority() != null) {
            priorityLabel.setVisible(true);
            priorityLabel.setManaged(true);

            switch (task.getPriority().name()) {
                case "HIGH":
                    priorityLabel.setText("Cao");
                    priorityLabel.getStyleClass().add("priority-high");
                    cardRoot.getStyleClass().add("card-high"); // <--- THÊM DÒNG NÀY (Bật viền đỏ)
                    break;
                case "MEDIUM":
                    priorityLabel.setText("Trung bình");
                    priorityLabel.getStyleClass().add("priority-medium");
                    cardRoot.getStyleClass().add("card-medium"); // <--- THÊM DÒNG NÀY (Bật viền cam)
                    break;
                case "LOW":
                    priorityLabel.setText("Thấp");
                    priorityLabel.getStyleClass().add("priority-low");
                    cardRoot.getStyleClass().add("card-low"); // <--- THÊM DÒNG NÀY (Bật viền xanh)
                    break;
                default:
                    priorityLabel.setText("Bình thường");
                    break;
            }
        } else {
            priorityLabel.setVisible(false);
            priorityLabel.setManaged(false);
        }

        // 3. Highlight TextFlow
        titleBox.getChildren().setAll(TaskUIHelper.highlightText(task.getTitle(), searchKey, false).getChildren());
        descBox.getChildren().setAll(TaskUIHelper.highlightText(task.getDescription(), searchKey, true).getChildren());
    }

    private void setupDragAndDropSource() {
        cardRoot.setOnDragDetected(e -> {
            cardRoot.setCache(true);
            cardRoot.setCacheHint(javafx.scene.CacheHint.SPEED);
            Dragboard db = cardRoot.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(currentTask.getId()));
            db.setContent(content);

            db.setDragView(cardRoot.snapshot(null, null), 50, 20);
            cardRoot.setOpacity(0.5);
            e.consume();
        });

        cardRoot.setOnDragDone(e -> {
            cardRoot.setOpacity(1.0);
        });
    }

    private void setupDoubleClick() {
        cardRoot.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                switchSceneMouse(e, "/task/taskdetails.fxml");
            }
        });
    }

    private void switchSceneMouse(MouseEvent event, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox getCardRoot() {
        return cardRoot;
    }
}