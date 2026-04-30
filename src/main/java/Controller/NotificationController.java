package Controller;

import DAO.ProjectDAO;
import DAO.TaskDAO;
import DAO.UserDAO;
import Model.NotificationDTO;
import Service.NotificationService;
import Service.ProjectService;
import Service.TaskService;
import Service.UserService;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
import Utils.UserSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import Enum.Screen;

import java.util.List;
import java.util.stream.Collectors;

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
        notifications = notificationService.getNotificationDTOByUserId(currentUserId);
        showNotifications(notifications);
        updateCounter();
    }

    public void refreshNotifications() {
        loadNotifications();
    }

    // ================= VIEW =================
    private void showNotifications(List<NotificationDTO> list) {
        list_notification.setItems(FXCollections.observableArrayList(list));
    }


    private void openTaskDetail(int taskId) {
        UserSession.setCurrentTaskId(taskId);

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

        UserSession.setCurrentProjectId(projectId);

        ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW);
    }

    // ================= COUNTER =================
    private void updateCounter() {
        number_all.setText(String.valueOf(notificationService.countAll(currentUserId)));
        number_unread.setText(String.valueOf(notificationService.countUnread(currentUserId)));
    }

    // ================= ACTIONS =================
    private void setupActions() {

        all_list.setOnMouseClicked(e ->
                showNotifications(notifications)
        );

        unread_list.setOnMouseClicked(e -> {

            List<NotificationDTO> unread = notifications.stream()
                    .filter(n -> !n.isRead())
                    .collect(Collectors.toList());

            showNotifications(unread);
        });

        tick_all.setOnAction(e -> {
            notificationService.markAllAsRead(currentUserId);
            loadNotifications();
        });
    }
}