package Controller;

import DAO.TaskDAO;
import Model.Task;
import Enum.TaskStatus;

// JavaFX
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

// Drag & Drop
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

// Animation + Realtime
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

// Collection
import java.util.List;
import java.util.ArrayList;

public class MainProjectController {

    // ================= UI =================
    @FXML private VBox vboxTodo;
    @FXML private VBox vboxInProgress;
    @FXML private VBox vboxDone;

    // ================= DATA =================
    private int projectId;
    private TaskDAO taskDAO = new TaskDAO();

    // ================= INIT =================
    public void init(int projectId) {
        this.projectId = projectId;

        setupDragDrop();
        loadTasks();

        //  có thể tắt nếu lag
        startRealtime();
    }

    // ================= LOAD =================
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

    // ================= RENDER =================
    private void render(List<Task> todo, List<Task> inProgress, List<Task> done) {

        vboxTodo.getChildren().clear();
        vboxInProgress.getChildren().clear();
        vboxDone.getChildren().clear();

        todo.forEach(t -> vboxTodo.getChildren().add(createTaskCard(t)));
        inProgress.forEach(t -> vboxInProgress.getChildren().add(createTaskCard(t)));
        done.forEach(t -> vboxDone.getChildren().add(createTaskCard(t)));
    }

    // ================= TASK CARD =================
    private VBox createTaskCard(Task task) {

        VBox card = new VBox();
        card.setSpacing(8);
        card.getStyleClass().add("borderTask");

        card.setUserData(task);

        Label title = new Label(task.getTitle());
        Label desc = new Label(task.getDescription());

        card.getChildren().addAll(title, desc);

        //  DRAG
        card.setOnDragDetected(e -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            db.setDragView(card.snapshot(null, null));
            e.consume();
        });

        //  HOVER ANIMATION
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
        });

        //  DOUBLE CLICK EDIT
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openEditPopup(task);
            }
        });

        return card;
    }

    // ================= DRAG DROP =================
    private void setupDragDrop() {
        setupColumn(vboxTodo, TaskStatus.TODO);
        setupColumn(vboxInProgress, TaskStatus.IN_PROGRESS);
        setupColumn(vboxDone, TaskStatus.DONE);
    }

    private void setupColumn(VBox column, TaskStatus status) {

        column.setOnDragOver(e -> {
            if (e.getGestureSource() != column) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        column.setOnDragEntered(e ->
                column.setStyle("-fx-background-color: #f5f5f5;")
        );

        column.setOnDragExited(e ->
                column.setStyle("")
        );

        column.setOnDragDropped(e -> {

            VBox card = (VBox) e.getGestureSource();
            VBox oldParent = (VBox) card.getParent();

            oldParent.getChildren().remove(card);
            column.getChildren().add(card);

            Task task = (Task) card.getUserData();
            task.setStatus(status);

            //  update DB nền (không lag UI)
            new Thread(() -> taskDAO.updateStatus(task.getId(), status)).start();

            e.setDropCompleted(true);
            e.consume();
        });
    }

    // ================= EDIT POPUP =================
    private void openEditPopup(Task task) {

        TextField title = new TextField(task.getTitle());
        TextField desc = new TextField(task.getDescription());
        Button save = new Button("Lưu");

        VBox layout = new VBox(10, title, desc, save);

        Stage stage = new Stage();
        stage.setTitle("Sửa công việc");
        stage.setScene(new Scene(layout, 300, 200));
        stage.show();

        save.setOnAction(e -> {
            task.setTitle(title.getText());
            task.setDescription(desc.getText());

            taskDAO.update(task);

            stage.close();
            loadTasks();
        });
    }

    // ================= REALTIME =================
    private void startRealtime() {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> loadTasks())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ================= CREATE TASK =================
    @FXML
    private void handleOpenCreateTask() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/task/createTask.fxml")
            );

            Parent root = loader.load();

            CreateTaskController controller = loader.getController();
            controller.setProjectId(projectId);

            controller.setOnTaskCreated(this::loadTasks);

            Stage stage = new Stage();
            stage.setTitle("Tạo công việc");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void switchScene(ActionEvent event, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================

    //  Tổng quan → Dashboard
    public void handleDashboard(ActionEvent event) {
        switchScene(event, "/dashboard/dashboard.fxml");
    }

    //  Dự án của tôi
    public void handleMyProjects(ActionEvent event) {
        switchScene(event, "/project/project.fxml");
    }

    //  Thông báo
    public void handleNotification(ActionEvent event) {
        switchScene(event, "/notification/notification.fxml");
    }

    //  Đăng xuất → Login
    public void handleLogout(ActionEvent event) {
        switchScene(event, "/auth/login.fxml");
    }

    //  Thêm công việc (popup riêng)
//    public void handleOpenCreateTask(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/createTask.fxml"));
//            Scene scene = new Scene(loader.load());
//
//            Stage stage = new Stage();
//            stage.setTitle("Create Task");
//            stage.setScene(scene);
//            stage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}