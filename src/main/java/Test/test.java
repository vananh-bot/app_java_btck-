package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controller.MemberListController; // Đảm bảo đường dẫn tới Controller chính xác

public class test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Chỉ định đường dẫn tới file FXML của màn hình Thành viên
        // Lưu ý: Kiểm tra lại đường dẫn file của bạn có đúng là /layout/... không
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/invite/MemberList.fxml"));

        // 2. PHẢI GỌI LOAD TRƯỚC khi lấy Controller
        Parent root = loader.load();

        // 3. LẤY CONTROLLER RA
        MemberListController controller = loader.getController();

        // 4. TRUYỀN ID ẢO LÀ 1 ĐỂ CHẠY THỬ
        if (controller != null) {
            controller.setProjectInfo(1);
            System.out.println("Đã truyền Project ID = 1 vào Controller thành công.");
        } else {
            System.err.println("Lỗi: Không tìm thấy MemberListController. Hãy kiểm tra fx:controller trong file FXML.");
        }

        // 5. Thiết lập Stage và hiển thị
        primaryStage.setTitle("Chương trình chạy thử: Danh sách thành viên");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}