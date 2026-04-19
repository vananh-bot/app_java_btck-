package Controller;

import Enum.Priority;
import Enum.TaskStatus;
import Model.Comment;
import Model.SubTask;
import Model.Task;
import Service.TaskService;
import Utils.TimeUtil;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;


public class TaskController {

    private final TaskService taskService = new TaskService();
    private int currentTaskId;
    private int currentUserId = 38;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ================= INIT =================
    @FXML
    public void initialize() {

        comboStatus.getItems().setAll("TODO", "IN_PROGRESS", "DONE");
        comboPriority.getItems().setAll("LOW", "MEDIUM", "HIGH");

        comboStatus.setOnAction(e -> updateStatus());
        comboPriority.setOnAction(e -> updatePriority());
        comboDeadline.setOnAction(e -> updateDeadline());

        addComment.setOnAction(e -> handleAddComment());

        titleMini.setFont(Font.loadFont(
                getClass().getResourceAsStream("/Font/Inter_18pt-SemiBold.ttf"),
                13
        ));
        titleMini.setTextFill(Color.web("#0d0d0d"));


        titleMini1.setFont(Font.loadFont(
                getClass().getResourceAsStream("/Font/Inter_18pt-ExtraBold.ttf"),
                12
        ));
        titleMini1.setTextFill(Color.BLACK);


        taskName.setFont(Font.loadFont(
                getClass().getResourceAsStream("/Font/Inter_18pt-Bold.ttf"),
                28
        ));
        taskName.setTextFill(Color.BLACK);

        description.setFont(Font.loadFont(
                getClass().getResourceAsStream("/Font/Inter_18pt-Medium.ttf"),
                11.5
        ));

        loadTask(1);
    }

    private int taskId;

    public void setTaskId(int taskId){
        this.taskId = taskId;
    }

    // ================= LOAD TASK =================
    public void loadTask(int taskId) {
        this.currentTaskId = taskId;
    }


    public void createTask() {

        Task task = taskService.getTaskById(taskId);
        if (task == null) return;

        taskName.setText(task.getTitle());
        description.setText(task.getDescription());

        comboStatus.setValue(task.getStatus().name());
        comboPriority.setValue(task.getPriority().name());

        if (task.getDeadline() != null) {
            comboDeadline.setValue(task.getDeadline().toLocalDate());
            deadline.setText(task.getDeadline().format(formatter));
        }

        createTime.setText(TimeUtil.toRelative(task.getCreatedAt()));
        updateTime.setText(TimeUtil.toRelative(task.getUpdatedAt()));

        loadSubTasks();
        loadComments();
    }

    // ================= TASK UPDATE =================
    private Task getTask() {
        return taskService.getTaskById(currentTaskId);
    }

    private void updateStatus() {
        Task task = getTask();
        if (task == null || comboStatus.getValue() == null) return;

        task.setStatus(TaskStatus.valueOf(comboStatus.getValue()));
        taskService.updateTask(task);
    }

    private void updatePriority() {
        Task task = getTask();
        if (task == null || comboPriority.getValue() == null) return;

        task.setPriority(Priority.valueOf(comboPriority.getValue()));
        taskService.updateTask(task);
    }

    private void updateDeadline() {
        Task task = getTask();
        if (task == null || comboDeadline.getValue() == null) return;

        task.setDeadline(comboDeadline.getValue().atStartOfDay());
        taskService.updateTask(task);

        deadline.setText(task.getDeadline().format(formatter));
    }

    // ================= SUBTASK =================
    private void loadSubTasks() {
        checkList.getChildren().clear();

        List<SubTask> list = taskService.getSubTasks(currentTaskId);

        for (SubTask s : list) {
            checkList.getChildren().add(createSubTaskRow(s));
        }

        updateProgress();
    }
    @FXML
    private void handleAddSubTask() {

        TextField tf = new TextField();
        tf.setPromptText("Nhập công việc...");

        HBox row = new HBox(10, new CheckBox(), tf);
        row.setAlignment(Pos.CENTER_LEFT);

        checkList.getChildren().add(row);
        tf.requestFocus();

        tf.setOnAction(e -> {
            String text = tf.getText().trim();
            if (text.isEmpty()) return;

            SubTask s = new SubTask();
            s.setTaskId(currentTaskId);
            s.setTitle(text);
            s.setCompleted(false);

            taskService.addSubTask(s);
            loadSubTasks();
        });
    }

