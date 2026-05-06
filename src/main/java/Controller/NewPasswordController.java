package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class NewPasswordController {

    @FXML private StackPane rootStack;

    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordVisible;
    @FXML private ImageView newEyeClose;
    @FXML private ImageView newEyeOpen;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisible;
    @FXML private ImageView confirmEyeClose;
    @FXML private ImageView confirmEyeOpen;

    @FXML private Label strengthLabel;
    @FXML private Label matchLabel;
    @FXML private Button updateButton;

    // Khai báo Service xử lý Database
    private final ForgotPasswordService forgotService = new ForgotPasswordService();

    @FXML
    public void initialize() {
        newPasswordVisible.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordVisible.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    void showNewPassword() {
        newPasswordField.setVisible(false);
        newPasswordVisible.setVisible(true);
        newEyeClose.setVisible(false);
        newEyeOpen.setVisible(true);
    }

    @FXML
    void hideNewPassword() {
        newPasswordField.setVisible(true);
        newPasswordVisible.setVisible(false);
        newEyeClose.setVisible(true);
        newEyeOpen.setVisible(false);
    }

    @FXML
    void showConfirmPassword() {
        confirmPasswordField.setVisible(false);
        confirmPasswordVisible.setVisible(true);
        confirmEyeClose.setVisible(false);
        confirmEyeOpen.setVisible(true);
    }

    @FXML
    void hideConfirmPassword() {
        confirmPasswordField.setVisible(true);
        confirmPasswordVisible.setVisible(false);
        confirmEyeClose.setVisible(true);
        confirmEyeOpen.setVisible(false);
    }

    @FXML
    void handleBack(ActionEvent event) {
        DialogManager.getInstance().close(rootStack);
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        String pass1 = newPasswordField.getText();
        String pass2 = confirmPasswordField.getText();

        matchLabel.setVisible(false);
        strengthLabel.setStyle("-fx-text-fill: #a0aec0;");

        // Validate 1: Độ dài tối thiểu
        if (pass1.length() < 6) {
            strengthLabel.setText("Mật khẩu phải từ 6 ký tự trở lên!");
            strengthLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validate 2: 2 ô mật khẩu phải khớp nhau
        if (!pass1.equals(pass2)) {
            matchLabel.setText("Mật khẩu xác nhận không khớp.");
            matchLabel.setStyle("-fx-text-fill: red;");
            matchLabel.setVisible(true);
            return;
        }

        updateButton.setDisable(true);
        updateButton.setText("Đang cập nhật...");

        String currentEmail = ResetPasswordContext.getEmail();

        boolean isSuccess = forgotService.resetPassword(currentEmail, pass1);

        if (isSuccess) {
            System.out.println("Đổi mật khẩu thành công!");

            // Xóa cache email và đóng tất cả cửa sổ Dialog
            ResetPasswordContext.clear();
            DialogManager.getInstance().closeAll();

        } else {
            // Hiển thị lỗi nếu update DB thất bại
            matchLabel.setText("Lỗi hệ thống. Không thể cập nhật mật khẩu.");
            matchLabel.setStyle("-fx-text-fill: red;");
            matchLabel.setVisible(true);

            // Mở lại nút bấm
            updateButton.setDisable(false);
            updateButton.setText("Cập nhật mật khẩu");
        }
    }
}