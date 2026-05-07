package Utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.SQLException;

public class AppErrorHandler {

    private static Stage offlineStage = null;

    public static void handle(Exception e) {
        // Nếu đúng là lỗi mất mạng do JDBCUtil ném ra
        if (e instanceof SQLException && "NO_INTERNET".equals(e.getMessage())) {
            showOfflinePopup();
            return;
        }

        // Các lỗi khác (sai cú pháp SQL, NullPointer...) thì cứ in ra để dev sửa
        e.printStackTrace();
    }

    public static void showOfflinePopup() {
        // Chạy trên luồng UI của JavaFX
        Platform.runLater(() -> {
            try {
                // Nếu đã hiện rồi thì không mở thêm
                if (offlineStage != null && offlineStage.isShowing()) return;

                // CHÚ Ý: Sửa đường dẫn này đúng với thư mục chứa offline.fxml của bạn
                FXMLLoader loader = new FXMLLoader(AppErrorHandler.class.getResource("/lossinternet.fxml"));
                Parent root = loader.load();

                offlineStage = new Stage();
                // Khóa tương tác với app chính (FlowTask) khi đang hiện popup
                offlineStage.initModality(Modality.APPLICATION_MODAL);
                offlineStage.initStyle(StageStyle.UNDECORATED); // Bỏ viền cửa sổ
                offlineStage.setScene(new Scene(root));
                offlineStage.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void closeOfflinePopup() {
        Platform.runLater(() -> {
            if (offlineStage != null && offlineStage.isShowing()) {
                offlineStage.close();
            }
        });
    }
}