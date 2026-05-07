package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML
    private ProgressIndicator loading;

    // Khai báo Service xử lý Database
    private final ForgotPasswordService forgotService = new ForgotPasswordService();

    @FXML
    public void initialize() {
        showLoading(false);
        newEyeClose.setFocusTraversable(false);
        newEyeOpen.setFocusTraversable(false);

        confirmEyeClose.setFocusTraversable(false);
        confirmEyeOpen.setFocusTraversable(false);
        newPasswordVisible.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordVisible.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    void showNewPassword() {
        newPasswordField.setVisible(false);
        newPasswordVisible.setVisible(true);
        newEyeClose.setVisible(false);
        newEyeOpen.setVisible(true);
        newPasswordVisible.requestFocus();
        newPasswordVisible.positionCaret(newPasswordVisible.getText().length());
    }

    @FXML
    void hideNewPassword() {
        newPasswordField.setVisible(true);
        newPasswordVisible.setVisible(false);
        newEyeClose.setVisible(true);
        newEyeOpen.setVisible(false);
        newPasswordField.requestFocus();
        newPasswordField.positionCaret(newPasswordField.getText().length());
    }

    @FXML
    void showConfirmPassword() {
        confirmPasswordField.setVisible(false);
        confirmPasswordVisible.setVisible(true);
        confirmEyeClose.setVisible(false);
        confirmEyeOpen.setVisible(true);
        confirmPasswordVisible.requestFocus();
        confirmPasswordVisible.positionCaret(confirmPasswordVisible.getText().length());
    }

    @FXML
    void hideConfirmPassword() {
        confirmPasswordField.setVisible(true);
        confirmPasswordVisible.setVisible(false);
        confirmEyeClose.setVisible(true);
        confirmEyeOpen.setVisible(false);
        confirmPasswordField.requestFocus();
        confirmPasswordField.positionCaret(confirmPasswordField.getText().length());
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
        showLoading(true);
        String currentEmail = ResetPasswordContext.getEmail();

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return forgotService.resetPassword(currentEmail, pass1);
            }
        };

        task.setOnSucceeded(e -> {
            if(task.getValue()) {
                ResetPasswordContext.clear();
                DialogManager.getInstance().closeAll();
            } else {
                matchLabel.setText("Lỗi hệ thống. Không thể cập nhật mật khẩu.");
                matchLabel.setStyle("-fx-text-fill: red;");
                matchLabel.setVisible(true);
            }
            showLoading(false);
        });
        task.setOnFailed(e -> {
            showLoading(false);
            matchLabel.setText("Có lỗi xảy ra!");
            matchLabel.setStyle("-fx-text-fill: red;");
            matchLabel.setVisible(true);

            task.getException().printStackTrace();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showLoading(boolean b){
        loading.setManaged(b);
        loading.setProgress(-1);
        loading.setVisible(b);
        if(b) updateButton.setText("");
        else updateButton.setText("Cập nhật mật khẩu");
    }
}