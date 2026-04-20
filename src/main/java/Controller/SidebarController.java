package Controller;

import Utils.SceneNavigator;
import Utils.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import Enum.Screen;

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

    private MainLayoutController main;

    public void setMain(MainLayoutController main) {
        this.main = main;
    }
    public void initialize(){
        btndashboard.getStyleClass().add("button_active");

        ScreenManager.getInstance().setSidebarController(this);
    }

    public void setActive(Button button){
        if(currentButton != null){
            currentButton.getStyleClass().remove("button_active");
        }

        button.getStyleClass().add("button_active");
        currentButton = button;
    }
    public void setActive1(Screen screen){

        btndashboard.getStyleClass().remove("button_active");
        btnproject.getStyleClass().remove("button_active");
        btnnotification.getStyleClass().remove("button_active");

        switch (screen){

            case DASHBOARD ->
                    btndashboard.getStyleClass().add("button_active");

            case ALL_MY_PROJECT,
                 TASK_DETAILS,
                 MAIN_PROJECT_VIEW ->
                    btnproject.getStyleClass().add("button_active");

            case NOTIFICATION ->
                    btnnotification.getStyleClass().add("button_active");
        }
    }
    @FXML
    void goToAllProject(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.ALL_MY_PROJECT);
    }

    @FXML
    void goToDashboard(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.DASHBOARD);
    }

    @FXML
    void goToNotification(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.NOTIFICATION);
    }

    @FXML
    void goToLogin(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, SceneNavigator.LOGIN, "Đăng nhập");
    }
}
