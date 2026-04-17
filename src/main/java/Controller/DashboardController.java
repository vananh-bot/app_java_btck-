package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private Button buttonCreateProject;

    @FXML
    private Label deadline;

    @FXML
    private Label description;

    @FXML
    private Label invitation;

    @FXML
    private HBox listActiveProject;

    @FXML
    private VBox listNotification;

    @FXML
    private VBox listTask;

    @FXML
    private TextField searchBar;

    @FXML
    private Label showAllTask;

    @FXML
    private Label taskAssinged;

    @FXML
    private Label welcome;

    @FXML
    void initialize(){

    }

    void load(){

    }
}
