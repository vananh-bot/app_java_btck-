package Controller;

import Utils.SceneNavigator;
import Utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DaskBoardController {
    @FXML
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