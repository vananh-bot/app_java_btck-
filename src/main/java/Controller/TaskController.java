package Controller;

import Enum.Priority;
import Enum.TaskStatus;
import Model.Comment;
import Model.SubTask;
import Model.Task;
import Service.TaskService1;
import Utils.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.image.Image;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import Enum.Screen;


public class TaskController implements DataReceiver<Integer> {

    private final TaskService1 taskService = new TaskService1();
    private int currentTaskId;
    private int currentUserId = UserSession.getUserId();
    private Task currentTask;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initData(Integer taskId){
        this.currentTaskId = taskId;
        currentTask = taskService.getTaskById(taskId);
        loadTask(currentTaskId);
    }

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
                getClass().getResourceAsStream("/fonts/Inter_18pt-Bold.ttf"),
                13
        ));
        titleMini.setTextFill(Color.web("#0d0d0d"));


        titleMini1.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/Inter_18pt-Bold.ttf"), 12));
        titleMini1.setTextFill(Color.BLACK);


        taskName.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/Inter_18pt-Bold.ttf"), 28));
        taskName.setTextFill(Color.BLACK);

        description.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/Inter_18pt-Medium.ttf"), 11.5));

    }

    // ================= LOAD TASK =================

    public void loadTask(int taskId) {

        if (currentTask == null) return;

        taskName.setText(currentTask.getTitle());
        taskName1.setText(currentTask.getTitle());
        String project = taskService.getProjectNameByTaskId(currentTaskId);

        if (project != null) {
            projectName.setText(project);
        } else {
            projectName.setText("No Project");
        }
        System.out.println(projectName);
        description.setText(currentTask.getDescription());

        comboStatus.setValue(currentTask.getStatus().name());
        comboPriority.setValue(currentTask.getPriority().name());

        if (currentTask.getDeadline() != null) {
            comboDeadline.setValue(currentTask.getDeadline().toLocalDate());
            deadline.setText(currentTask.getDeadline().format(formatter));
        }

        createTime.setText(TimeUtil.toRelative(currentTask.getCreatedAt()));
        updateTime.setText(TimeUtil.toRelative(currentTask.getUpdatedAt()));

        loadSubTasks();
        loadComments();
    }


    public void createTask() {
        currentTask = taskService.getTaskById(currentTaskId);

        if (currentTask == null) return;

        taskName.setText(currentTask.getTitle());
        taskName1.setText(currentTask.getTitle());
        String project = taskService.getProjectNameByTaskId(currentTaskId);

        if (project != null) {
            projectName.setText(project);
        } else {
            projectName.setText("No Project");
        }
        System.out.println(projectName);
        description.setText(currentTask.getDescription());

        comboStatus.setValue(currentTask.getStatus().name());
        comboPriority.setValue(currentTask.getPriority().name());

        if (currentTask.getDeadline() != null) {
            comboDeadline.setValue(currentTask.getDeadline().toLocalDate());
            deadline.setText(currentTask.getDeadline().format(formatter));
        }

        createTime.setText(TimeUtil.toRelative(currentTask.getCreatedAt()));
        updateTime.setText(TimeUtil.toRelative(currentTask.getUpdatedAt()));

        loadSubTasks();
        loadComments();
    }


    private Task getTask() {
        return taskService.getTaskById(currentTaskId);
    }

    private void updateStatus() {

        if (currentTask == null || comboStatus.getValue() == null) return;

        currentTask.setStatus(TaskStatus.valueOf(comboStatus.getValue()));

        new Thread(() -> taskService.updateTask(currentTask)).start();
    }

    private void updatePriority() {

        if (currentTask == null || comboPriority.getValue() == null) return;

        currentTask.setPriority(Priority.valueOf(comboPriority.getValue()));

        new Thread(() -> taskService.updateTask(currentTask)).start();
    }

    private void updateDeadline() {

        if (currentTask == null || comboDeadline.getValue() == null) return;

        currentTask.setDeadline(comboDeadline.getValue().atStartOfDay());
        deadline.setText(currentTask.getDeadline().format(formatter));

        new Thread(() -> taskService.updateTask(currentTask)).start();
    }


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

            new Thread(() -> {

                taskService.addSubTask(s);

                javafx.application.Platform.runLater(() -> {

                    checkList.getChildren().remove(row);

                    HBox newRow = createSubTaskRow(s);
                    checkList.getChildren().add(newRow);

                    animateSlideIn(newRow);
                    updateProgress();
                    scrollToBottomSubTask();
                });

            }).start();
        });
    }

    private void animateSlideIn(Node node) {

        node.setTranslateY(10);
        node.setOpacity(0);

        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(node.translateYProperty(), 0),
                        new KeyValue(node.opacityProperty(), 1))
        );

        t.play();
    }

    private void scrollToBottomSubTask() {

        Platform.runLater(() -> {
            subTaskScroll.applyCss();
            subTaskScroll.layout();
            subTaskScroll.setVvalue(1.0);
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

        row.setUserData(cb);

        row.setOnMouseEntered(e -> deleteBtn.setOpacity(1));
        row.setOnMouseExited(e -> deleteBtn.setOpacity(0));

        int id = s.getId();

        cb.setOnAction(e -> {

            boolean done = cb.isSelected();


            new Thread(() -> taskService.toggleSubTask(id, done)).start();

            title.setStyle(done
                    ? "-fx-strikethrough: true; -fx-text-fill: #999;"
                    : "");

            updateProgress();
        });

        deleteBtn.setOnAction(e -> {

            new Thread(() -> taskService.deleteSubTask(id)).start();

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


    private void loadComments() {

        List<Comment> list = taskService.getComments(currentTaskId);

        if (list == null) return;


        if (comment.getChildren().size() == list.size()) return;

        comment.getChildren().clear();

        for (Comment c : list) {
            comment.getChildren().add(createCommentItem(c));
        }

        comment.applyCss();
        comment.layout();
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
        content.setMaxWidth(650);
        content.setLineSpacing(2);
        content.setStyle("""
    -fx-font-size: 12px;
    -fx-text-fill: #333;
""");

        VBox box = new VBox(3, header, content);
        box.setMaxWidth(650);
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

        new Thread(() -> {

            boolean ok = taskService.addComment(c);

            Platform.runLater(() -> {

                if (ok) {

                    addComment.clear();


                    List<Comment> list = taskService.getComments(currentTaskId);

                    if (list != null && !list.isEmpty()) {

                        Comment newest = list.get(0); // ORDER BY DESC

                        HBox item = createCommentItem(newest);

                        comment.getChildren().add(0, item);

                        animateFade(item);
                        scrollToTop();
                    }
                }

            });

        }).start();
    }

    private void animateFade(Node node) {
        node.setOpacity(0);

        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setToValue(1);
        ft.play();
    }

    private void scrollToTop() {

        Platform.runLater(() -> {
            commentScroll.applyCss();
            commentScroll.layout();
            commentScroll.setVvalue(0);
        });
    }

    private void animateNewComments() {

        for (Node node : comment.getChildren()) {

            node.setOpacity(0);

            FadeTransition ft = new FadeTransition(Duration.millis(200), node);
            ft.setToValue(1);
            ft.play();
        }
    }


    //convert
    @FXML
    void convert_allMyProject(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.ALL_MY_PROJECT);

    }

    @FXML
    void convert_mainProject(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW, currentTask.getProjectId());
    }

    @FXML
    void convert_taskDetail(ActionEvent event) {

    }


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
    @FXML
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

    @FXML private ScrollPane commentScroll;
    @FXML private ScrollPane subTaskScroll;
    @FXML
    private Hyperlink taskName1;
    @FXML
    private Hyperlink projectName;
    @FXML
    private Hyperlink dashborad;
    @FXML
    private Label dashboard1;

}