package Controller;

import Model.Task;
import Service.helper.TaskUIHelper;
import Utils.ScreenManager;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import Enum.Screen;

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
        cardRoot.setUserData(task);

        // Deadline
        TaskUIHelper.applyDeadlineStyle(cardRoot, task);
        dateLabel.setText(TaskUIHelper.calculateDaysRemaining(task.getDeadline()));

        // Priority
        priorityLabel.getStyleClass().removeAll("priority-high", "priority-medium", "priority-low");
        cardRoot.getStyleClass().removeAll("card-high", "card-medium", "card-low");

        if (task.getPriority() != null) {
            priorityLabel.setVisible(true);
            priorityLabel.setManaged(true);

            switch (task.getPriority().name()) {
                case "HIGH":
                    priorityLabel.setText("Cao");
                    priorityLabel.getStyleClass().add("priority-high");
                    cardRoot.getStyleClass().add("card-high");
                    break;
                case "MEDIUM":
                    priorityLabel.setText("Trung bình");
                    priorityLabel.getStyleClass().add("priority-medium");
                    cardRoot.getStyleClass().add("card-medium");
                    break;
                case "LOW":
                    priorityLabel.setText("Thấp");
                    priorityLabel.getStyleClass().add("priority-low");
                    cardRoot.getStyleClass().add("card-low");
                    break;
                default:
                    priorityLabel.setText("Bình thường");
            }
        } else {
            priorityLabel.setVisible(false);
            priorityLabel.setManaged(false);
        }

        // Title highlight
        titleBox.getChildren().setAll(
                TaskUIHelper.highlightText(task.getTitle(), searchKey, false).getChildren()
        );

        //  FIX CHÍNH: đợi UI có width rồi mới clamp
        Platform.runLater(() -> {
            double width = descBox.getLayoutBounds().getWidth();
            if (width <= 0) width = descBox.getPrefWidth();

            String shortDesc = clampText(
                    task.getDescription(),
                    3,
                    width
            );

            descBox.getChildren().setAll(
                    TaskUIHelper.highlightText(shortDesc, searchKey, true).getChildren()
            );
        });
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

        cardRoot.setOnDragDone(e -> cardRoot.setOpacity(1.0));
    }

    private void setupDoubleClick() {
        cardRoot.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                switchSceneMouse(e);
            }
        });
    }

    private void switchSceneMouse(MouseEvent event) {
        ScreenManager.getInstance().show(Screen.TASK_DETAILS, currentTask.getId());
    }

    // 🔥 HÀM CLAMP CHUẨN
    private String clampText(String content, int maxLines, double width) {
        if (content == null) return "";

        Text text = new Text();
        text.setWrappingWidth(width);

        StringBuilder result = new StringBuilder();
        String[] words = content.split(" ");

        for (String word : words) {
            String test = result + (result.length() == 0 ? "" : " ") + word;
            text.setText(test);

            double height = text.getLayoutBounds().getHeight();
            double lineHeight = text.getFont().getSize() * 1.5;

            int lines = (int) Math.round(height / lineHeight);

            if (lines > maxLines) {
                return result.toString().trim() + "...";
            }

            if (result.length() > 0) result.append(" ");
            result.append(word);
        }

        return result.toString();
    }
}