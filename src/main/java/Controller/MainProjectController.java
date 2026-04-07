package Controller;

import DAO.TaskDAO;
import Model.Task;
import Enum.TaskStatus;

// JavaFX UI
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

// Drag & Drop
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

// Event
import javafx.scene.input.MouseEvent;

// Scene + Stage (popup)
import javafx.scene.Scene;
import javafx.stage.Stage;

// Animation
import javafx.animation.ScaleTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

// Collection
import java.util.List;
import java.util.ArrayList;
public class MainProjectController {
    @FXML private VBox vboxTodo;
    @FXML private VBox vboxInProgress;
    @FXML private VBox vboxDone;

    private int projectId;
    private TaskDAO taskDAO = new TaskDAO();

    public void init(int projectId) {
        this.projectId = projectId;

        setupDragDrop();   // drag
        loadTasks();       // load data
        startRealtime();   // realtime (optional)
    }

    private void loadTasks() {

        List<Task> all = taskDAO.getTasksByProjectId(projectId);

        List<Task> todo = new ArrayList<>();
        List<Task> inProgress = new ArrayList<>();
        List<Task> done = new ArrayList<>();

        for (Task t : all) {
            switch (t.getStatus()) {
                case TODO -> todo.add(t);
                case IN_PROGRESS -> inProgress.add(t);
                case DONE -> done.add(t);
            }
        }

        render(todo, inProgress, done);
    }

    private void render(List<Task> todo, List<Task> inProgress, List<Task> done) {

        vboxTodo.getChildren().clear();
        vboxInProgress.getChildren().clear();
        vboxDone.getChildren().clear();

        todo.forEach(t -> vboxTodo.getChildren().add(createTaskCard(t)));
        inProgress.forEach(t -> vboxInProgress.getChildren().add(createTaskCard(t)));
        done.forEach(t -> vboxDone.getChildren().add(createTaskCard(t)));
    }

    private VBox createTaskCard(Task task) {

        VBox card = new VBox();
        card.setSpacing(8);
        card.getStyleClass().add("borderTask");

        card.setUserData(task); // 🔥 lưu task

        Label title = new Label(task.getTitle());
        Label desc = new Label(task.getDescription());

        card.getChildren().addAll(title, desc);

        // ✨ DRAG
        card.setOnDragDetected(e -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("task");
            db.setContent(content);
            db.setDragView(card.snapshot(null, null));
        });

        // ✨ ANIMATION hover
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
        });

        // ✨ CLICK EDIT
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openEditPopup(task);
            }
        });

        return card;
    }

    private void setupDragDrop() {
        setupColumn(vboxTodo, TaskStatus.TODO);
        setupColumn(vboxInProgress, TaskStatus.IN_PROGRESS);
        setupColumn(vboxDone, TaskStatus.DONE);
    }

    private void setupColumn(VBox column, TaskStatus status) {

        column.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.MOVE);
        });

        column.setOnDragDropped(e -> {

            VBox card = (VBox) e.getGestureSource();
            VBox oldParent = (VBox) card.getParent();

            oldParent.getChildren().remove(card);
            column.getChildren().add(card); // 🔥 move UI

            Task task = (Task) card.getUserData();
            task.setStatus(status);

            // 🔥 update DB nền
            new Thread(() -> {
                taskDAO.updateStatus(task.getId(), status);
            }).start();

            e.setDropCompleted(true);
        });
    }

    private void openEditPopup(Task task) {

        TextField title = new TextField(task.getTitle());
        TextField desc = new TextField(task.getDescription());
        Button save = new Button("Lưu");

        VBox layout = new VBox(10, title, desc, save);

        Stage stage = new Stage();
        stage.setScene(new Scene(layout, 300, 200));
        stage.show();

        save.setOnAction(e -> {
            task.setTitle(title.getText());
            task.setDescription(desc.getText());

            taskDAO.update(task);

            stage.close();
            loadTasks(); // reload nhẹ
        });
    }

    private void startRealtime() {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> loadTasks())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
