package Test;

import Model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            User testUser = new User();
            testUser.setId(33);
            testUser.setName("Uyenuyen");
            Utils.UserSession.login(testUser);
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/project/createProject.fxml")
            );

            Scene scene = new Scene(loader.load());

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}