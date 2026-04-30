package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SceneNavigator {
    // Các đường dẫn FXML cố định
    public static final String LOGIN = "/auth/login.fxml";
    public static final String REGISTER = "/auth/register.fxml";
    public static final String DASHBOARD = "/layout/layoutDashboard.fxml"; // Đường dẫn file Dashboard mới merge
    public static final String ALL_PROJECTS = "/layout/layoutAllMyProjectView.fxml";
    public static final String NOTIFICATION = "/notification/notification.fxml";
    public static final String MAIN_PROJECT_VIEW = "/layout/layoutMainProjectView.fxml";
    public static final String TASK_DETAILS = "/layout/layoutTaskDetails.fxml";
    public static final String CREATE_TASK = "/task/createTask.fxml";
    public static final String CREATE_PROJECT = "/project/createProject.fxml";

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

    public static <T> T switchSceneWithController(String fxmlPath, String title) {
        try {
            var url = SceneNavigator.class.getResource(fxmlPath);

            if (url == null) {
                throw new RuntimeException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("FlowTask - " + title);
            stage.centerOnScreen();
            stage.show();

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T loadIntoCenter(String fxmlPath, StackPane contentArea) {
        try {
            var url = SceneNavigator.class.getResource(fxmlPath);

            if (url == null) {
                throw new RuntimeException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            contentArea.getChildren().setAll(view);

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

