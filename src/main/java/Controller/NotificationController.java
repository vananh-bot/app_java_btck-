package Controller;

import DAO.ProjectDAO;
import DAO.TaskDAO;
import DAO.UserDAO;
import DTO.NotificationDTO;
import Service.NotificationService;
import Service.ProjectService;
import Service.TaskService;
import Service.UserService;
import Utils.ScreenManager;
import Utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import Enum.Screen;

import java.util.List;

public class NotificationController {

    @FXML
    private ListView<NotificationDTO> list_notification;

    @FXML
    private Label number_all;

    @FXML
    private Label number_unread;

    @FXML
    private HBox all_list;

    @FXML
    private HBox unread_list;

    private List<HBox> tabs;

    @FXML
    private Button tick_all;
    @FXML
    private ProgressIndicator loading;

    private NotificationService notificationService;
    private ProjectService projectService;
    private TaskService taskService;
    private UserService userService;

    private List<NotificationDTO> notifications;

    private final int currentUserId = UserSession.getUserId();

    private void setActive(HBox selectedTab) {
        // remove active của tất cả
        for (HBox tab : tabs) {
            tab.getStyleClass().remove("active");
        }

        // add active cho tab được chọn
        if (!selectedTab.getStyleClass().contains("active")) {
            selectedTab.getStyleClass().add("active");
        }
    }

    @FXML
    public void initialize() {
        initServices();
        setupListView();

        tabs = List.of(all_list, unread_list);

        setupTabActions();

        loadNotifications();
        setActive(all_list);
    }

    private void setupTabActions() {

        all_list.setOnMouseClicked(e -> {
            setActive(all_list);                  // đổi UI
            showNotifications(notifications);     // show all
        });

        unread_list.setOnMouseClicked(e -> {
            setActive(unread_list);               // đổi UI

            List<NotificationDTO> unread = notifications.stream()
                    .filter(n -> !n.isRead())
                    .toList();

            showNotifications(unread);            // filter
        });

        tick_all.setOnAction(e -> {
            notificationService.markAllAsRead(currentUserId);
            notifications.forEach(n -> n.setRead(true));

            showNotifications(notifications);
            updateCounter(notifications.size(), 0);
        });
    }

    // ================= INIT =================
    private void initServices() {
        notificationService = new NotificationService();
        projectService = new ProjectService(new ProjectDAO());
        taskService = new TaskService(new TaskDAO());
        userService = new UserService(new UserDAO());
    }

    // ================= LOAD =================
    private void loadNotifications() {
        showLoading(true);

        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>(){
            List<NotificationDTO> list;
            int total;
            int unread;

            @Override
            protected Void call(){
                list = notificationService.getNotificationDTOByUserId(currentUserId);
                total = notificationService.countAll(currentUserId);
                unread = notificationService.countUnread(currentUserId);
                return null;
            }

            @Override
            protected void succeeded() {
                notifications = list;

                showNotifications(list);

                updateCounter(total, unread);

                showLoading(false);
            }
        };

        new Thread(task).start();

    }

    // ================= VIEW =================
    private void showNotifications(List<NotificationDTO> list) {
        if (list == null) list = List.of();
        list_notification.setItems(FXCollections.observableArrayList(list));
    }

    private void showLoading(boolean b){
        loading.setVisible(b);
        loading.setManaged(b);
        loading.setProgress(-1);

        list_notification.setDisable(b);
        tick_all.setDisable(b);
        all_list.setDisable(b);
        unread_list.setDisable(b);
    }

    private void openTaskDetail(int taskId) {
        ScreenManager.getInstance().show(Screen.TASK_DETAILS, taskId);
    }

    // ================= LIST CELL =================
    private void setupListView() {

        list_notification.setCellFactory(param -> {

            NotificationCell cell = new NotificationCell(
                    notificationService,
                    projectService,
                    taskService,
                    userService
            );

            // CLICK PROJECT → chuyển màn hình
            cell.setOpenProjectHandler(this::openProject);

            // CLICK ROOT ITEM → mở task detail
            cell.setOpenTaskHandler(this::openTaskDetail);

            return cell;
        });
    }

    // ================= OPEN PROJECT =================
    private void openProject(int projectId) {
        ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW, projectId);
    }

    // ================= COUNTER =================
    private void updateCounter(int total, int unread) {
        number_all.setText(String.valueOf(total));
        number_unread.setText(String.valueOf(unread));
    }
}