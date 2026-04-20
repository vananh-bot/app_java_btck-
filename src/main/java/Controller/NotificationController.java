package Controller;

import Service.NotificationService;
import javafx.fxml.FXML;

public class NotificationController {

    private NotificationService notificationService;

    @FXML
    private void initialize() {
        // chỉ init UI
    }

    public void setNotificationService(NotificationService service) {
        this.notificationService = service;
    }

    public void loadNotifications() {
        // gọi service ở đây
    }
}