package Controller;

import Utils.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class SidebarController {
    @FXML
    private Button btndashboard;

    @FXML
    private Button btnlogout;

    @FXML
    private Button btnnotification;

    @FXML
    private Button btnproject;

    private Button currentButton;

    public void initialize(){
        btndashboard.getStyleClass().add("button_active");
        currentButton = btndashboard;
    }

    public void setActive(Button button){
        if(currentButton != null){
            currentButton.getStyleClass().remove("button_active");
        }

        button.getStyleClass().add("button_active");
        currentButton = button;
    }
    @FXML
    void goToAllProject(ActionEvent event) {
        setActive(btnproject);
        Utils.SceneNavigator.switchScene(event, SceneNavigator.ALL_PROJECTS, "Tất cả dự án");
    }

    @FXML
    void goToDashboard(ActionEvent event) {
        setActive(btndashboard);
        Utils.SceneNavigator.switchScene(event, SceneNavigator.DASHBOARD, "Tổng quan");
    }

    @FXML
    void goToNotification(ActionEvent event) {
        setActive(btnnotification);
        //Utils.SceneNavigator.switchScene(event, SceneNavigator.NOTIFICATION, "Thông báo");
    }

    @FXML
    void goToLogin(ActionEvent event) {
        setActive(btnlogout);
        Utils.SceneNavigator.switchScene(event, SceneNavigator.LOGIN, "Đăng nhập");
    }
}