    private HBox createSubTaskRow(SubTask s) {

        CheckBox cb = new CheckBox();
        cb.setSelected(s.isCompleted());

        Label title = new Label(s.getTitle());
        title.setWrapText(true);

        if (s.isCompleted()) {
            title.setStyle("-fx-strikethrough: true; -fx-text-fill: #999;");
        }

        Button deleteBtn = new Button();
        ImageView imageView = new ImageView(
                new Image(getClass().getResource("/images/images__2_-removebg-preview.png").toExternalForm())
        );
        imageView.setFitWidth(13);
        imageView.setFitHeight(13);
        deleteBtn.setGraphic(imageView);
        HBox.setMargin(deleteBtn, new Insets(-4, 0, -0 ,2));

        deleteBtn.setStyle("-fx-background-color: transparent;\n" +
                "    -fx-border-color: transparent;");
        deleteBtn.setOpacity(0);


        HBox contentBox = new HBox(6, cb, title);
        HBox.setHgrow(title, javafx.scene.layout.Priority.ALWAYS);

        HBox row = new HBox(6, contentBox, deleteBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        row.setUserData(cb); // ⭐ FIX QUAN TRỌNG

        row.setOnMouseEntered(e -> deleteBtn.setOpacity(1));
        row.setOnMouseExited(e -> deleteBtn.setOpacity(0));

        int id = s.getId();

        cb.setOnAction(e -> {
            boolean done = cb.isSelected();
            taskService.toggleSubTask(id, done);

            title.setStyle(done
                    ? "-fx-strikethrough: true; -fx-text-fill: #999;"
                    : "");

            updateProgress();
        });

        deleteBtn.setOnAction(e -> {
            taskService.deleteSubTask(id);
            checkList.getChildren().remove(row);
            updateProgress();
        });

        return row;
    }

    private void updateProgress() {

        int total = 0;
        int done = 0;

        for (Node node : checkList.getChildren()) {

            if (!(node instanceof HBox row)) continue;

            CheckBox cb = (CheckBox) row.getUserData();

            total++;
            if (cb.isSelected()) done++;
        }

        double progressValue = total == 0 ? 0 : (double) done / total;

        animateProgress(progressValue);
        progressText.setText(done + " / " + total + " completed");
    }

    private void animateProgress(double value) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(progress.progressProperty(), value))
        );
        timeline.play();
    }

    // ================= COMMENTS =================
    private void loadComments() {

        comment.getChildren().clear();

        List<Comment> list = taskService.getComments(currentTaskId);

        if (list == null || list.isEmpty()) {
            comment.getChildren().add(new Label("Chưa có bình luận"));
            return;
        }

        for (Comment c : list) {
            comment.getChildren().add(createCommentItem(c));
        }
    }

    private HBox createCommentItem(Comment c) {

        Label avatar = new Label(c.getUserName().substring(0, 1).toUpperCase());
        avatar.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center; -fx-min-width: 28; -fx-min-height: 28; -fx-background-radius: 50;");

        Label name = new Label(c.getUserName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label time = new Label(TimeUtil.toRelative(c.getCreatedAt()));
        time.setStyle("-fx-text-fill: #888; -fx-font-size: 10px;");

        HBox header = new HBox(5, name, time);

        Label content = new Label(c.getContent());
        content.setWrapText(true);
        content.setMaxWidth(Double.MAX_VALUE); // ⭐ QUAN TRỌNG
        content.setStyle("-fx-font-size: 12px;");

        VBox box = new VBox(3, header, content);
        HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);
        HBox container = new HBox(10, avatar, box); container.setAlignment(Pos.TOP_LEFT);

        return container;
    }

    @FXML
    private void handleAddComment() {

        String text = addComment.getText().trim();
        if (text.isEmpty()) return;

        Comment c = new Comment();
        c.setTaskId(currentTaskId);
        c.setUserId(currentUserId);
        c.setContent(text);

        boolean ok = taskService.addComment(c);

        System.out.println("Insert = " + ok);

        if (ok) {
            addComment.clear();
            loadComments();
        }
    }


    // ================= FXML =================
    @FXML private TextField addComment;
    @FXML private VBox checkList;
    @FXML private VBox comment;

    @FXML private DatePicker comboDeadline;
    @FXML private ComboBox<String> comboPriority;
    @FXML private ComboBox<String> comboStatus;

    @FXML private TextArea description;
    @FXML private ProgressBar progress;
    @FXML private Label progressText;

    @FXML private Label taskName;
    @FXML private Label deadline;
    @FXML private Label createTime;
    @FXML private Label updateTime;

    private Label titleMini;

    @FXML
    private Label titleMini1;

    @FXML
    private Label titleMini10;

    @FXML
    private Label titleMini2;

    @FXML
    private Label titleMini3;

    @FXML
    private Label titleMini4;

    @FXML
    private Label titleMini7;

    @FXML
    private Label titleMini8;

    @FXML
    private Label titleMini9;

    public void convert_dashboard(ActionEvent event) {
    }

    public void convert_mainProject(ActionEvent event) {
    }

    public void convert_taskDetail(ActionEvent event) {
    }

    public void convert_notification(ActionEvent event) {
    }
}