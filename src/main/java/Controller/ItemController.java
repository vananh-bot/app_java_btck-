package Controller;

import Model.NotificationDTO;
import Service.NotificationService;
import Service.ProjectService;
import Service.TaskService;
import Utils.TimeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class ItemController {

    @FXML
    private StackPane backgroud_image;

    @FXML
    private Button btnProject;

    @FXML
    private Circle check_read;

    @FXML
    private ImageView image;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblTitle;

    @FXML
    private HBox rootItem;

    @FXML
    private VBox unreadDot;

    private NotificationDTO notification;

    private NotificationService notificationService;
    private ProjectService projectService;
    private TaskService taskService;

    // ===== CALLBACK MỞ PROJECT =====
    private java.util.function.Consumer<Integer> openProjectHandler;

    public void setOpenProjectHandler(java.util.function.Consumer<Integer> handler) {
        this.openProjectHandler = handler;
    }

    private static final Font BOLD =
            Font.loadFont(ItemController.class.getResourceAsStream("/fonts/Inter_18pt-Bold.ttf"), 13);

    private static final Font REG =
            Font.loadFont(ItemController.class.getResourceAsStream("/fonts/Inter_18pt-Medium.ttf"), 12);

    @FXML
    public void initialize() {

        rootItem.setOnMouseClicked(e -> {
            if (notification != null && !notification.isRead()) {
                notificationService.markAsRead(notification.getId());
                notification.setRead(true);
                updateReadStatus();
            }

            if (notification != null && notification.getTaskId() != null) {
                if (openTaskHandler != null) {
                    openTaskHandler.accept(notification.getTaskId());
                }
            }
        });
    }

    // ================= SERVICES =================
    public void setServices(NotificationService ns,
                            ProjectService ps,
                            TaskService ts) {
        this.notificationService = ns;
        this.projectService = ps;
        this.taskService = ts;
    }

    // ================= SET DATA =================
    public void setData(NotificationDTO n) {
        this.notification = n;

        applyTypeStyle(n);

        // ===== TITLE =====
        switch (n.getType()) {
            case COMMENT -> lblTitle.setText("Có bình luận mới trong task");
            case DEADLINE -> lblTitle.setText("Deadline sắp đến");
        }
        lblTitle.setFont(BOLD);

        // ===== MESSAGE =====
        String msg = switch (n.getType()) {
            case COMMENT -> n.getCreatorName()
                    + " đã bình luận trong task \"" + n.getTaskTitle() + "\"";

            case DEADLINE -> "Task \"" + n.getTaskTitle() + "\" sắp đến hạn";

        };

        lblMessage.setText(msg);
        lblMessage.setFont(REG);

        // ===== PROJECT BUTTON =====
        if (n.getProjectId() != null) {
            btnProject.setVisible(true);

            String name = n.getProjectName();
            if (name == null) {
                name = projectService.getProjectName(n.getProjectId());
            }

            btnProject.setText(name);
        } else {
            btnProject.setVisible(false);
            btnProject.setManaged(false);
        }

        // ===== TIME =====
        lblTime.setText(TimeUtil.toRelative(n.getCreatedAt()));

        updateReadStatus();
    }

    // ================= STYLE =================
    private void applyTypeStyle(NotificationDTO n) {

        String path = switch (n.getType()) {
            case COMMENT -> "/images/comment.png";
            case DEADLINE -> "/images/timer.png";
        };

        image.setImage(new Image(getClass().getResourceAsStream(path)));

        backgroud_image.getStyleClass().removeAll("purple_bg", "yellow_bg", "green_bg");
        btnProject.getStyleClass().removeAll("purple_text", "yellow_text", "green_text");
        image.getStyleClass().removeAll("icon_comment", "icon_deadline", "icon_join");

        switch (n.getType()) {
            case COMMENT -> {
                backgroud_image.getStyleClass().add("purple_bg");
                btnProject.getStyleClass().add("purple_text");
                image.getStyleClass().add("icon_comment");
            }
            case DEADLINE -> {
                backgroud_image.getStyleClass().add("yellow_bg");
                btnProject.getStyleClass().add("yellow_text");
                image.getStyleClass().add("icon_deadline");
            }
        }
    }

    // ================= CLICK OPEN PROJECT =================
    @FXML
    private void handleOpenProject() {
        if (notification != null && notification.getProjectId() != null) {

            if (openProjectHandler != null) {
                openProjectHandler.accept(notification.getProjectId());
            }
        }
    }

    // ================= READ STATUS =================
    private void updateReadStatus() {
        boolean unread = !notification.isRead();

        check_read.setVisible(unread);
        unreadDot.setVisible(unread);
        unreadDot.setManaged(unread);
    }

    private java.util.function.Consumer<Integer> openTaskHandler;

    public void setOpenTaskHandler(java.util.function.Consumer<Integer> handler) {
        this.openTaskHandler = handler;
    }
}