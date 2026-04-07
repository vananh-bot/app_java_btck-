package Test;

import Controller.MainProjectController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/project/mainProjectView.fxml")
            );

            Scene scene = new Scene(loader.load());

            // 🔥 LẤY CONTROLLER
            MainProjectController controller = loader.getController();

            // 🔥 GỌI INIT (QUAN TRỌNG NHẤT)
            controller.init(1); // truyền projectId

            stage.setTitle("Main Project");
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