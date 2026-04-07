package Test;

import Controller.MainProjectController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Chỉ định đường dẫn tới FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/project/mainProjectView.fxml")
            );

            // 2. Load giao diện vào một đối tượng Parent
            Parent root = loader.load();

            // 3. LẤY CONTROLLER VÀ GỌI INIT (Đúng chuẩn)
            MainProjectController controller = loader.getController();
            controller.init(1); // Test với projectId = 1

            // 4. Đưa root vào Scene
            Scene scene = new Scene(root);

            // 5. Cấu hình cửa sổ (Stage)
            stage.setTitle("Test FlowTask - Main Project");
            stage.setScene(scene);
            stage.centerOnScreen(); // Mở app ra sẽ nằm ngay giữa màn hình
            stage.show();

        } catch (Exception e) {
            System.err.println(" Lỗi khi load giao diện! Hãy kiểm tra lại đường dẫn FXML.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Nên truyền args vào launch để JavaFX nhận các tham số khởi chạy (nếu có)
        launch(args);
    }
}