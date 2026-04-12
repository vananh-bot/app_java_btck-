package Controller;

import DAO.TaskDAO;
import Model.Task;
import Enum.TaskStatus;

// JavaFX
import javafx.application.Platform;
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
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
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
import javafx.animation.FadeTransition;

// Time
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Collection
import java.util.List;
import java.util.ArrayList;
import java.text.Normalizer;

public class MainProjectController {

    // ================= UI =================
    @FXML private VBox vboxTodo;
    @FXML private VBox vboxInProgress;
    @FXML private VBox vboxDone;
    @FXML private TextField searchField;

    // ================= DATA =================
    private int projectId;
    private TaskDAO taskDAO = new TaskDAO();
    //==========
    private Timeline searchDelay;
    private List<Task> cachedTasks = new ArrayList<>();

    // lưu UI hiện tại để diff
    private List<Task> currentTodo = new ArrayList<>();
    private List<Task> currentInProgress = new ArrayList<>();
    private List<Task> currentDone = new ArrayList<>();

    // ================= INIT =================
    public void init(int projectId) {
        this.projectId = projectId;

        if (vboxTodo != null) vboxTodo.setCache(true);
        if (vboxInProgress != null) vboxInProgress.setCache(true);
        if (vboxDone != null) vboxDone.setCache(true);

        setupDragDrop();
        if (searchField != null) {
            searchDelay = new Timeline(
                    new KeyFrame(Duration.millis(300), e -> loadTasks())
            );
            searchDelay.setCycleCount(1);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                searchDelay.stop();
                searchDelay.play();
            });
        }
        cachedTasks.clear();
        loadTasks();

        startRealtime();
    }

    // ================= LOAD =================
    private void loadTasks() {

        if (cachedTasks.isEmpty()) {
            cachedTasks = taskDAO.getTasksByProjectId(projectId);
        }

        String key = normalize(searchField.getText());
        List<TaskScore> matched = new ArrayList<>();

        for (Task t : cachedTasks) {
            String title = normalize(t.getTitle());
            String desc = normalize(t.getDescription());

            int score = matchScore(title, key) * 2 + matchScore(desc, key);

            if (!key.isEmpty() && score == 0) continue;

            matched.add(new TaskScore(t, score));
        }

        // 🔥 SORT ỔN ĐỊNH
        matched.sort((a, b) -> {
            if (!key.isEmpty()) {
                int s = Integer.compare(b.score, a.score);
                if (s != 0) return s;
            }

            int p = Integer.compare(getPriorityOrder(a.task), getPriorityOrder(b.task));
            if (p != 0) return p;

            if (a.task.getDeadline() == null && b.task.getDeadline() == null) {
                return Integer.compare(a.task.getId(), b.task.getId());
            }
            if (a.task.getDeadline() == null) return 1;
            if (b.task.getDeadline() == null) return -1;

            int dateCompare = a.task.getDeadline().compareTo(b.task.getDeadline());
            if (dateCompare != 0) return dateCompare;

            return Integer.compare(a.task.getId(), b.task.getId());
        });

        List<Task> todo = new ArrayList<>();
        List<Task> inProgress = new ArrayList<>();
        List<Task> done = new ArrayList<>();

        for (TaskScore ts : matched) {
            Task t = ts.task;
            switch (t.getStatus()) {
                case TODO -> todo.add(t);
                case IN_PROGRESS -> inProgress.add(t);
                case DONE -> done.add(t);
            }
        }

        render(todo, inProgress, done);
    }

    private static class TaskScore {
        Task task;
        int score;

        TaskScore(Task t, int s) {
            this.task = t;
            this.score = s;
        }
    }

    private boolean fuzzyMatch(String text, String keyword) {
        if (keyword.isEmpty()) return true;
        int t = 0, k = 0;
        while (t < text.length() && k < keyword.length()) {
            if (text.charAt(t) == keyword.charAt(k)) {
                k++;
            }
            t++;
        }
        return k == keyword.length();
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replaceAll("[^a-zA-Z0-9 ]", "");
        return normalized.toLowerCase().trim();
    }

    private int matchScore(String text, String keyword) {
        if (keyword.isEmpty()) return 0;
        int score = 0;
        String[] words = keyword.split("\\s+");

        for (String w : words) {
            if (text.contains(w)) {
                score += 20;
            }
            else if (fuzzyMatch(text, w)) {
                score += 10;
            }
        }
        if (text.startsWith(keyword)) {
            score += 30;
        }
        return score;
    }

    private void applyDeadlineStyle(VBox card, Task task) {
        if (task.getDeadline() == null) return;

        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                task.getDeadline().toLocalDate()
        );

        card.getStyleClass().removeAll("deadline-overdue", "deadline-soon");

        if (days < 0) {
            card.getStyleClass().add("deadline-overdue");
        } else if (days <= 1) {
            card.getStyleClass().add("deadline-soon");
        }
    }

    // ================= RENDER =================
    private void render(List<Task> todo, List<Task> inProgress, List<Task> done) {
        updateColumn(vboxTodo, currentTodo, todo);
        updateColumn(vboxInProgress, currentInProgress, inProgress);
        updateColumn(vboxDone, currentDone, done);

        currentTodo = new ArrayList<>(todo);
        currentInProgress = new ArrayList<>(inProgress);
        currentDone = new ArrayList<>(done);
    }

    private void updateColumn(VBox column, List<Task> oldList, List<Task> newList) {
        for (int i = 0; i < newList.size(); i++) {
            if (i < column.getChildren().size()) {
                VBox card = (VBox) column.getChildren().get(i);
                Task newTask = newList.get(i);

                updateTaskCardUI(card, newTask, searchField.getText());

                // Đảm bảo thẻ luôn sáng rõ
                card.setOpacity(1.0);
                card.setScaleX(1.0);
                card.setScaleY(1.0);

            } else {
                VBox card = createTaskCard(newList.get(i));
                card.setOpacity(0);
                column.getChildren().add(card);

                FadeTransition fade = new FadeTransition(Duration.millis(150), card);
                fade.setToValue(1);
                fade.play();
            }
        }

        if (column.getChildren().size() > newList.size()) {
            column.getChildren().remove(newList.size(), column.getChildren().size());
        }
    }

    private void updateTaskCardUI(VBox card, Task task, String searchKey) {
        card.setUserData(task);
        applyDeadlineStyle(card, task);

        Label priorityLabel = (Label) card.getChildren().get(0);
        priorityLabel.getStyleClass().removeAll("priority-high", "priority-medium", "priority-low");

        if (task.getPriority() != null) {
            priorityLabel.setVisible(true);
            priorityLabel.setManaged(true);
            switch (task.getPriority().name()) {
                case "HIGH":
                    priorityLabel.setText("Cao");
                    priorityLabel.getStyleClass().add("priority-high");
                    break;
                case "MEDIUM":
                    priorityLabel.setText("Trung bình");
                    priorityLabel.getStyleClass().add("priority-medium");
                    break;
                case "LOW":
                    priorityLabel.setText("Thấp");
                    priorityLabel.getStyleClass().add("priority-low");
                    break;
                default:
                    priorityLabel.setText("Bình thường");
                    break;
            }
        } else {
            priorityLabel.setVisible(false);
            priorityLabel.setManaged(false);
        }

        VBox textVBox = (VBox) card.getChildren().get(1);
        textVBox.getChildren().set(0, highlightText(task.getTitle(), searchKey, false));
        textVBox.getChildren().set(1, highlightText(task.getDescription(), searchKey, true));

        HBox deadlineHBoxOuter = (HBox) card.getChildren().get(2);
        HBox deadlineHBoxInner = (HBox) deadlineHBoxOuter.getChildren().get(0);
        Label dateLabel = (Label) deadlineHBoxInner.getChildren().get(1);
        dateLabel.setText(calculateDaysRemaining(task.getDeadline()));
    }

    // ================= TASK CARD =================
    private VBox createTaskCard(Task task) {
        VBox card = new VBox();
        card.setSpacing(12);
        card.getStyleClass().add("borderTask");
        card.setUserData(task);
        applyDeadlineStyle(card, task);

        Label priorityLabel = new Label();
        priorityLabel.getStyleClass().add("levelPriority");

        if (task.getPriority() != null) {
            switch (task.getPriority().name()) {
                case "HIGH":
                    priorityLabel.setText("Cao");
                    priorityLabel.getStyleClass().add("priority-high");
                    break;
                case "MEDIUM":
                    priorityLabel.setText("Trung bình");
                    priorityLabel.getStyleClass().add("priority-medium");
                    break;
                case "LOW":
                    priorityLabel.setText("Thấp");
                    priorityLabel.getStyleClass().add("priority-low");
                    break;
                default:
                    priorityLabel.setText("Bình thường");
                    break;
            }
        } else {
            priorityLabel.setVisible(false);
            priorityLabel.setManaged(false);
        }

        VBox textVBox = new VBox(3);
        VBox.setMargin(textVBox, new Insets(0, -8, 0, -8));

        TextFlow titleBox = highlightText(task.getTitle(), searchField.getText(), false);
        TextFlow desc = highlightText(task.getDescription(), searchField.getText(), true);

        textVBox.getChildren().addAll(titleBox, desc);

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
            System.out.println("Cảnh báo: Không load được icon lịch");
        }

        Label dateLabel = new Label(calculateDaysRemaining(task.getDeadline()));
        dateLabel.getStyleClass().add("description");

        deadlineHBoxInner.getChildren().addAll(calendarIcon, dateLabel);
        deadlineHBoxOuter.getChildren().add(deadlineHBoxInner);

        card.getChildren().addAll(priorityLabel, textVBox, deadlineHBoxOuter);

        card.setOnDragDetected(e -> {
            card.setCache(true);
            card.setCacheHint(javafx.scene.CacheHint.SPEED);
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);

            db.setDragView(card.snapshot(null, null), 50, 20);

            card.setOpacity(0.5);
            e.consume();
        });

        // Đảm bảo nhả chuột ra là thẻ sáng lại
        card.setOnDragDone(e -> {
            card.setOpacity(1.0);
        });

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                switchSceneMouse(e, "/task/taskdetails.fxml");
            }
        });

        return card;
    }

    private String calculateDaysRemaining(LocalDateTime deadlineDateTime) {
        if (deadlineDateTime == null) return "Không có hạn";

        LocalDate deadlineDate = deadlineDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        long daysBetween = ChronoUnit.DAYS.between(today, deadlineDate);

        if (daysBetween == 0) return "Hôm nay";
        if (daysBetween > 0) return daysBetween + " ngày nữa";
        return "Quá hạn " + Math.abs(daysBetween) + " ngày";
    }

    // ================= DRAG DROP (SẠCH SẼ - KHÔNG LỖI DUPLICATE) =================
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

        column.setOnDragEntered(e -> column.getStyleClass().add("column-drag-over"));
        column.setOnDragExited(e -> column.getStyleClass().remove("column-drag-over"));

        column.setOnDragDropped(e -> {
            VBox card = (VBox) e.getGestureSource();
            Task task = (Task) card.getUserData();

            // 🔥 BẮT BUỘC: Nếu kéo thả vào CÙNG 1 cột thì bỏ qua (tránh lỗi duplicate và lag)
            if (task.getStatus() == status) {
                e.setDropCompleted(false);
                e.consume();
                return;
            }

            // 1. Đổi trạng thái trong bộ nhớ RAM
            task.setStatus(status);

            // 2. Yêu cầu UI tự động sắp xếp lại cực mượt từ RAM
            Platform.runLater(this::loadTasks);

            // 3. Cập nhật DB ngầm không làm đơ màn hình
            new Thread(() -> {
                taskDAO.updateStatus(task.getId(), status);
            }).start();

            e.setDropCompleted(true);
            e.consume();
        });
    }

    // ================= REALTIME =================
    private void startRealtime() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    new Thread(() -> {
                        List<Task> freshTasks = taskDAO.getTasksByProjectId(projectId);

                        if (freshTasks.size() != cachedTasks.size()) {
                            Platform.runLater(() -> {
                                cachedTasks = freshTasks;
                                loadTasks();
                            });
                        }
                    }).start();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ================= CREATE TASK =================
    @FXML
    private void handleOpenCreateTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/createTask.fxml"));
            Parent root = loader.load();
            CreateTaskController controller = loader.getController();
            controller.setProjectId(projectId);

            controller.setOnTaskCreated(() -> {
                cachedTasks.clear();
                loadTasks();
            });

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

    private void switchSceneMouse(javafx.scene.input.MouseEvent event, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPriorityOrder(Task t) {
        if (t.getPriority() == null) return 4;
        return switch (t.getPriority()) {
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    // ================= TEXTFLOW HIGHLIGHT NHIỀU TỪ =================
    private TextFlow highlightText(String originalText, String keyword, boolean isDesc) {
        TextFlow textFlow = new TextFlow();
        if (originalText == null) originalText = "";

        String normKeyword = normalize(keyword);
        String[] words = normKeyword.split("\\s+");
        List<String> searchWords = new ArrayList<>();
        for (String w : words) {
            if (!w.isEmpty()) searchWords.add(w);
        }

        if (searchWords.isEmpty()) {
            Text t = new Text(originalText);
            t.getStyleClass().add(isDesc ? "description" : "task-title");
            textFlow.getChildren().add(t);
            return textFlow;
        }

        String normText = normalize(originalText);
        int currentIndex = 0;

        while (currentIndex < originalText.length()) {
            int bestMatchIndex = -1;
            String bestMatchWord = "";

            for (String word : searchWords) {
                int index = normText.indexOf(word, currentIndex);
                if (index != -1) {
                    if (bestMatchIndex == -1 || index < bestMatchIndex) {
                        bestMatchIndex = index;
                        bestMatchWord = word;
                    }
                }
            }

            if (bestMatchIndex != -1) {
                if (bestMatchIndex > currentIndex) {
                    Text tNormal = new Text(originalText.substring(currentIndex, bestMatchIndex));
                    tNormal.getStyleClass().add(isDesc ? "description" : "task-title");
                    textFlow.getChildren().add(tNormal);
                }

                Text tHighlight = new Text(originalText.substring(bestMatchIndex, bestMatchIndex + bestMatchWord.length()));
                tHighlight.getStyleClass().add(isDesc ? "description" : "task-title");
                tHighlight.getStyleClass().add("highlight");
                textFlow.getChildren().add(tHighlight);

                currentIndex = bestMatchIndex + bestMatchWord.length();
            } else {
                Text tRest = new Text(originalText.substring(currentIndex));
                tRest.getStyleClass().add(isDesc ? "description" : "task-title");
                textFlow.getChildren().add(tRest);
                break;
            }
        }
        return textFlow;
    }
    // --- CẬP NHẬT SIDEBAR DÙNG NAVIGATOR CHUNG ---
    public void handleDashboard(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.DASHBOARD, "Tổng quan");
    }

    public void handleMyProjects(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.ALL_PROJECTS, "Dự án của tôi");
    }

    public void handleNotification(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.NOTIFICATION, "Thông báo");
    }

    public void handleLogout(ActionEvent event) {
        Utils.UserSession.logout();
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.LOGIN, "Đăng nhập");
    }
}