package Test;

import Service.SchedulerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/auth/login.fxml")
            );

            Scene scene = new Scene(loader.load());

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            SchedulerService.getInstance().startDeadlineScheduler();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // ================= STOP SCHEDULER =================
        SchedulerService.getInstance().stopScheduler();
    }

    public static void main(String[] args) {
        launch();
    }
}