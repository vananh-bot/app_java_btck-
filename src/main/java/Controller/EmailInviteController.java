package Controller;

import Service.InviteService;
import DAO.EmailInviteDAO;
import DAO.InviteLinkDAO;
import DAO.JoinRequestDAO;
import DAO.UserProjectDAO;

import Utils.DialogManager;
import Utils.UserSession;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import Enum.Screen;

public class EmailInviteController {
    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnSend,close;

    @FXML
    private StackPane overlay;
    @FXML
    private Label notification;
    @FXML
    private ProgressIndicator loading;

    // 2. Khai báo Service xử lý logic
    private InviteService inviteService;


    @FXML
    public void initialize() {
        // Khởi tạo các DAO và truyền vào InviteService
        inviteService = new InviteService(
                new InviteLinkDAO(),
                new EmailInviteDAO(),
                new JoinRequestDAO(),
                new UserProjectDAO()
        );
        loading.setVisible(false);

        // Bắt sự kiện khi người dùng click vào nút Gửi
        btnSend.setOnAction(event -> handleSendEmail());

    }

    private void showInlineMessage(String message, boolean isSuccess) {
        notification.setText(message);

        // Đổi màu chữ tùy theo thành công hay thất bại
        if (isSuccess) {
            notification.setStyle("-fx-font-family: \"Segoe UI\", Arial, sans-serif; -fx-text-fill: #007700; -fx-font-size: 16px; -fx-font-weight: bold;"); // Màu xanh lá
        } else {
            notification.setStyle("-fx-font-family: \"Segoe UI\", Arial, sans-serif; -fx-text-fill: #FF0000; -fx-font-size: 16px; -fx-font-weight: bold;"); // Màu đỏ
        }

        // Tự động xóa dòng chữ sau 3 giây để giao diện gọn gàng lại
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> notification.setText(""));
        pause.play();
    }

    // Hàm xử lý gửi email đã được cập nhật
    // Hàm xử lý gửi email cực gọn (Không cần ô nhập Tên nữa)
    private void handleSendEmail() {
        String email = txtEmail.getText().trim();
        int currentProjectId = UserSession.getCurrentProjectId();
        if (currentProjectId == -1) {
            showInlineMessage("Bạn chưa chọn dự án!", false);
            return;
        }

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

        loading.setVisible(true);
        loading.setProgress(-1);
        btnSend.setText("");
        btnSend.setDisable(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                inviteService.inviteByEmail(currentProjectId, email, name);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            btnSend.setText("Gửi lời mời");
            btnSend.setDisable(false);

            showInlineMessage("Đã gửi lời mời đến " + email, true);
            txtEmail.clear();
        });

        task.setOnFailed(e -> {
            loading.setVisible(false);
            btnSend.setText("Gửi lời mời");
            btnSend.setDisable(false);

            Throwable ex = task.getException();
            ex.printStackTrace(); // in lỗi thật

            showInlineMessage(ex.getMessage(), false); // hiển thị lỗi thật
        });
        new Thread(task).start();
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }
}