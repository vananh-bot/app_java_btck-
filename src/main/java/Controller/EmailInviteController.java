package Controller;

import Service.InviteService;
import DAO.EmailInviteDAO;
import DAO.InviteLinkDAO;
import DAO.JoinRequestDAO;
import DAO.UserProjectDAO;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class EmailInviteController {
    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnSend,close;

    @FXML
    private Hyperlink linkBack;

    @FXML
    private Label lblMessage;

    // 2. Khai báo Service xử lý logic
    private InviteService inviteService;

    // Giả sử Project ID hiện tại đang là 1
    private int currentProjectId = 1;

    @FXML
    public void initialize() {
        // Khởi tạo các DAO và truyền vào InviteService
        inviteService = new InviteService(
                new InviteLinkDAO(),
                new EmailInviteDAO(),
                new JoinRequestDAO(),
                new UserProjectDAO()
        );

        // Bắt sự kiện khi người dùng click vào nút Gửi
        btnSend.setOnAction(event -> handleSendEmail());

        // Bắt sự kiện khi người dùng click vào nút Quay lại
        linkBack.setOnAction(event -> handleBackToLinkMode());
    }

    private void showInlineMessage(String message, boolean isSuccess) {
        lblMessage.setText(message);

        // Đổi màu chữ tùy theo thành công hay thất bại
        if (isSuccess) {
            lblMessage.setStyle("-fx-text-fill: #007700; -fx-font-size: 13px; -fx-font-weight: bold;"); // Màu xanh lá
        } else {
            lblMessage.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 13px; -fx-font-weight: bold;"); // Màu đỏ
        }

        // Tự động xóa dòng chữ sau 3 giây để giao diện gọn gàng lại
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> lblMessage.setText(""));
        pause.play();
    }

    // Hàm xử lý gửi email đã được cập nhật
    // Hàm xử lý gửi email cực gọn (Không cần ô nhập Tên nữa)
    private void handleSendEmail() {
        String email = txtEmail.getText().trim();

        // 1. Kiểm tra rỗng cho Email
        if (email.isEmpty()) {
            showInlineMessage("Vui lòng nhập email của cộng sự!", false);
            return;
        }

        // 2. Kiểm tra định dạng Email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showInlineMessage("Định dạng email không hợp lệ!", false);
            return;
        }

        // 3. TỰ ĐỘNG LẤY TÊN TỪ EMAIL (Cắt toàn bộ ký tự trước dấu @)
        String name = email.substring(0, email.indexOf("@"));

        // Nếu bạn muốn tên đẹp hơn một chút, ví dụ viết hoa chữ cái đầu:
        // name = name.substring(0, 1).toUpperCase() + name.substring(1);

        try {
            btnSend.setText("Đang gửi...");
            btnSend.setDisable(true);

            // 4. Xử lý gửi: Truyền cái tên vừa cắt được vào hàm
            inviteService.inviteByEmail(currentProjectId, email, name);

            // Hiện thông báo xanh
            showInlineMessage("Đã gửi lời mời thành công đến " + email, true);
            txtEmail.clear(); // Chỉ cần xóa ô email

        } catch (Exception e) {
            e.printStackTrace();
            showInlineMessage(e.getMessage(), false);
        } finally {
            btnSend.setText("Gửi lời mời");
            btnSend.setDisable(false);
        }
    }

    // Hàm xử lý quay lại màn hình Share Link
    private void handleBackToLinkMode() {
        System.out.println("Đang quay lại màn hình chia sẻ liên kết...");
        // TODO: Gọi logic đổi Scene của JavaFX ở đây
    }
    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        // Lấy Stage (cửa sổ) hiện tại và đóng nó lại
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}