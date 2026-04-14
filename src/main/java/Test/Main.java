package Test;

import Controller.TaskController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/task/taskdetails.fxml")
            );

            Scene scene = new Scene(loader.load());
            TaskController controller = loader.getController();

            controller.loadTask(1);

            stage.setTitle("Task Detail");
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