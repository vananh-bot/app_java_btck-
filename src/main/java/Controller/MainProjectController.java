package Controller;

import DAO.TaskDAO;
import Model.Task;
import Enum.TaskStatus;

// JavaFX
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Drag & Drop
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

// Animation + Realtime
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

// Time
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Collection
import java.util.List;
import java.util.ArrayList;

public class MainProjectController {

    // ================= UI =================
    @FXML private VBox vboxTodo;
    @FXML private VBox vboxInProgress;
    @FXML private VBox vboxDone;
    @FXML private TextField searchField;

    // ================= DATA =================
    private int projectId;
    private TaskDAO taskDAO = new TaskDAO();

    // ================= INIT =================
    public void init(int projectId) {
        this.projectId = projectId;

        setupDragDrop();
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                loadTasks(); // Gọi lại hàm loadTasks mỗi khi chữ thay đổi
            });
        }
        loadTasks();

        //  có thể tắt nếu lag
        startRealtime();
    }

    // ================= LOAD =================
    private void loadTasks() {

        List<Task> all = taskDAO.getTasksByProjectId(projectId);

        // Lấy từ khóa tìm kiếm (chuyển về chữ thường để tìm không phân biệt hoa thường)
        String keyword = searchField != null ? searchField.getText().toLowerCase().trim() : "";

        List<Task> todo = new ArrayList<>();
        List<Task> inProgress = new ArrayList<>();
        List<Task> done = new ArrayList<>();

        for (Task t : all) {
            // LỌC TÌM KIẾM: Nếu có từ khóa, và tiêu đề không chứa từ khóa đó -> Bỏ qua task này
            if (!keyword.isEmpty()) {
                boolean matchTitle = t.getTitle() != null && t.getTitle().toLowerCase().contains(keyword);
                boolean matchDesc = t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword);

                // Nếu cả tiêu đề và mô tả đều không khớp thì bỏ qua
                if (!matchTitle && !matchDesc) {
                    continue;
                }
            }

            // Phân loại task sau khi đã lọc
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
        card.setSpacing(12);
        card.getStyleClass().add("borderTask");
        card.setUserData(task);

        // 1. Label Mức độ ưu tiên (Priority)
        Label priorityLabel = new Label();
        priorityLabel.getStyleClass().add("levelPriority");

        if (task.getPriority() != null) {
            switch (task.getPriority().name()) {
                case "HIGH":
                    priorityLabel.setText("Cao");
                    priorityLabel.setStyle("-fx-text-fill: #d93025;");
                    break;
                case "MEDIUM":
                    priorityLabel.setText("Trung bình");
                    priorityLabel.setStyle("-fx-text-fill: #e37400;");
                    break;
                case "LOW":
                    priorityLabel.setText("Thấp");
                    priorityLabel.setStyle("-fx-text-fill: #188038;");
                    break;
                default:
                    priorityLabel.setText("Bình thường");
                    priorityLabel.setStyle("-fx-text-fill: #5f6368;");
                    break;
            }
        } else {
            priorityLabel.setVisible(false);
            priorityLabel.setManaged(false); // Xóa khoảng trống nếu không có priority
        }

        // 2. Khối chứa Tiêu đề và Mô tả
        VBox textVBox = new VBox(3);
        VBox.setMargin(textVBox, new Insets(0, -8, 0, -8));

        Label title = new Label(task.getTitle());
        title.getStyleClass().add("newLine");
        // Dùng Font chuẩn của JavaFX thay vì setStyle
        title.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label desc = new Label(task.getDescription());
        desc.getStyleClass().addAll("description", "newLine");
        // Dùng Font chuẩn của JavaFX
        desc.setFont(Font.font("System", 11));
        desc.setTextFill(Color.valueOf("#434343"));

        textVBox.getChildren().addAll(title, desc);

        // 3. Khối chứa Icon Lịch và Hạn chót (Deadline)
        HBox deadlineHBoxOuter = new HBox();
        deadlineHBoxOuter.getStyleClass().add("borderMini");
        deadlineHBoxOuter.setPadding(new Insets(4, 4, 4, 4));
        VBox.setMargin(deadlineHBoxOuter, new Insets(0, 0, -10, 0));

        HBox deadlineHBoxInner = new HBox(5);
        deadlineHBoxInner.setAlignment(Pos.CENTER);
        HBox.setMargin(deadlineHBoxInner, new Insets(4, 0, 3, 0));

        ImageView calendarIcon = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/calendar-249-removebg-preview.png"));
            calendarIcon.setImage(img);
            calendarIcon.setFitWidth(17);
            calendarIcon.setFitHeight(17);
        } catch (Exception e) {
            System.out.println("Cảnh báo: Không load được icon lịch - " + e.getMessage());
        }

        Label dateLabel = new Label(calculateDaysRemaining(task.getDeadline()));
        // Dùng Font chuẩn của JavaFX
        dateLabel.setFont(Font.font("System", 11));
        dateLabel.setTextFill(Color.valueOf("#434343"));

        deadlineHBoxInner.getChildren().addAll(calendarIcon, dateLabel);
        deadlineHBoxOuter.getChildren().add(deadlineHBoxInner);

        // Lắp ráp Card
        card.getChildren().addAll(priorityLabel, textVBox, deadlineHBoxOuter);

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
        // DOUBLE CLICK ĐỂ CHUYỂN SANG MÀN HÌNH CHI TIẾT
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // Thay đổi đường dẫn này nếu file của bạn nằm ở thư mục khác
                switchSceneMouse(e, "/task/taskdetails.fxml");
            }
        });

        return card;
    }

    // ================= DATE UTIL =================
    private String calculateDaysRemaining(LocalDateTime deadlineDateTime) {
        if (deadlineDateTime == null) return "Không có hạn";

        LocalDate deadlineDate = deadlineDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        long daysBetween = ChronoUnit.DAYS.between(today, deadlineDate);

        if (daysBetween == 0) return "Hôm nay";
        if (daysBetween > 0) return daysBetween + " ngày nữa";
        return "Quá hạn " + Math.abs(daysBetween) + " ngày";
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
    // ================= CHUYỂN SCENE BẰNG CHUỘT =================
    private void switchSceneMouse(javafx.scene.input.MouseEvent event, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Lỗi chuyển trang: Hãy kiểm tra lại đường dẫn FXML (" + path + ")");
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


}