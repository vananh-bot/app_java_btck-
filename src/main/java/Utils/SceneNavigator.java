package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SceneNavigator {
    // Các đường dẫn FXML cố định
    public static final String LOGIN = "/auth/login.fxml";
    public static final String REGISTER = "/auth/register.fxml";
    public static final String DASHBOARD = "/dashboard/general_dashboard_view2.fxml"; // Đường dẫn file Dashboard mới merge
    public static final String ALL_PROJECTS = "/project/AllMyProjectView.fxml";
    public static final String NOTIFICATION = "/notification/notification.fxml";
    public static final String MAIN_PROJECT_VIEW = "/project/mainProjectView.fxml";
    public static final String TASK_DETAILS = "/task/taskdetails.fxml";
    public static final String CREATE_TASK = "/task/createTask.fxml";

    public static void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FlowTask - " + title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Lỗi chuyển màn hình sang " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}