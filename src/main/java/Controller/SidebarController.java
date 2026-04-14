package Controller;

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
    }

    public void setActive(Button button){
        resetAll();
        button.getStyleClass().add("button_active");
        currentButton = button;
    }
    @FXML
    void goToAllProject(ActionEvent event) {
        setActive(btnproject);
    }

    @FXML
    void goToDashboard(ActionEvent event) {
        setActive(btndashboard);
    }

    @FXML
    void goToNotification(ActionEvent event) {
        setActive(btnnotification);
    }

    @FXML
    void goToLogin(ActionEvent event) {
        setActive(btnlogout);
    }

    public void resetAll(){
        btndashboard.getStyleClass().remove("button_active");
        btnnotification.getStyleClass().remove("button_active");
        btnproject.getStyleClass().remove("button_active");
        btnlogout.getStyleClass().remove("button_active");
    }

}
